package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        LinkedHashMap<String, RaidCoinDataDTO> raidCoinDataCache = RaidCoinRepository.getInstance().raidCoinDataCache;

        player.sendMessage("§e§n                                                                                                                     §f");
        player.sendMessage("");
        player.sendMessage("§e    [ 각 보스 별 보스 코인 드롭 정보 ]    ");
        player.sendMessage("");

        for (String string : raidCoinDataCache.keySet()) {
            RaidCoinDataDTO raidCoinDataDTO = raidCoinDataCache.get(string);
            String raidName = raidCoinDataDTO.getRaidName();
            player.sendMessage(ColorManager.format("    §6"+raidName+"§f: #B6FFC1보스 코인 §6"+raidCoinDataDTO.getNormalAmount()+"§f개 / §c상급 보스 코인 §6"+raidCoinDataDTO.getSpecialAmount()+"§f개"));
        }
        player.sendMessage("");
        player.sendMessage("§e    이번 주 획득한 최고 보상의 레이드: §f"+RaidCoinRepository.getInstance().raidCoinPlayerCache.get(player.getUniqueId().toString()).getHighestClearedRaid());
        player.sendMessage("");
        player.sendMessage("§c    보스 코인은 홀로 레이드를 격파 한 경우에만 인정되며, 매주 월요일 초기화 됩니다. 자세한 내용은 위키를 참고해주세요.");
        player.sendMessage("§e§n                                                                                                                     §f");

        return false;
    }
}