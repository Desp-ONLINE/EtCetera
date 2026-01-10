package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Random;

public class SpawnCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (EtCetera.getChannelType().equals("lobby")) {
            Player player = (Player) sender;
            CommandUtil.runCommandAsOP(player, "warp 로비");
            return false;
        } else {
            Random random = new Random();
            int i = random.nextInt(0, 2);
            Player player = (Player) sender;
            if(i == 0){
                CommandUtil.runCommandAsOP(player, "채널 워프 lobby warp 로비");
            }
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby2 warp 로비");

            return false;
        }
    }
}
