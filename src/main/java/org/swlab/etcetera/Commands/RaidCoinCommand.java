package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Dto.RaidCoinDataDTO;
import org.swlab.etcetera.Repositories.RaidCoinRepository;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class RaidCoinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player sender = (Player) commandSender;
        if (strings.length != 0) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player == null) {
                sender.sendMessage("§c 미접속중");
                return false;
            }
            if (sender.hasPermission("*")) {
                String s1 = "";
                if (strings.length >= 3) {

                    s1 = strings[1] + " " + strings[2];
                } else {
                    s1 = strings[1];
                }
                if (strings.length == 3) {
                    RaidCoinRepository.getInstance().giveNormalReward(player, strings[1] + " " + strings[2]);
                    RaidCoinRepository.getInstance().giveSpecialReward(player, strings[1] + " " + strings[2]);
                    RaidCoinRepository.getInstance().updateUserRaidData(player, strings[1] + " " + strings[2]);
                } else {
                    RaidCoinRepository.getInstance().giveNormalReward(player, strings[1]);
                    RaidCoinRepository.getInstance().giveSpecialReward(player, strings[1]);
                    RaidCoinRepository.getInstance().updateUserRaidData(player, strings[1]);
                }

            }
            return true;
        }
        LinkedHashMap<String, RaidCoinDataDTO> raidCoinDataCache = RaidCoinRepository.getInstance().raidCoinDataCache;

        sender.sendMessage("§e§n                                                                                                                     §f");
        sender.sendMessage("");
        sender.sendMessage("§e    [ 각 보스 별 보스 코인 드롭 정보 ]    ");
        sender.sendMessage("");

        for (String string : raidCoinDataCache.keySet()) {
            RaidCoinDataDTO raidCoinDataDTO = raidCoinDataCache.get(string);
            String raidName = raidCoinDataDTO.getRaidName();
            sender.sendMessage(ColorManager.format("    §6" + raidName + "§f: #B6FFC1보스 코인 §6" + raidCoinDataDTO.getNormalAmount() + "§f개 / §c상급 보스 코인 §6" + raidCoinDataDTO.getSpecialAmount() + "§f개"));
        }
        sender.sendMessage("");
        sender.sendMessage("§e    이번 주 획득한 최고 보상의 레이드: §f" + RaidCoinRepository.getInstance().raidCoinPlayerCache.get(sender.getUniqueId().toString()).getHighestClearedRaid());
        sender.sendMessage("");
        sender.sendMessage("§c    보스 코인은 홀로 레이드를 격파 한 경우에만 인정되며, 매주 월요일 초기화 됩니다. 자세한 내용은 위키를 참고해주세요.");
        sender.sendMessage("§e§n                                                                                                                     §f");

        return false;
    }
}