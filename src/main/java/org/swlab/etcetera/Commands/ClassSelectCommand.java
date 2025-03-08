package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class ClassSelectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!EtCetera.getChannelType().equals("lobby")){
            sender.sendMessage("§c 로비에서만 직업을 변경할 수 있습니다!");
            return false;
        }
        Player player = (Player) sender;
        CommandUtil.runCommandAsOP(player, "spawn");
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                CommandUtil.runCommandAsOP(player, "gui open 전직");
            }
        }, 10L);
        return true;
    }
}
