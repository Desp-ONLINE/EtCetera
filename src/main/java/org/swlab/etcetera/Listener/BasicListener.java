package org.swlab.etcetera.Listener;

import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicPlayerAttackEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class BasicListener implements Listener {
    @EventHandler
    public void cancelInstantAttack(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
            e.setCancelled(true);
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
            return;
        }
        if(e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            if(!(e.getEntity() instanceof Player)){
                e.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void cancelPlayerDebuff(MythicProjectileHitEvent e) {
        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
//            System.out.println("+==========================");
//            System.out.println("e.getEntity().isPlayer() = " + e.getEntity().isPlayer());
//            System.out.println("e.getEntity().getName() = " + e.getEntity().getName());
//            System.out.println("e.getEntity() = " + e.getEntity());
//            System.out.println("++==========================");
                if (e.getEntity().isPlayer()) {
                    e.setCancelled(true);
                }
            }
        }
        if (EtCetera.getChannelType().equals("lobby")) {
            String worldName = "타격방지월드이름";
            if (!e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                    if (e.getEntity().isPlayer()) {
                        e.setCancelled(true);
                    }
                }
            }

        }

    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
//        e.setJoinMessage("§a[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에 접속하셨습니다!");
        e.setJoinMessage("");
        CommandUtil.runCommandAsOP(e.getPlayer(), "spawn");
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
//        e.setQuitMessage("§c[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에서 퇴장하셨습니다.");
        e.setQuitMessage("");
    }


    @EventHandler
    public void cancelInstantAttack(EntityDamageByEntityEvent e) {
        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) { // 기본공격 캔슬
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void cancelThrowItem(PlayerDropItemEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelInteractItem(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setKeepInventory(true);
        e.setDeathMessage(null);
    }

    @EventHandler
    public void cancelCraftItem(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelItemSwap(PlayerSwapHandItemsEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelHungerChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelItemConsume(PlayerItemConsumeEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }



}
