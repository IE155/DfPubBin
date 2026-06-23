package org.example1.dfPubBin.rare;

import org.example1.dfPubBin.DfPubBinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.example1.dfPubBin.config.ConfigManager;
import java.util.HashSet;
import java.util.Set;

/**
 * 稀有物品检测与播报
 */
public class RareItemManager {

    private static final Set<Material> RARE_ITEMS = new HashSet<>();

    public static void init() {
        // 清空之前的稀有物品列表
        RARE_ITEMS.clear();
        // 获取配置中的稀有物品列表
        for (String id : DfPubBinPlugin.getInstance()
                .getConfig()
                .getStringList("rare-items.items")) {
            try {
                // 将物品类型添加到集合
                RARE_ITEMS.add(Material.valueOf(id));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static boolean isRare(ItemStack item) {
        return item != null && RARE_ITEMS.contains(item.getType());
    }

    public static void broadcastRareItem() {
        net.kyori.adventure.text.Component message = net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_BROADCAST)
                .append(net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_CLICK_TEXT)
                        .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/pbin pub")));
        Bukkit.broadcast(message);
    }
    
    public static void broadcastRareItem(String type) {
        net.kyori.adventure.text.Component message;
        if ("private".equalsIgnoreCase(type)) {
            message = net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_BROADCAST)
                .append(net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_CLICK_TEXT)
                        .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/pbin pub")));
        } else {
            message = net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_BROADCAST)
                .append(net.kyori.adventure.text.Component.text(ConfigManager.RARE_ITEM_CLICK_TEXT)
                        .clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand("/pbin pub")));
        }
        Bukkit.broadcast(message);
    }
}
