package org.swlab.etcetera.Listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


public class LeapListener implements Listener {
    private final HashMap<UUID, Long> lastJumpTime = new HashMap<>();

    @EventHandler
    public void onLeap(PlayerSwapHandItemsEvent e) {
        
        Player p = e.getPlayer();
        ArrayList<String> sword = new ArrayList(Arrays.asList(Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD, Material.WOODEN_SWORD, Material.STONE_SWORD));
        if (sword.contains(p.getInventory().getItemInMainHand().getType()) && !p.isSneaking()) {
            long currentTimeMillis = System.currentTimeMillis();
            UUID uniqueId = p.getUniqueId();
            long lastTime = lastJumpTime.getOrDefault(uniqueId, 0L);
            if (currentTimeMillis - lastTime < 3000) {
                return;
            }
            Vector vector;
            if(EtCetera.getChannelType().equals("lobby")){
                if(p.getWorld().getName().equals("pvp")){
                    vector = p.getLocation().getDirection().multiply(1.5).setY(0.5);
                }
                else {
                    vector = p.getLocation().getDirection().multiply(3).setY(0.5);

                }

            }
            else {
                vector = p.getLocation().getDirection().multiply(2).setY(0.5);
            }
            p.setVelocity(vector);
            lastJumpTime.put(uniqueId, currentTimeMillis);
            long EXPIRATION_TIME = 60000; // 60초
            lastJumpTime.entrySet().removeIf(entry -> (currentTimeMillis - entry.getValue()) > EXPIRATION_TIME);
            e.setCancelled(true);
        }
        if(p.isSneaking()){
            e.setCancelled(true);
            CommandUtil.runCommandAsOP(p, "gui open 메뉴");
            return;
        }

    }
}
