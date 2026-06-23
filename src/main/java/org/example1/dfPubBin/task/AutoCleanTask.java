package org.example1.dfPubBin.task;

import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoCleanTask {

    public static org.bukkit.scheduler.BukkitTask start(DfPubBinPlugin dfPubBinPlugin) {
        if (!ConfigManager.AUTO_CLEAN_ENABLED) return null;

        // 从配置中获取时间间隔
        long interval = ConfigManager.AUTO_CLEAN_INTERVAL;

        org.bukkit.scheduler.BukkitRunnable runnable = new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                // 获取公共垃圾桶
                org.example1.dfPubBin.data.GarbageContainer publicContainer = org.example1.dfPubBin.data.GarbageManager.getPublicContainer();

                // 遍历垃圾桶页面
                publicContainer.getPages().forEach((pageIndex, page) -> {
                    // 遍历页面的所有槽位
                    for (int i = 0; i < page.getSize(); i++) {
                        org.bukkit.inventory.ItemStack item = page.getItem(i);

                        // 如果物品不在白名单中，则清理物品
                        if (item != null && item.getType() != org.bukkit.Material.AIR) {
                            // 判断是否是白名单物品，若不是则清理
                            if (!isWhitelisted(item)) {
                                page.setItem(i, null); // 清除物品
                            }
                        }
                    }
                });
            }
        };
        
        return runnable.runTaskTimer(DfPubBinPlugin.getInstance(), interval, interval); // 使用配置文件中的时间间隔
    }

    private static boolean isWhitelisted(ItemStack item) {
        // 可以根据实际需求检查物品是否在白名单中
        return DfPubBinPlugin.getInstance().getConfig().getStringList("auto-clean.whitelist-items")
                .contains(item.getType().name());
    }
}
