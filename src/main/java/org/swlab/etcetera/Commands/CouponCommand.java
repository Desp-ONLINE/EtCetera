package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class CouponCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(strings.length == 0) {
            player.sendMessage("§7 > /쿠폰 <코드> - 코드를 입력하여 쿠폰을 수령하세요.");
            return false;

        }
        CommandUtil.runCommandAsOP(player, "coupon "+strings[0]);
        return true;
    }
}
