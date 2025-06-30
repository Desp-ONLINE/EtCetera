package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class RaidCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(!EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby gui open 레이드");
            return true;
        }
        CommandUtil.runCommandAsOP(player, "gui open 레이드");
        return false;
    }
}