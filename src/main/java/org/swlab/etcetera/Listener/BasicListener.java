package org.swlab.etcetera.Listener;

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

public class BasicListener implements Listener{
    @EventHandler
    public void cancelInstantAttack(EntityDamageEvent e){
        if(e.getCause() == EntityDamageEvent.DamageCause.FIRE){
            e.setCancelled(true);
            return;
        }
        if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
            e.setCancelled(true);
            return;
        }
    }
    @EventHandler
    public void cancelInstantAttack(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){ // 기본공격 캔슬
                e.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void cancelThrowItem(PlayerDropItemEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void cancelInteractItem(PlayerInteractEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void cancelCraftItem(CraftItemEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void cancelItemSwap(PlayerSwapHandItemsEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void cancelHungerChange(FoodLevelChangeEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void cancelBlockPlace(BlockPlaceEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void cancelBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void cancelItemConsume(PlayerItemConsumeEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }



}
