package org.example1.dfPubBin.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.rare.RareItemManager;

import java.util.List;
import java.util.ArrayList;

/**
 * 掉落物清理任务
 * 定期清理服务器中的掉落物，将稀有或有价值的物品放入公共垃圾桶，清理普通物品
 */
public class DroppedItemCleanupTask extends BukkitRunnable {

    private final DfPubBinPlugin plugin;
    private final List<java.util.UUID> processedItems = new ArrayList<>();
    private boolean rareItemFoundInCurrentRun = false; // 用于跟踪本次任务执行中是否发现了稀有物品

    public DroppedItemCleanupTask(DfPubBinPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // 重置稀有物品发现标志
        rareItemFoundInCurrentRun = false;
        processDroppedItems();
        // 如果本次任务执行中发现了稀有物品，则广播一次
        if (rareItemFoundInCurrentRun) {
            RareItemManager.broadcastRareItem("public");
        }
    }

    /**
     * 处理掉落物：将所有掉落物都放入垃圾桶
     */
    private void processDroppedItems() {
        // 创建一个临时列表，记录本次处理任务中处理的物品ID
        List<java.util.UUID> itemsToRemove = new ArrayList<>();
        
        // 遍历所有世界
        for (World world : Bukkit.getWorlds()) {
            // 遍历世界中的所有实体
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item item) {
                    // 检查是否已经处理过此物品（在本次任务执行中）
                    if (processedItems.contains(item.getUniqueId())) {
                        continue;
                    }

                    ItemStack itemStack = item.getItemStack();
                    
                    // 将所有物品添加到公共垃圾桶
                    addToGarbage(itemStack);
                    
                    // 标记为已处理，避免在本次任务执行中重复处理
                    processedItems.add(item.getUniqueId());
                    
                    // 移除原掉落物
                    item.remove();
                    
                    // 记录这个物品，以便稍后从processedItems中移除
                    itemsToRemove.add(item.getUniqueId());
                }
            }
        }
        
        // 从processedItems中移除已处理的物品，以便下次执行时可以处理新生成的掉落物
        processedItems.removeAll(itemsToRemove);
    }

    /**
     * 将物品添加到垃圾桶
     */
    private void addToGarbage(ItemStack itemStack) {
        // 获取公共垃圾桶容器
        GarbageContainer publicContainer = GarbageManager.getPublicContainer();
        
        // 检查添加的物品是否为稀有物品
        boolean isRareItem = RareItemManager.isRare(itemStack);
        
        // 如果是稀有物品，设置标志
        if (isRareItem) {
            rareItemFoundInCurrentRun = true;
        }
        
        // 查找最佳放置位置
        if (findAndAddItemToBestPosition(publicContainer, itemStack, isRareItem)) {
            // 添加物品后重新组织所有页面，确保稀有物品和最新物品在最前面
            publicContainer.reorganizeItems();
            
            return;
        }
        
        // 如果没有找到合适的位置，创建新页面并添加物品
        if (addItemToNewPage(publicContainer, itemStack)) {
            // 添加物品后重新组织所有页面
            publicContainer.reorganizeItems();
        }
    }

    /**
     * 查找最佳位置并添加物品
     */
    private boolean findAndAddItemToBestPosition(GarbageContainer container, ItemStack item, boolean isRareItem) {
        // 获取所有页面并按索引排序
        java.util.List<java.util.Map.Entry<Integer, org.example1.dfPubBin.data.GarbagePage>> sortedPages = 
            new java.util.ArrayList<>(container.getPages().entrySet());
        sortedPages.sort(java.util.Map.Entry.comparingByKey());
        
        // 遍历所有页面尝试添加
        for (java.util.Map.Entry<Integer, org.example1.dfPubBin.data.GarbagePage> entry : sortedPages) {
            org.example1.dfPubBin.data.GarbagePage page = entry.getValue();
            
            // 尝试合并相似物品
            for (int i = 0; i < page.getSize(); i++) {
                ItemStack existingItem = page.getItem(i);
                if (existingItem != null && !existingItem.getType().isAir() && 
                    existingItem.isSimilar(item) && existingItem.getAmount() < existingItem.getMaxStackSize()) {
                    // 合并物品
                    int freeSpace = existingItem.getMaxStackSize() - existingItem.getAmount();
                    if (item.getAmount() <= freeSpace) {
                        existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                        // 设置所有者为null（自然），时间戳为当前时间
                        page.setItemWithOwner(i, existingItem, null);
                        return true;
                    } else {
                        // 部分合并
                        existingItem.setAmount(existingItem.getMaxStackSize());
                        page.setItemWithOwner(i, existingItem, null);
                        // 处理剩余物品
                        ItemStack remainingItem = item.clone();
                        remainingItem.setAmount(item.getAmount() - freeSpace);
                        return findAndAddItemToBestPosition(container, remainingItem, isRareItem);
                    }
                }
            }
            
            // 尝试放置到空槽位
            for (int i = 0; i < page.getSize(); i++) {
                if (page.getItem(i) == null || page.getItem(i).getType().isAir()) {
                    // 设置所有者为null（自然），时间戳为当前时间
                    page.setItemWithOwner(i, item.clone(), null);
                    return true;
                }
            }
        }
        
        // 如果所有现有页面都满了，返回false让调用者处理创建新页面
        return false;
    }

    /**
     * 尝试将物品添加到新页面
     */
    private boolean addItemToNewPage(GarbageContainer container, ItemStack item) {
        // 获取当前最大页码并创建新页面
        int nextPageIndex = 0;
        if (!container.getPages().isEmpty()) {
            // 获取最大的页面索引，然后加1
            nextPageIndex = container.getPages().keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
        }
        
        // 确保新的页面索引不与任何现有页面冲突
        while (container.getPages().containsKey(nextPageIndex)) {
            nextPageIndex++;
        }
        
        org.example1.dfPubBin.data.GarbagePage newPage = container.getOrCreatePage(nextPageIndex, 45);
        
        // 在新页面上添加物品
        return addItemToPage(newPage, item);
    }

    /**
     * 尝试将物品添加到指定页面
     */
    private boolean addItemToPage(org.example1.dfPubBin.data.GarbagePage page, ItemStack item) {
        // 获取页面大小
        int size = page.getSize();
        
        // 首先尝试合并相似物品
        for (int i = 0; i < size; i++) {
            ItemStack existingItem = page.getItem(i);
            
            if (existingItem != null && !existingItem.getType().isAir() && 
                existingItem.isSimilar(item) && existingItem.getAmount() < existingItem.getMaxStackSize()) {
                // 如果物品相似且未满堆叠，尝试合并
                int freeSpace = existingItem.getMaxStackSize() - existingItem.getAmount();
                if (item.getAmount() <= freeSpace) {
                    // 如果能完全合并
                    existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                    // 设置所有者为null（自然），时间戳为当前时间
                    page.setItemWithOwner(i, existingItem, null);
                    return true;
                } else {
                    // 部分合并
                    existingItem.setAmount(existingItem.getMaxStackSize());
                    // 设置所有者为null（自然），时间戳为当前时间
                    page.setItemWithOwner(i, existingItem, null);
                    item.setAmount(item.getAmount() - freeSpace);
                    // 如果还有剩余物品，继续尝试添加剩余部分
                    if (item.getAmount() > 0) {
                        return addItemToPage(page, item); // 递归处理剩余物品
                    }
                    return true;
                }
            }
        }
        
        // 检查页面是否已满
        int occupiedSlots = 0;
        for (int i = 0; i < size; i++) {
            ItemStack existingItem = page.getItem(i);
            if (existingItem != null && !existingItem.getType().isAir()) {
                occupiedSlots++;
            }
        }
        
        // 如果页面未满，添加物品到合适位置
        if (occupiedSlots < size) {
            // 查找空槽位并添加物品
            for (int i = 0; i < size; i++) {
                if (page.getItem(i) == null || page.getItem(i).getType().isAir()) {
                    // 设置所有者为null（自然），时间戳为当前时间
                    page.setItemWithOwner(i, item.clone(), null);
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 启动任务
     */
    public static void start(DfPubBinPlugin plugin) {
        DroppedItemCleanupTask task = new DroppedItemCleanupTask(plugin);
        
        // 从配置获取间隔时间（分钟），转换为ticks（1分钟 = 1200 ticks）
        int intervalMinutes = ConfigManager.DROPPED_ITEM_CLEANUP_INTERVAL_MINUTES;
        long intervalTicks = intervalMinutes * 1200L; // 转换为ticks
        
        // 确保间隔时间至少为20 ticks（1秒）
        if (intervalTicks < 20) {
            intervalTicks = 1200L; // 默认1分钟
        }
        
        task.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }
}