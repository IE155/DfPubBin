package org.example1.dfPubBin.command;

import org.example1.dfPubBin.config.ConfigManager;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.data.GarbageType;
import org.example1.dfPubBin.gui.GarbageGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PBinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // 检查是否启用了 /pbin 指令
        if (!ConfigManager.ENABLE_PBIN_COMMAND) {
            sender.sendMessage(ConfigManager.COMMAND_DISABLED);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ConfigManager.COMMAND_USAGE);
            return false;
        }

        if (sender instanceof Player player) {
            switch (args[0].toLowerCase()) {
                case "pub":
                    openGarbage(player, GarbageType.PUBLIC, 0);
                    break;
                case "priv":
                    openGarbage(player, GarbageType.PRIVATE, 0);
                    break;
                default:
                    player.sendMessage(ConfigManager.COMMAND_INVALID);
                    return false;
            }
        } else {
            sender.sendMessage("§c ERROR");
        }

        return true;
    }

    /**
     * 打开垃圾桶界面
     * @param player 玩家
     * @param type 垃圾桶类型
     * @param pageIndex 页面索引
     */
    public void openGarbage(Player player, GarbageType type, int pageIndex) {
        GarbageContainer container;
        if (type == GarbageType.PUBLIC) {
            container = GarbageManager.getPublicContainer();
        } else {
            container = GarbageManager.getPrivateContainer(player.getUniqueId());
        }

        player.openInventory(GarbageGui.create(player, container, type, pageIndex));
    }
}
