package org.swlab.etcetera.Listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;


public class JumpListener implements Listener{
    private final HashMap<UUID, Long> lastJumpTime = new HashMap<>();
    @EventHandler
    public void onJump(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if(p.getGameMode()==GameMode.CREATIVE){
            return;
        }
        if(!p.isOnGround()){
//            e.setCancelled(true);
            if(!p.isOp()){
                p.setAllowFlight(false);
            }
            long currentTimeMillis = System.currentTimeMillis();
            UUID uniqueId = p.getUniqueId();
            long lastTime = lastJumpTime.getOrDefault(uniqueId, 0L);
            if(currentTimeMillis - lastTime < 2000){
                return;
            }
            Vector vector = p.getLocation().getDirection().multiply(3).setY(0.5);
            p.setVelocity(vector);
            lastJumpTime.put(uniqueId, currentTimeMillis);
            long EXPIRATION_TIME = 60000; // 60초
            lastJumpTime.entrySet().removeIf(entry -> (currentTimeMillis - entry.getValue()) > EXPIRATION_TIME);
        }

    }
}
