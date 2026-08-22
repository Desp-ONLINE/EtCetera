package org.swlab.etcetera.Listener;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ItemFrameProtectListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFrameBreak(HangingBreakByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame)) {
            return;
        }
        Player player = resolvePlayer(e.getRemover());
        if (player != null && !player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFrameDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame) && !(e.getEntity() instanceof ArmorStand)) {
            return;
        }
        Player player = resolvePlayer(e.getDamager());
        if (player != null && !player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent e) {
        Player player = resolvePlayer(e.getAttacker());
        if (player != null && !player.isOp()) {
            e.setCancelled(true);
        }
    }

    private Player resolvePlayer(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }

}
