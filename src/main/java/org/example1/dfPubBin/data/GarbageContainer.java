package org.example1.dfPubBin.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 垃圾桶容器类，用于存储物品及其管理
 */
public class GarbageContainer {

    private final Map<Integer, GarbagePage> pages = new HashMap<>();
    private final GarbageType type;
    private final UUID playerId;

    public GarbageContainer(GarbageType type, UUID playerId) {
        this.type = type;
        this.playerId = playerId;
    }

    // 获取或创建页面
    public GarbagePage getOrCreatePage(int pageIndex, int size) {
        return pages.computeIfAbsent(pageIndex, k -> new GarbagePage(size));
    }

    // 获取指定页面
    public GarbagePage getPage(int pageIndex) {
        return pages.get(pageIndex);
    }

    // 获取页面列表
    public Map<Integer, GarbagePage> getPages() {
        return pages;
    }

    // 获取最后一页的索引
    public int getLastPageIndex() {
        if (pages.isEmpty()) {
            return 0; // 如果没有页面，返回第一页
        }
        
        // 从最大的页面索引开始向下查找，直到找到有物品的页面
        return getHighestPageWithItems();
    }
    
    // 获取有物品的最高页面索引
    public int getHighestPageWithItems() {
        if (pages.isEmpty()) {
            return 0; // 如果没有页面，返回第一页
        }
        
        // 获取所有页面索引并排序
        java.util.List<Integer> sortedPageIndexes = new java.util.ArrayList<>(pages.keySet());
        java.util.Collections.sort(sortedPageIndexes, java.util.Collections.reverseOrder());
        
        // 从最大索引开始查找有物品的页面
        for (int pageIndex : sortedPageIndexes) {
            GarbagePage page = pages.get(pageIndex);
            if (page != null && page.hasItems()) {
                return pageIndex;
            }
        }
        
        // 如果没有找到有物品的页面，返回0
        return 0;
    }

    // 设置指定页面的物品
    public void setItem(int pageIndex, int slotIndex, ItemStack item, Player player) {
        GarbagePage page = getOrCreatePage(pageIndex, 45); // 默认每页45个槽位
        page.setItem(slotIndex, item, player);
    }
    
    /**
     * 重新组织所有页面中的物品，确保稀有物品在最前面，最新物品其次，然后是一般物品
     */
    public void reorganizeItems() {
        // 收集所有物品及其相关信息
        List<GarbageItemEntry> allItems = new ArrayList<>();
        
        // 遍历所有页面收集物品
        for (Map.Entry<Integer, GarbagePage> pageEntry : pages.entrySet()) {
            GarbagePage page = pageEntry.getValue();
            for (int i = 0; i < page.getSize(); i++) {
                ItemStack item = page.getItem(i);
                if (item != null && item.getType() != org.bukkit.Material.AIR) {
                    UUID owner = page.getItemOwner(i);
                    Long dropTime = page.getItemDropTime(i);
                    allItems.add(new GarbageItemEntry(item, owner, dropTime, pageEntry.getKey(), i));
                }
            }
        }
        
        // 按照要求排序：稀有物品优先，然后是按时间倒序（最新物品在前）
        allItems.sort(new Comparator<GarbageItemEntry>() {
            @Override
            public int compare(GarbageItemEntry a, GarbageItemEntry b) {
                boolean aIsRare = org.example1.dfPubBin.rare.RareItemManager.isRare(a.item);
                boolean bIsRare = org.example1.dfPubBin.rare.RareItemManager.isRare(b.item);

                // 稀有物品优先
                if (aIsRare && !bIsRare) {
                    return -1;
                }
                if (!aIsRare && bIsRare) {
                    return 1;
                }

                // 如果都是稀有物品或都不是稀有物品，则按时间倒序（最新的在前）
                if (a.dropTime != null && b.dropTime != null) {
                    return Long.compare(b.dropTime, a.dropTime);
                } else if (a.dropTime != null) {
                    return -1;
                } else if (b.dropTime != null) {
                    return 1;
                }
                return 0;
            }
        });
        
        // 清空所有页面
        for (GarbagePage page : pages.values()) {
            for (int i = 0; i < page.getSize(); i++) {
                page.setItem(i, null);
            }
        }
        
        // 按照排序后的顺序重新分配物品到页面，每页最多45个物品
        int slotIndex = 0;
        int pageIndex = 0;
        GarbagePage currentPage = getOrCreatePage(0, 45);
        
        for (GarbageItemEntry entry : allItems) {
            if (slotIndex >= 45) {
                // 当前页面已满，移动到下一页
                slotIndex = 0;
                pageIndex++;
                currentPage = getOrCreatePage(pageIndex, 45);
            }
            
            currentPage.setItemWithOwner(slotIndex, entry.item, entry.owner);
            slotIndex++;
        }
        
        // 删除空页面（保留从0到最高有物品页面之间的所有页面）
        int lastPageIndex = getHighestPageWithItems();
        // 删除高于lastPageIndex的所有页面
        pages.entrySet().removeIf(entry -> entry.getKey() > lastPageIndex && !entry.getValue().hasItems());
    }
    
    // 辅助方法：根据UUID获取玩家对象
    private Player getPlayerFromUuid(UUID uuid) {
        if (uuid == null) return null;
        return org.bukkit.Bukkit.getOfflinePlayer(uuid).getPlayer();
    }
    
    /**
     * 用于存储物品及其相关信息的辅助类
     */
    private static class GarbageItemEntry {
        final ItemStack item;
        final UUID owner;
        final Long dropTime;
        final int originalPageIndex;
        final int originalSlotIndex;
        
        GarbageItemEntry(ItemStack item, UUID owner, Long dropTime, int originalPageIndex, int originalSlotIndex) {
            this.item = item;
            this.owner = owner;
            this.dropTime = dropTime;
            this.originalPageIndex = originalPageIndex;
            this.originalSlotIndex = originalSlotIndex;
        }
    }
}

