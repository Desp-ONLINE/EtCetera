package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Repositories.WeeklyRaidLimitRepository;

public class WeeklyRaidCountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        int clearCount = WeeklyRaidLimitRepository.getInstance().getClearCount(player);
        int remainCount = Math.max(0, WeeklyRaidLimitRepository.MAX_WEEKLY_CLEAR - clearCount);

        player.sendMessage("§6[주간 레이드] §f이번 주 클리어 횟수: §e" + clearCount + "§7/§e" + WeeklyRaidLimitRepository.MAX_WEEKLY_CLEAR);
        if (remainCount > 0) {
            player.sendMessage("§6[주간 레이드] §f남은 클리어 가능 횟수: §a" + remainCount + "회");
        } else {
            player.sendMessage("§6[주간 레이드] §c이번 주 클리어 가능 횟수를 모두 사용하였습니다. §7(월요일 초기화)");
        }
        return true;
    }
}
