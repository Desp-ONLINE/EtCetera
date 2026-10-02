package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.NumberUtil;
import fr.maxlego08.zauctionhouse.api.utils.Priority;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;
import org.swlab.etcetera.Training.listeners.CombatListener;
import org.swlab.etcetera.Training.objects.TrainingController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DamageListener implements Listener {



    @EventHandler
    public void cancelInstantAttack(EntityDamageEvent e) {
        // 데미지 타입 별 수정
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            e.setCancelled(true);
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.LAVA && e.getEntity() instanceof Player player) {
            if (player.getWorld().getName().equals("raid")) {
                double v = player.getMaxHealth() / 10;
                player.damage(v);
                e.setCancelled(true);
                return;
            }
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
            return;
        }

        //로비채널에서 world에선 데미지 입는거 cancel
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing") || e.getEntity().getWorld().getName().equals("tuto")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAttack(PlayerAttackEvent e) {
        PlayerMetadata eAttacker = e.getAttacker();
        LivingEntity eVictim = e.getEntity();
        if (eVictim instanceof Player) {
            if (!EtCetera.getChannelType().equals("pvp")) {
                e.setCancelled(true);
            }
        }
        if (EtCetera.getChannelType().equals("dungeon")) {
            if (eAttacker instanceof Player && eVictim instanceof Player) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cancelInstantAttack(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getDamager() instanceof Player) { // 기본공격 캔슬
            e.setCancelled(true);
            return;
        }
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing") || e.getEntity().getWorld().getName().equals("tuto")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathEvent(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
            player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            e.setCancelled(true);
            Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.setHealth(player.getMaxHealth() * 0.3);
                }
            }, 1L);
            player.sendMessage("§a    특수 효과로 죽음을 한번 면했습니다.");

        }

    }


}
