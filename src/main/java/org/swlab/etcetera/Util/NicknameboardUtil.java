package org.swlab.etcetera.Util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.swlab.etcetera.EtCetera;

public class NicknameboardUtil {

    public static void setPlayerLevelPrefix(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), () -> {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerData playerData = mmoCoreAPI.getPlayerData(player);
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());

            // 이미 팀이 있으면 재사용, 없으면 새로 생성
            if (team == null) {
                team = scoreboard.registerNewTeam(player.getName());
            }

            String job = PlaceholderAPI.setPlaceholders(player, "%Title_class%");

            // prefix 설정 (색깔도 가능)
            team.setPrefix("§f"+job+" §6[Lv." + playerData.getLevel() + "] ");

            // 팀에 플레이어 추가
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }

            // 적용
            player.setScoreboard(scoreboard);
        }, 20L);

    }
}
