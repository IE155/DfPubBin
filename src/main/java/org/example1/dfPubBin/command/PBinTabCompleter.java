package org.example1.dfPubBin.command;

import org.example1.dfPubBin.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PBinTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        
        // 如果指令被禁用，返回空列表
        if (!ConfigManager.ENABLE_PBIN_COMMAND) {
            return null;
        }
        
        // 如果是第一个参数（子命令）
        if (args.length == 1) {
            return Arrays.asList("pub", "priv");
        }
        
        // 其他情况返回空列表
        return null;
    }
}