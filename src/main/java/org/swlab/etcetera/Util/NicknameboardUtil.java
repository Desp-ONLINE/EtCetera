package org.swlab.etcetera.Util;

import com.binggre.binggreapi.utils.ColorManager;
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

            int level = playerData.getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + job + " #BDFFB9[Lv." + playerData.getLevel() + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + job + " #FFFC9B[Lv." + playerData.getLevel() + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + job + " #E257FF[Lv." + playerData.getLevel() + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + job + " #FF6557[Lv." + playerData.getLevel() + "] ");
            } else {
                format = ColorManager.format("§f" + job + " #FF3D3D[Lv." + playerData.getLevel() + "] ");
            }
            team.setPrefix(format);


            // 팀에 플레이어 추가
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }

            // 적용
            player.setScoreboard(scoreboard);
        }, 20L);

    }
}
