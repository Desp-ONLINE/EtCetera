package org.swlab.etcetera.Listener;

import fr.maxlego08.zauctionhouse.api.event.events.AuctionPostAdminRemoveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionSellEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionTransactionEvent;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicPlayerAttackEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.Material.WARPED_DOOR;

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
        //로비채널에서 world에선 데미지 입는거 cancel
        if((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player){
            e.setCancelled(true);
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
    public void skillOnVillage(PlayerCastSkillEvent e){
        if(EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing"))){
            e.setCancelled(true);
        }
    }
//    @EventHandler
//    public void onsellitem(AuctionPostAdminRemoveEvent e){
//        Player player = e.getPlayer();
//    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
//        e.setJoinMessage("§a[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에 접속하셨습니다!");
        e.setJoinMessage("");
        CommandUtil.runCommandAsOP(e.getPlayer(), "spawn");
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).setClassPoints(999);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
//        e.setQuitMessage("§c[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에서 퇴장하셨습니다.");
        e.setQuitMessage("");
    }


    @EventHandler(priority = EventPriority.MONITOR)
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
            Player player = (Player) e.getDamager();
            double damage = Math.round(e.getDamage());
            player.sendTitle("", "§f                                                                   ᎈ §c"+damage, 5, 10, 5);
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
            if(e.getClickedBlock()==null){
                return;
            }
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().isInteractable()){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void dontStomp(EntityChangeBlockEvent e){
        Block block = e.getBlock();
        if(block.getType() == Material.FARMLAND && e.getEntity() != null){
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
    public void cancelItemConsume(PlayerItemConsumeEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }



}
