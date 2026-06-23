package org.example1.dfPubBin;

import org.bukkit.scheduler.BukkitTask;
import org.example1.dfPubBin.task.DroppedItemCleanupTask;
import org.example1.dfPubBin.command.PBinCommand;
import org.example1.dfPubBin.command.PBinTabCompleter;
import org.example1.dfPubBin.command.ReloadConfigCommand;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.disablemechanism.ItemDespawnListener;
import org.example1.dfPubBin.listener.GuiListener;
import org.example1.dfPubBin.listener.InventoryListener;
import org.example1.dfPubBin.rare.RareItemManager;
import org.example1.dfPubBin.task.AutoCleanTask;
import org.example1.dfPubBin.task.PeriodicReminderTask;
import org.example1.dfPubBin.yaml.YamlDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DfPubBinPlugin extends JavaPlugin {

    // 单例模式：插件实例
    private static DfPubBinPlugin instance;
    
    // YAML数据管理器
    private YamlDataManager yamlDataManager;
    
    // 自动清理任务
    private BukkitTask autoCleanTask;
    
    // 周期性提醒任务
    private BukkitTask periodicReminderTask;
    
    // 掉落物清理任务
    private BukkitTask droppedItemCleanupTask;
    
    // 禁用原版掉落物刷新机制监听器
    private ItemDespawnListener itemDespawnListener;
    


    @Override

    public void onEnable() {
        // 保存插件实例
        instance = this;

        // 初始化YAML数据管理器
        yamlDataManager = new YamlDataManager(this);
        
        // 初始化稀有物品管理器
        RareItemManager.init();
        
        // 初始化垃圾桶管理器
        GarbageManager.init();

        // 加载默认配置文件
        saveDefaultConfig();
        
        // 加载配置
        ConfigManager.load(this);

        // 从YAML加载数据
        yamlDataManager.loadAllData();

        // 注册 /pbin 命令
        getCommand("pbin").setExecutor(new PBinCommand());
        getCommand("pbin").setTabCompleter(new PBinTabCompleter());

        // 注册 /pbin2load 命令
        getCommand("pbin2load").setExecutor(new ReloadConfigCommand());

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        
        // 初始化并注册禁用原版掉落物刷新机制监听器
        // 始终注册监听器，但监听器内部根据配置值决定是否取消事件
        itemDespawnListener = new ItemDespawnListener();
        getServer().getPluginManager().registerEvents(itemDespawnListener, this);

        // 启动自动清理任务（如果需要）
        startAutoCleanTask();
        
        // 启动周期性提醒任务（如果需要）
        startPeriodicReminderTask();
        
        // 启动掉落物清理任务（如果需要）
        startDroppedItemCleanupTask();
        

        
        getLogger().info("DfPubBin 插件已启用！");
    }

    @Override
    public void onDisable() {
        // 保存数据到YAML
        if (yamlDataManager != null) {
            try {
                yamlDataManager.saveAllData();
            } catch (Exception e) {
                getLogger().severe("保存垃圾桶数据时发生错误: " + e.getMessage());
            }
        }
        
        // 停止所有任务
        stopAutoCleanTask();
        stopPeriodicReminderTask();
        stopDroppedItemCleanupTask();
        
        // 清除插件实例
        instance = null;
        
        getLogger().info("DfPubBin 插件已禁用！");
    }

    // 获取插件实例
    public static DfPubBinPlugin getInstance() {
        return instance;
    }
    
    // 获取YAML数据管理器
    public YamlDataManager getYamlDataManager() {
        return yamlDataManager;
    }
    
    // 启动自动清理任务
    public void startAutoCleanTask() {
        // 如果已有任务在运行，先取消它
        if (autoCleanTask != null && !autoCleanTask.isCancelled()) {
            autoCleanTask.cancel();
        }
        
        // 启动新的自动清理任务
        autoCleanTask = AutoCleanTask.start(this);
    }
    
    // 停止自动清理任务
    public void stopAutoCleanTask() {
        if (autoCleanTask != null && !autoCleanTask.isCancelled()) {
            autoCleanTask.cancel();
            autoCleanTask = null;
        }
    }
    
    // 启动周期性提醒任务
    public void startPeriodicReminderTask() {
        // 如果已有任务在运行，先取消它
        if (periodicReminderTask != null && !periodicReminderTask.isCancelled()) {
            periodicReminderTask.cancel();
        }
        
        // 启动新的周期性提醒任务，保存任务引用以便后续停止
        periodicReminderTask = PeriodicReminderTask.start(this);
    }
    
    // 停止周期性提醒任务
    public void stopPeriodicReminderTask() {
        PeriodicReminderTask.stop();
        periodicReminderTask = null;
    }
    
    // 启动掉落物清理任务
    public void startDroppedItemCleanupTask() {
        // 如果已有任务在运行，先取消它
        if (droppedItemCleanupTask != null && !droppedItemCleanupTask.isCancelled()) {
            droppedItemCleanupTask.cancel();
        }
        
        // 检查是否启用了掉落物清理功能
        if (ConfigManager.DROPPED_ITEM_CLEANUP_ENABLED) {
            // 启动新的掉落物清理任务，保存任务引用以便后续停止
            droppedItemCleanupTask = DroppedItemCleanupTask.start(this);
        }
    }
    
    // 停止掉落物清理任务
    public void stopDroppedItemCleanupTask() {
        if (droppedItemCleanupTask != null && !droppedItemCleanupTask.isCancelled()) {
            droppedItemCleanupTask.cancel();
            droppedItemCleanupTask = null;
        }
    }
    
    // 重新加载禁用原版掉落物刷新机制监听器
    public void reloadItemDespawnListener() {
        // 由于ItemDespawnListener类内部检查配置变量，只需确保监听器已注册
        // 如果监听器尚未注册但配置启用，则注册它
        if (itemDespawnListener == null && ConfigManager.DISABLE_ITEM_DESPAWN_MECHANISM) {
            itemDespawnListener = new ItemDespawnListener();
            getServer().getPluginManager().registerEvents(itemDespawnListener, this);
        }
        // 如果监听器已注册但配置禁用，则无需操作，因为监听器内部会检查配置值
    }
}
