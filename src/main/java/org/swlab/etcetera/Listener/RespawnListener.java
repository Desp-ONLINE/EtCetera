package org.swlab.etcetera.Listener;

import com.binggre.mmodungeon.api.MMODungeonAPI;
import com.binggre.mmodungeon.repository.DungeonRepository;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class RespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if(EtCetera.getChannelType().equals("lobby")){
            World world = Bukkit.getWorld("world");
            if(e.getPlayer().getWorld().equals(Bukkit.getWorld("raid"))){
                System.out.println("ASDgadsg");
                return;
            }
            Location location = new Location(world, -21.475, 37.0000, -737.459, -90.7f, 1.9f);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().teleport(location);
                }
            }, 10L);

        }
        if(EtCetera.getChannelType().equals("dungeon")){
            CommandUtil.runCommandAsOP(e.getPlayer(), "채널 워프 lobby spawn");
        }
    }
}
