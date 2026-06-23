package org.example1.dfPubBin.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.data.GarbageType;
import org.example1.dfPubBin.gui.DiscardConfirmGui;
import org.example1.dfPubBin.gui.GarbageGui;
import org.example1.dfPubBin.gui.GarbageGui.GarbageHolder;
import org.example1.dfPubBin.rare.RareItemManager;
import org.example1.dfPubBin.util.NewItemDetector;

/**
 * GUI 点击事件监听器
 * 负责路由点击事件到对应的处理器
 */
public class GuiListener implements Listener {

    private final GarbagePickupHandler pickupHandler = new GarbagePickupHandler();
    private final GarbageItemAddManager itemAddManager = new GarbageItemAddManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();

        // 检查是否为垃圾箱界面
        if (inv.getHolder() instanceof GarbageHolder holder) {
            handleGarbageGuiClick(event, player, holder);
        }
        // 检查是否为丢弃确认界面
        else if (inv.getHolder() instanceof DiscardConfirmGui.DiscardConfirmHolder) {
            handleDiscardConfirmClick(event, player, inv);
        }
    }

    /**
     * 处理垃圾桶GUI的点击事件
     */
    private void handleGarbageGuiClick(InventoryClickEvent event, Player player, GarbageHolder holder) {
        int rawSlot = event.getRawSlot();
        int pageIndex = holder.getPageIndex();
        GarbageType type = holder.getType();
        GarbageContainer container = holder.getContainer();

        if (rawSlot < 45) {
            // 检查点击的是否是垃圾桶库存中的物品（而不是玩家背包）
            if (event.getClickedInventory() != null &&
                    event.getClickedInventory().equals(event.getView().getTopInventory())) {
                // 点击的是垃圾桶库存中的物品
                if (pickupHandler.isPickupClick(event.getClick())) {
                    // 左键或右键点击：取出物品
                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem != null && !clickedItem.getType().isAir()) {
                        GarbagePage page = container.getOrCreatePage(pageIndex, 45);
                        if (pickupHandler.handlePickup(player, clickedItem, container, page, rawSlot, pageIndex, type)) {
                            event.setCancelled(true);
                            return;
                        }
                        // 冷却中或物品为空
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    // 其他操作（Shift+点击、拖拽、双击等）在垃圾桶物品上，取消操作以防止复制
                    event.setCancelled(true);
                    return;
                }
            }
            // 如果点击的是玩家背包中的物品或进行拖拽操作，则允许正常交互

            // 对于所有其他情况（如拖拽操作），允许正常交互但刷新界面
            event.setCancelled(false);

            // 下一 tick 强制刷新 GUI，防止底部被覆盖
            Bukkit.getScheduler().runTaskLater(
                    org.example1.dfPubBin.DfPubBinPlugin.getInstance(),
                    () -> player.openInventory(GarbageGui.create(player, container, type, pageIndex)),
                    1L
            );
            return;
        }

        event.setCancelled(true);

        // 丢弃确认
        if (rawSlot == 49) {
            player.openInventory(DiscardConfirmGui.create(type));
            return;
        }

        // 跳转私人垃圾桶（蓝）- 仅在公共垃圾桶界面有效
        if (rawSlot == 50 && type == GarbageType.PUBLIC) {
            player.closeInventory();
            Bukkit.getScheduler().runTask(
                    org.example1.dfPubBin.DfPubBinPlugin.getInstance(),
                    () -> player.performCommand("pbin priv")
            );
            return;
        }

        // 上一页
        if (rawSlot == 45) {
            if (pageIndex > 0) {
                player.openInventory(GarbageGui.create(player, container, type, pageIndex - 1));
            }
            return;
        }

        // 下一页
        if (rawSlot == 53) {
            int lastPageIndex = container.getHighestPageWithItems();
            if (pageIndex < lastPageIndex) {
                player.openInventory(GarbageGui.create(player, container, type, pageIndex + 1));
            }
        }
    }

    /**
     * 处理丢弃确认界面的点击事件
     */
    private void handleDiscardConfirmClick(InventoryClickEvent event, Player player, Inventory inv) {
        int slot = event.getSlot();

        // 取消按钮 (红玻璃板，槽位36)
        if (slot == 36) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        // "去垃圾桶查看"按钮 (紫玻璃板，槽位42)
        if (slot == 42) {
            event.setCancelled(true);
            DiscardConfirmGui.DiscardConfirmHolder discardHolder =
                    (DiscardConfirmGui.DiscardConfirmHolder) inv.getHolder();
            GarbageType sourceType = discardHolder.getSourceType();

            player.closeInventory();
            String command = sourceType == GarbageType.PUBLIC ? "pbin pub" : "pbin priv";
            Bukkit.getScheduler().runTask(
                    org.example1.dfPubBin.DfPubBinPlugin.getInstance(),
                    () -> player.performCommand(command)
            );
            return;
        }

        // 确认丢弃按钮 (绿玻璃板，槽位44)
        if (slot == 44) {
            event.setCancelled(true);
            DiscardConfirmGui.DiscardConfirmHolder discardHolder =
                    (DiscardConfirmGui.DiscardConfirmHolder) inv.getHolder();
            GarbageType sourceType = discardHolder.getSourceType();

            org.example1.dfPubBin.DfPubBinPlugin.getInstance().getServer().getScheduler().runTask(
                    org.example1.dfPubBin.DfPubBinPlugin.getInstance(),
                    () -> executeDiscardLogic(inv, player, sourceType)
            );
            return;
        }

        // 对于物品放置区域（0-35）的点击，不取消事件，允许玩家放置/移动物品
        if (slot >= 0 && slot <= 35) {
            if (event.getClick().isCreativeAction() || event.getClick() == ClickType.SHIFT_LEFT
                    || event.getClick() == ClickType.SHIFT_RIGHT) {
                // 不取消事件，让InventoryListener处理
            } else {
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != org.bukkit.Material.AIR) {
                    // 不取消事件，让InventoryListener处理
                }
            }
        }
    }

    /**
     * 执行丢弃逻辑（在确认丢弃按钮点击后调用）
     */
    private void executeDiscardLogic(Inventory inv, Player player, GarbageType sourceType) {
        boolean hasRareItem = false;
        boolean hasNewItemType = false;

        java.util.Set<org.bukkit.Material> itemsToBeAdded = new java.util.HashSet<>();

        if (sourceType == GarbageType.PUBLIC) {
            GarbageContainer publicContainer = GarbageManager.getPublicContainer();

            // 遍历玩家放入的所有物品，收集物品类型
            for (int i = 0; i < 36; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && !item.getType().isAir()) {
                    if (RareItemManager.isRare(item)) {
                        hasRareItem = true;
                    }
                    itemsToBeAdded.add(item.getType());
                }
            }

            // 遍历垃圾桶中的物品，检查是否有新物品类型
            java.util.Set<org.bukkit.Material> existingItems = new java.util.HashSet<>();
            for (java.util.Map.Entry<Integer, GarbagePage> pageEntry : publicContainer.getPages().entrySet()) {
                GarbagePage page = pageEntry.getValue();
                for (int i = 0; i < page.getSize(); i++) {
                    ItemStack existingItem = page.getItem(i);
                    if (existingItem != null && !existingItem.getType().isAir()) {
                        existingItems.add(existingItem.getType());
                    }
                }
            }

            for (org.bukkit.Material itemMaterial : itemsToBeAdded) {
                if (!existingItems.contains(itemMaterial)) {
                    hasNewItemType = true;
                    break;
                }
            }
        }

        // 在丢弃确认界面的前4行(槽位0-35)中查找所有物品并添加到对应的垃圾桶
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && !item.getType().isAir()) {
                if (sourceType == GarbageType.PUBLIC) {
                    itemAddManager.addToPublicWithoutBroadcast(item, player);
                } else {
                    itemAddManager.addToPrivate(item, player);
                }
                inv.clear(i);
            }
        }

        // 如果是公共垃圾桶且包含稀有物品，则广播一次
        if (sourceType == GarbageType.PUBLIC && hasRareItem) {
            RareItemManager.broadcastRareItem("public");
        }

        // 如果是公共垃圾桶且包含新类型物品，则发送新物品提示
        if (sourceType == GarbageType.PUBLIC && hasNewItemType && ConfigManager.NEW_ITEM_NOTIFICATION_ENABLED) {
            NewItemDetector.broadcastNewItemsNotification();
        }
    }
}
