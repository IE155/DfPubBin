package org.example1.dfPubBin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;


/**
 * 新物品检测工具类
 * 负责检测垃圾桶中是否有新类型的物品
 */
public class NewItemDetector {
    
    /**
     * 检查垃圾桶中是否已存在指定类型的物品
     * 
     * @param container 垃圾桶容器
     * @param itemToCheck 要检查的物品
     * @return 如果垃圾桶中已存在相同类型的物品则返回true，否则返回false
     */
    public static boolean containsItemType(GarbageContainer container, ItemStack itemToCheck) {
        if (itemToCheck == null || itemToCheck.getType().isAir()) {
            return true; // 空气物品视为已存在
        }
        
        if (container == null) {
            return false; // 容器为空则认为物品不存在
        }
        
        java.util.Map<Integer, org.example1.dfPubBin.data.GarbagePage> pages = container.getPages();
        if (pages == null || pages.isEmpty()) {
            return false; // 如果没有页面，则物品不存在
        }
        
        // 遍历垃圾桶中的所有页面
        for (org.example1.dfPubBin.data.GarbagePage page : pages.values()) {
            if (page == null) {
                continue; // 跳过空页面
            }
            
            // 遍历页面中的所有槽位
            for (int i = 0; i < page.getSize(); i++) {
                ItemStack existingItem = page.getItem(i);
                if (existingItem != null && existingItem.getType() == itemToCheck.getType()) {
                    return true; // 找到相同类型的物品
                }
            }
        }
        return false; // 没有找到相同类型的物品
    }
    
    /**
     * 发送新物品广播通知
     */
    public static void broadcastNewItemsNotification() {
        if (!ConfigManager.NEW_ITEM_NOTIFICATION_ENABLED) {
            return;
        }
        
        Component message = Component.text(ConfigManager.NEW_ITEM_BROADCAST)
            .append(Component.text(ConfigManager.NEW_ITEM_CLICK_TEXT)
                    .clickEvent(ClickEvent.runCommand("/pbin pub")));
        Bukkit.broadcast(message);
    }
}