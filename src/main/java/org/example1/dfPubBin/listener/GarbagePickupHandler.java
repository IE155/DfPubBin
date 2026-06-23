package org.example1.dfPubBin.listener;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.data.GarbageType;
import org.example1.dfPubBin.gui.GarbageGui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 垃圾桶物品取出处理器
 * 负责处理玩家从垃圾桶中取出物品的逻辑，包括冷却时间检测
 */
public class GarbagePickupHandler {

    // 跟踪玩家最后拿取物品的时间
    private final HashMap<UUID, Long> lastPickupTime = new HashMap<>();

    /**
     * 检查点击是否为有效的取出操作（左键或右键）
     */
    public boolean isPickupClick(ClickType clickType) {
        return clickType == ClickType.RIGHT || clickType == ClickType.LEFT;
    }

    /**
     * 处理从垃圾桶取出物品
     * 包含冷却检测、物品移除、Lore清理、给玩家物品、刷新界面
     *
     * @return true 如果成功取出物品
     */
    public boolean handlePickup(Player player, ItemStack clickedItem,
                                GarbageContainer container, GarbagePage page,
                                int rawSlot, int pageIndex, GarbageType type) {
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return false;
        }

        // 检查冷却时间
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastPickupTime.getOrDefault(playerId, 0L);
        long cooldownMs = ConfigManager.ITEM_PICKUP_COOLDOWN * 1000L;

        if (currentTime - lastTime < cooldownMs) {
            int remainingSeconds = (int) Math.ceil((cooldownMs - (currentTime - lastTime)) / 1000.0);
            player.sendMessage(ConfigManager.ITEM_PICKUP_COOLDOWN_MESSAGE
                    .replace("%seconds%", String.valueOf(remainingSeconds)));
            return false;
        }

        // 更新最后拿取时间
        lastPickupTime.put(playerId, currentTime);

        // 取出当前格子的所有物品
        int amount = clickedItem.getAmount();

        // 从垃圾桶数据中减少物品数量
        page.setItem(rawSlot, null); // 清空当前槽位

        // 重新组织整个容器中的物品，确保稀有物品和最新物品在最前面
        container.reorganizeItems();

        // 给玩家物品，重置标签但保留附魔
        ItemStack itemToGive = cleanItemForPlayer(clickedItem, amount);
        player.getInventory().addItem(itemToGive);

        // 刷新界面
        Bukkit.getScheduler().runTaskLater(
                DfPubBinPlugin.getInstance(),
                () -> player.openInventory(GarbageGui.create(player, container, type, pageIndex)),
                1L
        );

        return true;
    }

    /**
     * 清理物品Lore（移除垃圾桶附加信息），保留附魔
     */
    private ItemStack cleanItemForPlayer(ItemStack clickedItem, int amount) {
        ItemStack itemToGive = clickedItem.clone();
        itemToGive.setAmount(amount);

        // 重置标签但保留附魔
        ItemMeta meta = itemToGive.getItemMeta();
        if (meta != null) {
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            meta.setLore(null); // 清空Lore
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
            itemToGive.setItemMeta(meta);
        }

        return itemToGive;
    }
}
