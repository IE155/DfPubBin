package org.example1.dfPubBin.command;

import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.rare.RareItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("dfpubbin.reload")) {
            DfPubBinPlugin.getInstance().reloadConfig();
            ConfigManager.load(DfPubBinPlugin.getInstance());
            // 重新初始化稀有物品管理器以加载新的稀有物品列表
            RareItemManager.init();
            // 重启自动清理任务以应用新的配置
            DfPubBinPlugin.getInstance().stopAutoCleanTask();
            DfPubBinPlugin.getInstance().startAutoCleanTask();

            // 重启周期性提醒任务以应用新的配置
            DfPubBinPlugin.getInstance().stopPeriodicReminderTask();
            DfPubBinPlugin.getInstance().startPeriodicReminderTask();

            // 重启掉落物清理任务以应用新的配置
            DfPubBinPlugin.getInstance().stopDroppedItemCleanupTask();
            DfPubBinPlugin.getInstance().startDroppedItemCleanupTask();

            // 重新加载禁用原版掉落物刷新机制监听器以应用新的配置
            DfPubBinPlugin.getInstance().reloadItemDespawnListener();

            sender.sendMessage("§a配置文件已重新加载！");
        } else {
            sender.sendMessage("§c你没有权限执行此命令！");
        }
        return true;
    }
}
