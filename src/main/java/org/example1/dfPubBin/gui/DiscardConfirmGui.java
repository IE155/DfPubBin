package org.example1.dfPubBin.gui;

import org.example1.dfPubBin.data.GarbageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 丢弃确认界面
 */
public class DiscardConfirmGui {

    public static Inventory create() {
        // 默认情况下，创建一个没有特定来源的丢弃确认界面，物品会进入公共垃圾桶
        return create(GarbageType.PUBLIC);
    }

    public static Inventory create(GarbageType sourceType) {
        // 45个槽位 = 9x5 布局
        Inventory inv = Bukkit.createInventory(new DiscardConfirmHolder(sourceType), 45, "确认丢弃");

        // 物品放置区域（前4行，0-35）
        // 这里不设置默认物品，允许玩家拖放物品

        // 底部功能栏
        inv.setItem(36, button(Material.RED_STAINED_GLASS_PANE, "§c取消"));
        // 37-42 为白色玻璃板，用作填充
        for (int i = 37; i < 42; i++) {
            inv.setItem(i, button(Material.WHITE_STAINED_GLASS_PANE, " "));
        }
        inv.setItem(43,button(Material.WHITE_STAINED_GLASS_PANE, " "));
        // 42号槽位：紫色玻璃板，根据来源类型显示相应文本
        String viewButtonText = sourceType == GarbageType.PUBLIC ? "§5去公共垃圾桶查看" : "§5去私人垃圾桶查看";
        inv.setItem(42, button(Material.PURPLE_STAINED_GLASS_PANE, viewButtonText));
        inv.setItem(44, button(Material.GREEN_STAINED_GLASS_PANE, "§a确认丢弃"));

        return inv;
    }

    public static Inventory create(int size, String title) {
        Inventory inv = Bukkit.createInventory(null, size, title);
        return inv;
    }

    private static ItemStack button(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    public static class DiscardConfirmHolder implements InventoryHolder {
        private final GarbageType sourceType;

        public DiscardConfirmHolder(GarbageType sourceType) {
            this.sourceType = sourceType;
        }

        public GarbageType getSourceType() {
            return sourceType;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
