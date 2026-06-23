package org.example1.dfPubBin.listener;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.rare.RareItemManager;
import org.example1.dfPubBin.util.NewItemDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 垃圾桶物品添加管理器
 * 负责将物品添加到垃圾桶容器中，包括位置查找、物品合并、分页管理
 */
public class GarbageItemAddManager {

    /**
     * 将物品添加到公共垃圾桶（不发送新物品提示，用于批量处理）
     */
    public void addToPublicWithoutBroadcast(ItemStack item, Player player) {
        GarbageContainer publicContainer = GarbageManager.getPublicContainer();
        addToContainerWithoutRareBroadcast(publicContainer, item, player);
    }

    /**
     * 将物品添加到公共垃圾桶（带新物品检测）
     */
    public void addToPublic(ItemStack item, Player player) {
        GarbageContainer publicContainer = GarbageManager.getPublicContainer();
        boolean wasItemTypeAlreadyPresent = NewItemDetector.containsItemType(publicContainer, item);
        addToContainerWithoutRareBroadcast(publicContainer, item, player);
        if (!wasItemTypeAlreadyPresent && ConfigManager.NEW_ITEM_NOTIFICATION_ENABLED) {
            NewItemDetector.broadcastNewItemsNotification();
        }
    }

    /**
     * 将物品添加到私人垃圾桶
     */
    public void addToPrivate(ItemStack item, Player player) {
        GarbageContainer privateContainer = GarbageManager.getPrivateContainer(player.getUniqueId());
        addToContainerWithoutRareBroadcast(privateContainer, item, player);
    }

    /**
     * 将物品添加到指定的垃圾桶容器（不广播稀有物品消息，用于批量处理）
     */
    private void addToContainerWithoutRareBroadcast(GarbageContainer container, ItemStack item, Player player) {
        boolean isRareItem = RareItemManager.isRare(item);

        if (findAndAddItemToBestPosition(container, item, player, isRareItem)) {
            container.reorganizeItems();
            return;
        }

        if (addItemToNewPage(container, item, player)) {
            container.reorganizeItems();
        } else {
            player.sendMessage(ConfigManager.TRASH_FULL);
        }
    }

    /**
     * 查找最佳位置并添加物品
     */
    private boolean findAndAddItemToBestPosition(GarbageContainer container, ItemStack item, Player player, boolean isRareItem) {
        List<Map.Entry<Integer, GarbagePage>> sortedPages = new ArrayList<>(container.getPages().entrySet());
        sortedPages.sort(Map.Entry.comparingByKey());

        // 稀有物品优先尝试放在前面的页面
        if (isRareItem) {
            for (Map.Entry<Integer, GarbagePage> entry : sortedPages) {
                GarbagePage page = entry.getValue();
                if (tryMergeOrPlace(item, page, player, container, entry.getKey(), isRareItem)) return true;
            }
        } else {
            // 普通物品也按顺序尝试
            for (Map.Entry<Integer, GarbagePage> entry : sortedPages) {
                GarbagePage page = entry.getValue();
                if (tryMergeOrPlace(item, page, player, container, entry.getKey(), isRareItem)) return true;
            }
        }

        return false;
    }

    /**
     * 尝试在当前页面合并或放置物品
     */
    private boolean tryMergeOrPlace(ItemStack item, GarbagePage page, Player player,
                                    GarbageContainer container, int currentPageIndex, boolean isRareItem) {
        // 尝试合并相似物品
        for (int i = 0; i < page.getSize(); i++) {
            ItemStack existingItem = page.getItem(i);
            if (existingItem != null && !existingItem.getType().isAir() &&
                    existingItem.isSimilar(item) && existingItem.getAmount() < existingItem.getMaxStackSize()) {
                int freeSpace = existingItem.getMaxStackSize() - existingItem.getAmount();
                if (item.getAmount() <= freeSpace) {
                    existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                    page.setItem(i, existingItem, player);
                    return true;
                } else {
                    existingItem.setAmount(existingItem.getMaxStackSize());
                    page.setItem(i, existingItem, player);
                    ItemStack remainingItem = item.clone();
                    remainingItem.setAmount(item.getAmount() - freeSpace);
                    return findAndAddItemToBestPosition(container, remainingItem, player, isRareItem);
                }
            }
        }

        // 尝试放置到空槽位
        for (int i = 0; i < page.getSize(); i++) {
            if (page.getItem(i) == null || page.getItem(i).getType().isAir()) {
                page.setItem(i, item.clone(), player);
                return true;
            }
        }

        return false;
    }

    /**
     * 尝试将物品添加到指定页面（递归处理合并和溢出）
     */
    private boolean addItemToPage(GarbagePage page, ItemStack item, Player player,
                                  GarbageContainer container, int currentPageIndex) {
        int size = page.getSize();

        // 首先尝试合并相似物品
        for (int i = 0; i < size; i++) {
            ItemStack existingItem = page.getItem(i);
            if (existingItem != null && !existingItem.getType().isAir() &&
                    existingItem.isSimilar(item) && existingItem.getAmount() < existingItem.getMaxStackSize()) {
                int freeSpace = existingItem.getMaxStackSize() - existingItem.getAmount();
                if (item.getAmount() <= freeSpace) {
                    existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                    page.setItem(i, existingItem, player);
                    return true;
                } else {
                    existingItem.setAmount(existingItem.getMaxStackSize());
                    page.setItem(i, existingItem, player);
                    item.setAmount(item.getAmount() - freeSpace);
                    if (item.getAmount() > 0) {
                        return addItemToPage(page, item, player, container, currentPageIndex);
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

        if (occupiedSlots < size) {
            for (int i = 0; i < size; i++) {
                if (page.getItem(i) == null || page.getItem(i).getType().isAir()) {
                    page.setItem(i, item.clone(), player);
                    break;
                }
            }
            return true;
        } else {
            GarbagePage nextPage = container.getOrCreatePage(currentPageIndex + 1, size);
            return addItemToPage(nextPage, item, player, container, currentPageIndex + 1);
        }
    }

    /**
     * 尝试将物品添加到新页面
     */
    private boolean addItemToNewPage(GarbageContainer container, ItemStack item, Player player) {
        int nextPageIndex = 0;
        if (!container.getPages().isEmpty()) {
            nextPageIndex = container.getPages().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0) + 1;
        }

        while (container.getPages().containsKey(nextPageIndex)) {
            nextPageIndex++;
        }

        GarbagePage newPage = container.getOrCreatePage(nextPageIndex, 45);

        return addItemToPage(newPage, item, player, container, nextPageIndex);
    }
}
