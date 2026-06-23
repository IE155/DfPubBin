package org.example1.dfPubBin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    public static boolean AUTO_CLEAN_ENABLED;
    public static long AUTO_CLEAN_INTERVAL;
    public static int ITEM_PICKUP_COOLDOWN; // 物品拿取冷却时间（秒）
    public static boolean ENABLE_PBIN_COMMAND; // 是否启用 /pbin 指令
    public static boolean NEW_ITEM_NOTIFICATION_ENABLED; // 是否启用新物品提示
    public static boolean PERIODIC_REMINDER_ENABLED; // 是否启用周期性提醒
    public static int PERIODIC_REMINDER_INTERVAL; // 周期性提醒间隔（分钟）
    public static String PERIODIC_REMINDER_MESSAGE; // 周期性提醒消息
    
    // 掉落物清理功能
    public static boolean DROPPED_ITEM_CLEANUP_ENABLED;
    public static int DROPPED_ITEM_CLEANUP_INTERVAL_MINUTES;
    
    // 禁用原版掉落物刷新机制
    public static boolean DISABLE_ITEM_DESPAWN_MECHANISM;
    
    // 自定义消息
    public static String RARE_ITEM_BROADCAST;
    public static String RARE_ITEM_CLICK_TEXT;
    public static String NEW_ITEM_BROADCAST;
    public static String NEW_ITEM_CLICK_TEXT;
    public static String PAGE_TURN_DISABLED;
    public static String COMMAND_DISABLED;
    public static String COMMAND_USAGE;
    public static String COMMAND_INVALID;
    public static String TRASH_FULL;
    public static String ITEM_PICKUP_COOLDOWN_MESSAGE;
    public static String DISCARD_CANCEL;
    public static String DISCARD_SUCCESS;

    public static void load(JavaPlugin plugin) {
        FileConfiguration cfg = plugin.getConfig();

        // 检查配置文件中是否存在键，如果不存在则使用合理的默认值
        AUTO_CLEAN_ENABLED = cfg.contains("auto-clean.enabled") ? cfg.getBoolean("auto-clean.enabled") : true;
        AUTO_CLEAN_INTERVAL = cfg.contains("auto-clean.interval-ticks") ? cfg.getLong("auto-clean.interval-ticks") : 240000L; // 默认20分钟(240000 ticks)
        ITEM_PICKUP_COOLDOWN = cfg.contains("item-pickup-cooldown") ? cfg.getInt("item-pickup-cooldown") : 3; // 默认3秒
        ENABLE_PBIN_COMMAND = cfg.contains("enable-pbin-command") ? cfg.getBoolean("enable-pbin-command") : true; // 默认启用 /pbin 指令
        NEW_ITEM_NOTIFICATION_ENABLED = cfg.contains("new-item-notification.enabled") ? cfg.getBoolean("new-item-notification.enabled") : false; // 从配置文件读取，配置中默认为false
        PERIODIC_REMINDER_ENABLED = cfg.contains("periodic-reminder.enabled") ? cfg.getBoolean("periodic-reminder.enabled") : true; // 默认启用周期性提醒
        PERIODIC_REMINDER_INTERVAL = cfg.contains("periodic-reminder.interval-minutes") ? cfg.getInt("periodic-reminder.interval-minutes") : 120; // 默认120分钟（2小时）
        PERIODIC_REMINDER_MESSAGE = cfg.contains("periodic-reminder.message") ? cfg.getString("periodic-reminder.message") : "§2[DfPubBin]§e公共垃圾桶出现新物品"; // 默认提醒消息
        
        // 掉落物清理配置
        DROPPED_ITEM_CLEANUP_ENABLED = cfg.contains("dropped-item-cleanup.enabled") ? cfg.getBoolean("dropped-item-cleanup.enabled") : true; // 默认启用
        DROPPED_ITEM_CLEANUP_INTERVAL_MINUTES = cfg.contains("dropped-item-cleanup.interval-minutes") ? cfg.getInt("dropped-item-cleanup.interval-minutes") : 30; // 默认30分钟
        
        // 禁用原版掉落物刷新机制配置
        DISABLE_ITEM_DESPAWN_MECHANISM = cfg.contains("disable-item-despawn.enabled") ? cfg.getBoolean("disable-item-despawn.enabled") : false; // 默认禁用
        
        // 加载自定义消息
        RARE_ITEM_BROADCAST = cfg.contains("messages.rare-item-broadcast") ? cfg.getString("messages.rare-item-broadcast") : "§2[DfPubBin]§e公共垃圾桶出现稀有物品 ";
        RARE_ITEM_CLICK_TEXT = cfg.contains("messages.rare-item-click-text") ? cfg.getString("messages.rare-item-click-text") : "§a(点击查看!)";
        NEW_ITEM_BROADCAST = cfg.contains("messages.new-item-broadcast") ? cfg.getString("messages.new-item-broadcast") : "§2[DfPubBin]§e公共垃圾桶出现新物品 ";
        NEW_ITEM_CLICK_TEXT = cfg.contains("messages.new-item-click-text") ? cfg.getString("messages.new-item-click-text") : "§a(点击查看!)";
        PAGE_TURN_DISABLED = cfg.contains("messages.page-turn-disabled") ? cfg.getString("messages.page-turn-disabled") : "§c已经到边界页，无法继续翻页";
        COMMAND_DISABLED = cfg.contains("messages.command-disabled") ? cfg.getString("messages.command-disabled") : "§c该指令已被禁用";
        COMMAND_USAGE = cfg.contains("messages.command-usage") ? cfg.getString("messages.command-usage") : "§c用法: /pbin <pub|priv>";
        COMMAND_INVALID = cfg.contains("messages.command-invalid") ? cfg.getString("messages.command-invalid") : "§c无效的子指令，请使用 /pbin <pub|priv>";
        TRASH_FULL = cfg.contains("messages.trash-full") ? cfg.getString("messages.trash-full") : "§c垃圾桶已满，无法丢弃更多物品！";
        ITEM_PICKUP_COOLDOWN_MESSAGE = cfg.contains("messages.item-pickup-cooldown") ? cfg.getString("messages.item-pickup-cooldown") : "§c操作频繁，请%seconds%秒后重试";
        DISCARD_CANCEL = cfg.contains("messages.discard-cancel") ? cfg.getString("messages.discard-cancel") : "§c取消丢弃操作";
        DISCARD_SUCCESS = cfg.contains("messages.discard-success") ? cfg.getString("messages.discard-success") : "§a物品已成功丢弃到垃圾桶";
    }
}
