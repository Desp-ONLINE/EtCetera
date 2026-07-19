package org.swlab.etcetera.Commands;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class BlinkCommand implements CommandExecutor {

    private static final double MAX_DIST = 15.0;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (strings.length == 0) {
            player.sendMessage("§c사용법: /점멸 <최대거리>");
            return true;
        }

        double dist;
        try {
            dist = Double.parseDouble(strings[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c거리는 숫자로 입력해주세요. §7(사용법: /점멸 <최대거리>)");
            return true;
        }
        dist = Math.min(Math.max(dist, 1.0), MAX_DIST);

        Location dest = safeBlink(player, dist);
        if (dest == null) {
            player.sendMessage("§c이동할 수 있는 안전한 위치가 없습니다.");
            return true;
        }

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0);
        player.teleport(dest);
        player.getWorld().spawnParticle(Particle.PORTAL, dest.clone().add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0);
        player.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        return true;
    }

    public Location safeBlink(Player p, double maxDist) {
        Location eye = p.getEyeLocation();
        RayTraceResult hit = p.getWorld().rayTraceBlocks(
                eye, eye.getDirection(), maxDist,
                FluidCollisionMode.NEVER, true); // true = 통과 불가 블록만

        double dist = (hit != null)
                ? hit.getHitPosition().distance(eye.toVector()) - 0.5 // 벽 앞 0.5칸 여유
                : maxDist;

        Location dest = eye.clone().add(eye.getDirection().multiply(Math.max(0, dist)));
        dest.subtract(0, p.getEyeHeight(), 0); // 눈 높이 → 발 높이 보정
        return findSafe(dest);
    }

    // 도착 지점이 블록 안이면 위로 최대 2칸 올려보고, 그래도 막혀 있으면 null
    private Location findSafe(Location dest) {
        Block feet = dest.getBlock();
        for (int i = 0; i < 2 && isBlocked(feet); i++) {
            feet = feet.getRelative(BlockFace.UP);
        }
        if (isBlocked(feet)) {
            return null;
        }
        Location safe = feet.getLocation().add(0.5, 0, 0.5);
        safe.setYaw(dest.getYaw());
        safe.setPitch(dest.getPitch());
        return safe;
    }

    private boolean isBlocked(Block feet) {
        return !feet.isPassable() || !feet.getRelative(BlockFace.UP).isPassable();
    }
}
