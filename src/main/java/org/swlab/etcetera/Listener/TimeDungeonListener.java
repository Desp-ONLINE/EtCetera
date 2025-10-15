package org.swlab.etcetera.Listener;

import com.binggre.mmodungeon.api.DungeonFailedEvent;
import com.binggre.mmoguild.MMOGuild;
import com.binggre.mmoguild.objects.Guild;
import com.binggre.mmoguild.objects.PlayerGuild;
import com.binggre.mmoguild.repository.GuildRepository;
import com.binggre.mmoguild.repository.PlayerGuildRepository;
import com.binggre.mmotimeraid.api.TimeRaidClearEvent;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;


public class TimeDungeonListener implements Listener {

    private final GuildRepository gr = MMOGuild.getPlugin().getGuildRepository();
    private final PlayerGuildRepository pgr = MMOGuild.getPlugin().getPlayerRepository();

    @EventHandler
    public void onTimeRaidClear(TimeRaidClearEvent e) {
        if (e.getDifficulty() <= 6) {
            expLogic(e, 1);
        } else if (e.getDifficulty() <= 9) {
            expLogic(e, 2);
        }
    }

    private void expLogic(TimeRaidClearEvent e, int exp) {
        for (Player player : e.getPlayers()) {
            PlayerGuild playerGuild = pgr.get(player.getUniqueId());
            if (playerGuild == null) {
                continue;
            }
            Guild guild = playerGuild.getGuild();
            if (guild == null) {
                continue;
            }
            guild.getInfo().addExp(exp);
            guild.updateRedis();
            // ㄱㄱ요
            gr.saveAsync(guild);
        }
    }
}