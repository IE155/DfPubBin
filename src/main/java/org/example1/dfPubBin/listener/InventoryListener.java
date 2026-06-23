package org.example1.dfPubBin.listener;

import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.data.GarbageType;
import org.example1.dfPubBin.gui.GuiHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * GUI 点击监听
 * 使用惰性刷新原则
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (event.getInventory() == null) {
            return;
        }

        String title = event.getView().getTitle();
        
        // 检查是否为丢弃确认GUI
        if (title.equals("确认丢弃")) {
            // 检查点击的库存是否是丢弃确认界面本身
            if (event.getInventory().equals(event.getView().getTopInventory())) {
                // 点击的是丢弃确认界面
                if (event.getRawSlot() >= 36) {
                    // 底部功能栏（36-44），如果是功能按钮则取消事件
                    if (event.getRawSlot() == 36 || event.getRawSlot() == 44) {
                        // 这些是功能按钮，事件应该被取消并处理
                        event.setCancelled(true);
                        return; // 让 GuiListener 处理这些按钮
                    }
                    // 37-43 是白色玻璃板，也取消事件以防止移动
                    else if (event.getRawSlot() >= 37 && event.getRawSlot() <= 43) {
                        event.setCancelled(true);
                        return;
                    }
                }
                
                // 处理在丢弃确认界面中的Shift+点击事件
                if (event.getClick().isShiftClick() && event.getClickedInventory().equals(player.getInventory())) {
                    // 从玩家背包使用Shift+点击移动到丢弃确认界面
                    ItemStack item = event.getCurrentItem();
                    if (item != null && item.getType() != Material.AIR) {
                        // 找到丢弃确认界面中第一个空槽位（0-35）
                        ItemStack[] contents = event.getInventory().getContents();
                        for (int i = 0; i < 36; i++) {
                            if (contents[i] == null || contents[i].getType() == Material.AIR) {
                                // 找到空槽位，移动物品
                                event.getInventory().setItem(i, item.clone());
                                // 清空玩家背包中的物品
                                event.getClickedInventory().setItem(event.getSlot(), null);
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                    return;
                }
                
                // 处理拖拽操作
                if (event.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY) {
                    // 处理数字键快速移动物品
                    ItemStack item = event.getCurrentItem();
                    if (item != null && item.getType() != Material.AIR) {
                        // 找到丢弃确认界面中第一个空槽位（0-35）
                        ItemStack[] contents = event.getInventory().getContents();
                        for (int i = 0; i < 36; i++) {
                            if (contents[i] == null || contents[i].getType() == Material.AIR) {
                                // 找到空槽位，移动物品
                                event.getInventory().setItem(i, item.clone());
                                // 清空玩家背包中的物品
                                event.getClickedInventory().setItem(event.getSlot(), null);
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                    return;
                }
                
                // 防止从丢弃确认界面拿走物品
                if (event.getRawSlot() >= 0 && event.getRawSlot() <= 35) {
                    // 如果玩家试图从丢弃确认界面拿走物品，则取消该操作
                    if (event.getClick() == org.bukkit.event.inventory.ClickType.LEFT 
                        || event.getClick() == org.bukkit.event.inventory.ClickType.RIGHT 
                        || event.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_LEFT 
                        || event.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT) {
                        
                        // 如果当前槽位有物品，并且操作会导致物品被移走，则取消事件
                        ItemStack currentItem = event.getCurrentItem();
                        if (currentItem != null && currentItem.getType() != Material.AIR) {
                            // 对于左键/右键点击，如果是从确认界面拿取物品，则取消
                            if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                                // 检查是拿取操作还是放置操作
                                if (event.getClick() == org.bukkit.event.inventory.ClickType.LEFT 
                                    || event.getClick() == org.bukkit.event.inventory.ClickType.RIGHT) {
                                    // 这是拿取操作，取消事件
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
                
                // 物品放置区域（0-35）允许正常交互（如拖拽放置）
                if (event.getRawSlot() >= 0 && event.getRawSlot() <= 35) {
                    // 对拖拽操作不做特殊处理，让其正常传递，但防止从确认界面拿走物品
                }
            }
            // 如果点击的是玩家背包，则允许所有交互
            // 不需要做任何特殊处理，让事件正常传递
            return;
        }
        
        // 检查是否为垃圾桶GUI
        if (title.startsWith("垃圾桶 - 第 ")) {
            // 处理从玩家背包到垃圾桶GUI的物品拖放
            if (event.getClickedInventory() != event.getInventory()) {
                // 从玩家背包拖拽到垃圾桶GUI - 不允许，必须通过丢弃确认界面
                if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                    // 从玩家背包拖到垃圾桶GUI（上层库存）
                    event.setCancelled(true);
                    player.sendMessage("§c请先点击黄色玻璃板进入丢弃确认界面再丢弃物品");
                    return;
                }
            }
            
            // 功能栏全部禁止玩家操作
            if (event.getRawSlot() >= 45) {
                event.setCancelled(true);
                return;
            }

            // 只允许玩家在物品区域（0-44）进行操作（取出物品）
            if (event.getRawSlot() < 45) {
                // 如果点击的是玩家背包，则允许正常交互
                if (event.getClickedInventory() != event.getInventory()) {
                    // 玩家在操作自己的背包，允许正常交互
                    return;
                }
                // 如果点击的是垃圾桶GUI中的物品，让 GuiListener 处理
                // 这里不进行任何操作，让事件继续传递给 GuiListener
            } else {
                // 功能栏操作（45-53），全部禁止
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder guiHolder)) {
            // 检查是否是丢弃确认界面（没有GuiHolder）
            if (event.getView().getTitle().equals("确认丢弃")) {
                // 丢弃确认界面关闭时不需要特殊处理，因为丢弃操作在点击绿色按钮时已经处理
                return;
            }
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        // 同步当前页面的物品到垃圾桶数据
        int pageIndex = guiHolder.getPageIndex();
        GarbageType type = guiHolder.getType();
        
        GarbageContainer container;
        if (type == GarbageType.PUBLIC) {
            container = GarbageManager.getPublicContainer();
        } else {
            container = GarbageManager.getPrivateContainer(player.getUniqueId());
        }

        GarbagePage page = container.getOrCreatePage(pageIndex, 45);
        ItemStack[] inventoryContents = event.getInventory().getContents();

        // 同步前45个槽位（物品区域）到垃圾桶页面数据
        for (int i = 0; i < 45; i++) {
            if (i < inventoryContents.length) {
                ItemStack item = inventoryContents[i];
                
                // 根据垃圾桶类型设置物品
                if (type == GarbageType.PRIVATE) {
                    if (item != null && item.getType() != Material.AIR) {
                        // 如果有新物品，记录当前玩家为所有者
                        page.setItem(i, item, player);
                    } else {
                        // 如果槽位为空，直接设置（会移除所有者信息）
                        page.setItem(i, item);
                    }
                } else {
                    // 对于公共垃圾桶，直接设置物品
                    page.setItem(i, item);
                }
            }
        }
    }
}
