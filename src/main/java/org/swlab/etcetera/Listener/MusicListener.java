package org.swlab.etcetera.Listener;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.quests.Quest;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MusicListener implements Listener {

    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();

    private String MUSIC_WORLD = "uisounds:phartenon";
    private String MUSIC_RAID = "uisounds:raid";
    private String MUSIC_DUNGEON = "uisounds:dungeon";


    @EventHandler
    public void onServerJoin(PlayerJoinEvent e) {
            playSound(e.getPlayer(), e.getPlayer().getWorld());

    }
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        cancelTask(e.getPlayer());
        playSound(e.getPlayer(), e.getPlayer().getWorld());
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.getPlayer().isOnline()) {
                    this.cancel();
                    playerTasks.remove(e.getPlayer().getUniqueId());
                    return;
                }
                playSound(e.getPlayer(), e.getPlayer().getWorld());
            }
        };
        task.runTaskTimer(EtCetera.getInstance(), 301 * 20L, 301 * 20L); // 5분(300초) 간격 실행
        playerTasks.put(e.getPlayer().getUniqueId(), task);

    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // 플레이어가 서버를 떠나면 타이머 중지
        cancelTask(e.getPlayer());
    }

    private void playSound(Player player, World world) {
        if(EtCetera.getChannelType().equals("lobby")){
            if(world.getName().equals("world")){
                player.playSound(player.getLocation(), MUSIC_WORLD, SoundCategory.PLAYERS,1.0f, 1.0f);
            } else if (world.getName().equals("raid") || world.getName().equals("pvp")){
                player.playSound(player.getLocation(), MUSIC_RAID, SoundCategory.PLAYERS, 1.0f, 1.0f);
            } else {
                return;
            }
        } else if (EtCetera.getChannelType().equals("dungeon")){
            player.playSound(player.getLocation(), MUSIC_DUNGEON, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

    }

    private void cancelTask(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerTasks.containsKey(uuid)) {
            playerTasks.get(uuid).cancel();
            playerTasks.remove(uuid);
        }
    }
}
