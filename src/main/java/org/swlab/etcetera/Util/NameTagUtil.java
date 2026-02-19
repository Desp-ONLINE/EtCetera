package org.swlab.etcetera.Util;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.swlab.etcetera.EtCetera;

public class NameTagUtil {

    public static void setPlayerNameTag(Player player) {
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (!player.isOnline()) {
                return;
            }

            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerData playerData = mmoCoreAPI.getPlayerData(player);
            int level = playerData.getLevel();

            TextColor levelColor;
            if (level < 20) {
                levelColor = TextColor.fromHexString("#BDFFB9");
            } else if (level < 45) {
                levelColor = TextColor.fromHexString("#FFFC9B");
            } else if (level < 70) {
                levelColor = TextColor.fromHexString("#F1ADFF");
            } else if (level < 100) {
                levelColor = TextColor.fromHexString("#AC7EFF");
            } else if (level < 130) {
                levelColor = TextColor.fromHexString("#7ECBFF");
            } else if (level < 160) {
                levelColor = TextColor.fromHexString("#7A89FF");
            } else {
                levelColor = TextColor.fromHexString("#5D64CB");
            }

            Component prefix = Component.text("[Lv." + level + "] ", levelColor);

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            String teamName = "nt_" + player.getUniqueId().toString().replace("-", "").substring(0, 13);
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            team.prefix(prefix);
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
        }, 20L);
    }
}
