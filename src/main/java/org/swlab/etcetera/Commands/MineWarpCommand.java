package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Random;

public class MineWarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "워프 이동 광산_입구");
            return false;
        }
        else{
            Random random = new Random();
            int i = random.nextInt(0, 2);
            if (i == 0) {
                CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 광산_입구");
            }
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby2 워프 이동 광산_입구");
            return false;
        }
    }
}
