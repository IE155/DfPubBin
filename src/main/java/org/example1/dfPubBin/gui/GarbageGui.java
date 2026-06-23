package org.example1.dfPubBin.gui;

import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.data.GarbageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

public class GarbageGui {

    public static Inventory create(Player player,
                                   GarbageContainer container,
                                   GarbageType type,
                                   int pageIndex) {



        GarbagePage page = container.getOrCreatePage(pageIndex, 45);

        GarbageHolder holder =
                new GarbageHolder(container, page, type, pageIndex);

        Inventory inv = Bukkit.createInventory(
                holder,
                54,
                type == GarbageType.PUBLIC ? "公共垃圾桶" : "私人垃圾桶"
        );


        ItemStack[] contents = page.getContentsForPlayer(player, type);
        for (int i = 0; i < contents.length && i < 45; i++) {
            inv.setItem(i, contents[i]);
        }



        // 白色占位
        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 45; i < 54; i++) {
            inv.setItem(i, filler);
        }

        // 上一页（红）
        ItemStack prev = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta pm = prev.getItemMeta();
        pm.setDisplayName("§c上一页");
        prev.setItemMeta(pm);
        inv.setItem(45, prev);

        // 丢弃（黄）
        ItemStack drop = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta dm = drop.getItemMeta();
        dm.setDisplayName("§e丢弃物品");
        drop.setItemMeta(dm);
        inv.setItem(49, drop);

        // 跳转私人垃圾桶（蓝）- 仅在公共垃圾桶界面显示
        if (type == GarbageType.PUBLIC) {
            ItemStack bluePane = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
            ItemMeta bm = bluePane.getItemMeta();
            bm.setDisplayName("§9切换私人垃圾桶"); // 空白文字
            bluePane.setItemMeta(bm);
            inv.setItem(50, bluePane); // 位置50，在黄色玻璃板（丢弃）右侧
        }

        // 下一页（绿）
        ItemStack next = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta nm = next.getItemMeta();
        nm.setDisplayName("§a下一页");
        next.setItemMeta(nm);
        inv.setItem(53, next);

        return inv;
    }




    public static class GarbageHolder implements InventoryHolder {

        private final GarbageContainer container;
        private final GarbagePage page;
        private final GarbageType type;
        private final int pageIndex;

        public GarbageHolder(GarbageContainer container,
                             GarbagePage page,
                             GarbageType type,
                             int pageIndex) {
            this.container = container;
            this.page = page;
            this.type = type;
            this.pageIndex = pageIndex;
        }



        public GarbageContainer getContainer() {
            return container;
        }

        public GarbagePage getPage() {
            return page;
        }

        public GarbageType getType() {
            return type;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}



