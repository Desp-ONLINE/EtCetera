package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Random;

public class GuildRaidCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if(!EtCetera.getChannelType().equals("lobby")){
            player.sendMessage("§c 로비에서만 이용하실 수 있는 명령어 입니다.");
            return true;
        }
        CommandUtil.runCommandAsOP(player, "gui open 길드레이드");

        return true;
    }
}
