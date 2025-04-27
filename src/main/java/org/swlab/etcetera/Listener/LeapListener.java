package org.swlab.etcetera.Listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


public class LeapListener implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        if (p.isSneaking()) {
            CommandUtil.runCommandAsOP(p, "gui open 메뉴");
            return;
        }

        ArrayList<String> sword = new ArrayList(Arrays.asList(Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD, Material.WOODEN_SWORD, Material.STONE_SWORD));

        if (sword.contains(p.getInventory().getItemInMainHand().getType()) && !p.isSneaking()) {
            UUID uuid = p.getUniqueId();
            if (!isCooldown(uuid)) {
                e.setCancelled(true);
                return;
            }
            Vector vector;
            if (EtCetera.getChannelType().equals("lobby")) {
                if (p.getWorld().getName().equals("pvp")) {
                    vector = p.getLocation().getDirection().normalize().multiply(1.3).setY(0.5);
                } else {
                    vector = p.getLocation().getDirection().normalize().multiply(2.2).setY(0.5);
                }
            } else {
                vector = p.getLocation().getDirection().normalize().multiply(3.2).setY(0.5);
            }
            p.setVelocity(vector);
            addCooldown(uuid);
            runEffect(p);
        }
    }


    public void runEffect(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2.0f, 2.2f);

        player.getWorld().spawnParticle(
                Particle.CLOUD,                                  // 파티클 종류
                player.getLocation().add(0, 0.1, 0),              // 위치 (살짝 발 밑)
                20,                                               // 입자 개수
                0.2, 0.05, 0.2,                                   // 확산 범위 (X, Y, Z)
                0.01                                              // 속도
        );
    }
    public boolean isCooldown(UUID uuid) {
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < 3000) {
            return false;
        }
        return true;
    }
    public void addCooldown(UUID uuid){
        long now = System.currentTimeMillis();
        cooldowns.put(uuid, now);
    }
}
