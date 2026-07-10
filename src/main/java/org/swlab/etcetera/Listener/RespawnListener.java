package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RespawnListener implements Listener {

    private static final long RESPAWN_INVINCIBLE_MILLIS = 3000L;
    private static final Map<UUID, Long> respawnTimes = new ConcurrentHashMap<>();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        respawnTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        if(EtCetera.getChannelType().equals("lobby")){
            if(e.getPlayer().getWorld().equals(Bukkit.getWorld("raid"))){
                return;
            }
            World world = Bukkit.getWorld("world");
            Location location = new Location(world, -21.475, 37.0000, -737.459, -90.7f, 1.9f);
            e.setRespawnLocation(location);

        }
        Player player = e.getPlayer();
        if(EtCetera.getChannelType().equals("dungeon")){
            Random random = new Random();
            int i = random.nextInt(0, 2);
            if (i == 0) {
                CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 던전");
            }
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby2 워프 이동 던전");
        }
    }

    @EventHandler
    public void onDamageAfterRespawn(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        Long respawnedAt = respawnTimes.get(player.getUniqueId());
        if (respawnedAt == null) {
            return;
        }
        if (System.currentTimeMillis() - respawnedAt <= RESPAWN_INVINCIBLE_MILLIS) {
            e.setCancelled(true);
        } else {
            respawnTimes.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        respawnTimes.remove(e.getPlayer().getUniqueId());
    }
}
