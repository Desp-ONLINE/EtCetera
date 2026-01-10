package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Random;

public class DailyQuestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if(EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "gui open 일일퀘스트");
            return true;
        }
        Random random = new Random();
        int i = random.nextInt(0, 2);
        if (i == 0) {
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby gui open 일일퀘스트");
        }
        CommandUtil.runCommandAsOP(player, "채널 워프 lobby2 gui open 일일퀘스트");
        return true;
    }
}
