package org.swlab.etcetera.Listener;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.util.Vector;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


public class LeapListener implements Listener {
    public final static HashMap<UUID, Long> cooldowns = new HashMap<>();

    private static LeapListener instance;

    private MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());

    public static LeapListener getInstance() {
        if (instance == null) {
            instance = new LeapListener();
        }
        return instance;
    }


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

            String job = PlayerData.get(p.getUniqueId()).getProfess().getName();
            UUID uuid = p.getUniqueId();
            if (!isCooldown(uuid)) {
                e.setCancelled(true);
                return;
            }
            Vector vector;
            if(EtCetera.getChannelType().equalsIgnoreCase("pvp")){
                vector = p.getLocation().getDirection().normalize().multiply(1.3).setY(0.3);
            }
            else if (EtCetera.getChannelType().equals("lobby")) {
                if (p.getWorld().getName().equals("adventures")) {
                    return;
                } else {
                    // 로비 도약 거리
                    if(job.equals("루인드") || job.equals("드레드노트")) {
                        vector = p.getLocation().getDirection().normalize().multiply(1.9).setY(0.35);
                    } else {
                        vector = p.getLocation().getDirection().normalize().multiply(2.2).setY(0.5);
                    }
                }
            } else {
                // 던전 채널 도약 거리
                if(job.equals("루인드") || job.equals("드레드노트")) {
                    vector = p.getLocation().getDirection().normalize().multiply(2.7).setY(0.35);
                } else {
                    vector = p.getLocation().getDirection().normalize().multiply(3.2).setY(0.5);
                }
            }
            p.setVelocity(vector);
            addCooldown(uuid);
            runEffect(p);
        }
    }


    public void runEffect(Player player) {
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
        String job = PlayerData.get(uuid).getProfess().getName();
        long now = System.currentTimeMillis();
        if(job.equals("루인드") || job.equals("드레드노트")){
            return !cooldowns.containsKey(uuid) || now - cooldowns.get(uuid) >= 2000;
        }
        return !cooldowns.containsKey(uuid) || now - cooldowns.get(uuid) >= 3000;
    }

    public void addCooldown(UUID uuid) {
        long now = System.currentTimeMillis();
        cooldowns.put(uuid, now);
    }
}
