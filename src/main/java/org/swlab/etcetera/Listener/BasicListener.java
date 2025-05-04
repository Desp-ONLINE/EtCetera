package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.api.MMODungeonAPI;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastComponentVelocityListener;
import com.binggre.velocitysocketclient.listener.BroadcastVelocityListener;
import de.kinglol12345.GUIPlus.events.GUIClickEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
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
//        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
//            if (!(e.getEntity() instanceof Player)) {
//                e.getEntity().remove();
//            }
//        }
        //로비채널에서 world에선 데미지 입는거 cancel
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (e.getWorld().getName().equals("raid")) {

        }
//        e.getChunk().addPluginChunkTicket(EtCetera.getInstance());
    }

    @EventHandler
    public void onMythicHitEvent(MythicDamageEvent e) {
        if (!e.getTarget().getWorld().getName().equals("raid")) {
            return;
        }
        SkillCaster caster = e.getCaster();
        AbstractEntity target = e.getTarget();
        if (caster.getEntity().isPlayer() && target.isPlayer()) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            if (mmoCoreAPI.isInSameParty((Player) caster.getEntity().getBukkitEntity(), (Player) target.getBukkitEntity())) {
                e.setCancelled(true);
            }
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
        if (e.getProjectile().getData().getCaster().getEntity().isPlayer()) {
            if (e.getEntity().isPlayer()) {
                MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                if (mmoCoreAPI.isInSameParty((Player) e.getProjectile().getData().getCaster().getEntity().asPlayer().getBukkitEntity(), (Player) e.getEntity().asPlayer().getBukkitEntity())) {
                    e.setCancelled(true);
                }
            }
        }

        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                if (e.getEntity().isPlayer()) {
                    e.setCancelled(true);
                }
            }
        }
        if (EtCetera.getChannelType().equals("lobby")) {
            String worldName = "pvp";
            if (!e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                    if (e.getEntity().isPlayer()) {
                        e.setCancelled(true);
                    }
                }
            }
            if (e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getEntity().getLocation().getY() >= 18) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void skillOnVillage(PlayerCastSkillEvent e) {
        if (EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing") || e.getPlayer().getWorld().getName().equals("jump"))) {
            e.setCancelled(true);
        }

    }




    @EventHandler
    public void onGuiClick(GUIClickEvent e) {
        InventoryClickEvent inventoryClickEvent = e.getInventoryClickEvent();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        player.playSound(player, "uisounds:buttonclick2", 1.0F, 1.0F);
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent e) {
        Player player = e.getPlayer();
        player.playSound(player, "uisounds:congratulations", 1.0F, 1.0F);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        if ((!e.getPlayer().hasPlayedBefore() && EtCetera.getChannelType().equals("lobby"))) {
            String text = " §f摩 #8FFFAE" + e.getPlayer().getName() + " 님께서 서버에 &e첫 접속 #8FFFAE하셨습니다! 환영해주세요!";
            String format = ColorManager.format(text);
            Bukkit.broadcastMessage(format);

            VelocityClient.getInstance().getConnectClient().send(FirstJoinVelocityListener.class, format);
        }

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                e.getPlayer().clearActivePotionEffects();
            }
        }, 20L);
        if (EtCetera.getInstance().getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(e.getPlayer(), "spawn");
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).setClassPoints(999);

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "huds layout " + e.getPlayer().getName() + " remove slot_hud-layout");
                e.getPlayer().setNoDamageTicks(0);
                e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
            }
        }, 20L);

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
        if (e.getDamager() instanceof Player attacker) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) { // 기본공격 캔슬
                e.setCancelled(true);
                return;
            }
            if (e.getEntity() instanceof LivingEntity victim) {
                PotionEffect potionEffect = victim.getPotionEffect(PotionEffectType.BAD_OMEN);
                if (potionEffect != null) {
                    e.setDamage(damage + damage * (potionEffect.getAmplifier() + 1) * 10 / 100);
                }
            }
            if (e.getEntity() instanceof Zombie) {
                if (mmoCoreAPI.getPlayerData(attacker).getProfess().getId().equals("파우스트")) {
                    e.setDamage(damage + damage * 8 / 100);
                }
                if (mmoCoreAPI.getPlayerData(attacker).getProfess().getId().equals("제피르")) {
                    e.setDamage(damage - damage * 8 / 100);
                }
            }
            if (e.getEntity() instanceof Cow) {
                if (mmoCoreAPI.getPlayerData(attacker).getProfess().getId().equals("인페르노")) {
                    e.setDamage(damage + damage * 5 / 100);
                }

            }
            if (e.getEntity() instanceof Player victim) {
                if (mmoCoreAPI.getPlayerData(attacker).getProfess().getId().equals("제피르")) {
                    damage = damage + damage * 10 / 100;
                    e.setDamage(damage);
                }
                e.setDamage(damage - damage * 55 / 100);
            }
            double fixedDamage = Math.round(e.getDamage());
            attacker.sendTitle("", "§f                                                                   ᎈ §c" + fixedDamage, 5, 10, 5);
        }
    }

    @EventHandler
    public void onFireTick(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
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
