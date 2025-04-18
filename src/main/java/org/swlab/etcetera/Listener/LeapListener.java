package org.swlab.etcetera.Listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
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
        if (p.isSneaking()) {
            e.setCancelled(true);
            CommandUtil.runCommandAsOP(p, "gui open 메뉴");
            return;
        }

    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 서바이벌/어드벤처 모드 + 지상일 때
        if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
                && player.isOnGround()) {

            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();

            // 쿨타임이 끝났으면 비행 허용
            if (!cooldowns.containsKey(uuid) || now - cooldowns.get(uuid) >= 2000) {
                player.setAllowFlight(true);
            } else {
                player.setAllowFlight(false); // 쿨타임 중엔 비행 불가
            }
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        // 쿨타임 검사 (2초 = 2000ms)
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < 2000) {
            event.setCancelled(true);
            return;
        }

        // 이제 진짜 점프 처리 시작


        Vector vector;
        if (EtCetera.getChannelType().equals("lobby")) {
            if (player.getWorld().getName().equals("pvp")) {
                vector = player.getLocation().getDirection().normalize().multiply(1.3).setY(0.5);
            } else {
                vector = player.getLocation().getDirection().normalize().multiply(2.2).setY(0.5);
            }
        } else {
            vector = player.getLocation().getDirection().normalize().multiply(3.2).setY(0.5);
        }

        player.setVelocity(vector);
        cooldowns.put(uuid, now);
        player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2.0f, 2.2f);

        player.getWorld().spawnParticle(
                Particle.CLOUD,                                  // 파티클 종류
                player.getLocation().add(0, 0.1, 0),              // 위치 (살짝 발 밑)
                20,                                               // 입자 개수
                0.2, 0.05, 0.2,                                   // 확산 범위 (X, Y, Z)
                0.01                                              // 속도
        );
    }
}
