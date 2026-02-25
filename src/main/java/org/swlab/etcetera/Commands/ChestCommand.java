package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class ChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(strings.length == 0){
            CommandUtil.runCommandAsOP(player, "gui open 창고");
            return true;
        }
        String string = strings[0];
        if(isNumberic(string)){
            Bukkit.dispatchCommand(player, "추가창고 "+string);
            return true;
        }
        player.sendMessage("§c 잘못된 명령어 입니다. §7§o(/창고 <번호>)");

        return true;
    }
    public static boolean isNumberic(String str) {
        return str.matches("[+-]?\\d*(\\.\\d+)?");
    }
}
