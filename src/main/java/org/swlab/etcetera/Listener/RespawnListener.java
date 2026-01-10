package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Random;

public class RespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
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
}
