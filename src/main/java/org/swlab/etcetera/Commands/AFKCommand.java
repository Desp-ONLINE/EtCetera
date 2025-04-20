package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class AFKCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "채널 이동 afk");
        }
        else {
            CommandUtil.runCommandAsOP(player, "채널 이동 afk");
        }
        return false;
    }
}
