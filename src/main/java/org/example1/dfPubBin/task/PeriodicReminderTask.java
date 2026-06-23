package org.example1.dfPubBin.task;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.config.ConfigManager;

public class PeriodicReminderTask {

    private static BukkitTask task;

    /**
     * 启动周期性提醒任务
     * 
     * @param plugin 插件实例
     */
    public static void start(DfPubBinPlugin plugin) {
        // 从配置中获取提醒间隔（分钟）
        int intervalMinutes = ConfigManager.PERIODIC_REMINDER_INTERVAL;
        
        // 如果间隔设置为0或负数，则不启动任务
        if (intervalMinutes <= 0) {
            return;
        }
        
        // 将分钟转换为ticks（1分钟 = 1200 ticks）
        long intervalTicks = intervalMinutes * 1200L;
        
        // 创建周期性任务
        org.bukkit.scheduler.BukkitRunnable runnable = new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                // 发送周期性提醒消息
                sendPeriodicReminder();
            }
        };
        
        // 启动周期性任务
        task = runnable.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }

    /**
     * 发送周期性提醒消息
     */
    private static void sendPeriodicReminder() {
        if (ConfigManager.PERIODIC_REMINDER_ENABLED && !ConfigManager.PERIODIC_REMINDER_MESSAGE.isEmpty()) {
            // 分别处理主消息和点击文本
            Component mainMessage = LegacyComponentSerializer.legacySection().deserialize(ConfigManager.PERIODIC_REMINDER_MESSAGE);
            Component clickText = Component.text("§a(点击查看!)")
                .clickEvent(ClickEvent.runCommand("/pbin pub"));
            
            // 组合消息
            TextComponent fullMessage = Component.text()
                .append(mainMessage)
                .append(Component.space()) // 添加一个空格
                .append(clickText)
                .build();
                
            Bukkit.broadcast(fullMessage);
        }
    }

    /**
     * 停止周期性提醒任务
     */
    public static void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }
}