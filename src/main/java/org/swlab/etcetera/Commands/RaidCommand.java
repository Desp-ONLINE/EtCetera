package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class RaidCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(strings.length != 0){
            if(strings[0].equalsIgnoreCase("포기")){
                ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                Bukkit.getServer().dispatchCommand(consoleSender, "인던 중지 "+player.getName());
            } else {
                player.sendMessage("§f /레이드 포기 - 현재 진행중인 레이드를 포기할 수 있습니다.");
            }
            return true;
        }
        player.sendMessage("§7§o /레이드 포기 : 명령어로 레이드를 중간에 포기하실 수 있습니다.");
        if(!EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby gui open 레이드");
            return true;
        }
        CommandUtil.runCommandAsOP(player, "gui open 레이드");
        return false;
    }
}