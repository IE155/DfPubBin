package org.example1.dfPubBin.disablemechanism;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.example1.dfPubBin.config.ConfigManager;

/**
 * 监听并阻止物品自然消失事件
 * 当配置中启用该功能时，阻止原版的5分钟掉落物刷新机制
 */
public class ItemDespawnListener implements Listener {

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        // 检查是否启用了禁用原版掉落物刷新机制
        if (ConfigManager.DISABLE_ITEM_DESPAWN_MECHANISM) {
            // 取消物品消失事件
            event.setCancelled(true);
        }
    }
}