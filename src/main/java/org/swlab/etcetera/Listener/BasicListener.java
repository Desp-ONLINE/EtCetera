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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            if (!(e.getEntity() instanceof Player)) {
                e.getEntity().remove();
            }
        }
        //로비채널에서 world에선 데미지 입는거 cancel
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFishingRodHitPlayer(PlayerFishEvent e) {
        if (e.getCaught() != null) {
            if (e.getCaught() instanceof Player) {
                e.getPlayer().sendMessage("§c 낚싯대로 사람을 낚지 마세요! 인생을 낚으세요!");
                return;
            }
        }
    }

    @EventHandler
    public void cancelPlayerDebuff(MythicProjectileHitEvent e) {
        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
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
    public void skillOnVillage(PlayerCastSkillEvent e) {
        if (EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing"))) {
            e.setCancelled(true);
        }
    }

    //    @EventHandler
//    public void onsellitem(AuctionPostAdminRemoveEvent e){
//        Player player = e.getPlayer();
//    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
//        e.setJoinMessage("§a[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에 접속하셨습니다!");
        e.setJoinMessage("");
        CommandUtil.runCommandAsOP(e.getPlayer(), "spawn");
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).setClassPoints(999);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
//        e.setQuitMessage("§c[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에서 퇴장하셨습니다.");
        e.setQuitMessage("");
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelInstantAttack(EntityDamageByEntityEvent e) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        double damage = e.getDamage();
        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Cow && e.getEntity() instanceof Player) {
            String className = mmoCoreAPI.getPlayerData((Player) e.getEntity()).getProfess().getId();
            if (className.equals("크루세이더")) {
                e.setDamage(damage - damage * 10 / 100);
            }
        }
        if (e.getDamager() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) { // 기본공격 캔슬
                e.setCancelled(true);
                return;
            }
            Player player = (Player) e.getDamager();
            if (e.getEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e.getEntity();
                PotionEffect potionEffect = livingEntity.getPotionEffect(PotionEffectType.BAD_OMEN);
                if (potionEffect != null) {
                    e.setDamage(damage + damage * (potionEffect.getAmplifier()+1) / 100);
                }
            }
            if (e.getEntity() instanceof Zombie) {
                if (mmoCoreAPI.getPlayerData((Player) e.getDamager()).getProfess().getId().equals("파우스트")) {
                    e.setDamage(damage + damage * 10 / 100);
                }
            }
            if (e.getEntity() instanceof Cow) {
                if (mmoCoreAPI.getPlayerData((Player) e.getDamager()).getProfess().getId().equals("인페르노")) {
                    e.setDamage(damage + damage * 5 / 100);
                }
            }
            if(e.getEntity() instanceof Player){
                if (mmoCoreAPI.getPlayerData((Player) e.getDamager()).getProfess().getId().equals("제피르")) {
                    e.setDamage(damage + damage * 10 / 100);
                }
            }
            double fixedDamage = Math.round(e.getDamage());
            player.sendTitle("", "§f                                                                   ᎈ §c" + fixedDamage, 5, 10, 5);
        }
    }
    @EventHandler
    public void onFireTick(BlockIgniteEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e){
        e.setCancelled(true);
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
            if (e.getClickedBlock() == null) {
                return;
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().isInteractable()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void dontStomp(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        if (block.getType() == Material.FARMLAND && e.getEntity() != null) {
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
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }


}
