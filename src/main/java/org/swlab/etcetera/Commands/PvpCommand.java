package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class PvpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(EtCetera.getChannelType().equals("lobby")){
            Player player = (Player) sender;
            CommandUtil.runCommandAsOP(player, "워프 이동 PVP");
            return false;
        }
        else {
            Player player = (Player) sender;
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 PVP");
            return false;
        }
    }
}
