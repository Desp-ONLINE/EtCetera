package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class AFKCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length>=1) {
            String nickname = strings[0];
            Player player1 = Bukkit.getPlayer(nickname);
            if(player1==null){
                return false;
            }
            CommandUtil.runCommandAsOP(player1, "채널 이동 afk");
        }
        Player player = (Player) commandSender;

        CommandUtil.runCommandAsOP(player, "채널 이동 afk");
        return false;
    }

}
