package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.NumberUtil;
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
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DamageListener implements Listener {


    //    HashMap<String, Double> damageMultipliers = new HashMap<>(Map.of("adamas_genesis", 0.08, "adamas_dominance", 0.15));
    ArrayList<String> damageMultipliers = new ArrayList<>(Arrays.asList("adamas"));

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

        if (!e.getAttacker().getPlayer().isOp()) {
            if (e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        double damage = e.getDamage().getDamage();


        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getAttacker() instanceof Player && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        if (EtCetera.getChannelType().equals("dungeon") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
            return;
        }


        Player attacker = e.getAttacker().getPlayer();
        String profess = mmoCoreAPI.getPlayerData(attacker).getProfess().getId();
        LivingEntity victim = e.getEntity();


        // 보스데미지 연산 ( 우선 1순위 )
        MMOPlayerData mmoPlayerData = MMOPlayerData.get(attacker);

        if (victim.getType().equals(EntityType.COW)) {
            double customBossdamage = mmoPlayerData.getStatMap().getStat("CUSTOM_BOSSDAMAGE");
            damage += damage * (customBossdamage) / 100;
        }


        // 나약함 연산 ( 유저가 가하는 ㅔㄷ미지랑 관련 x )
        if (victim instanceof Player player && e.getAttacker() instanceof Cow boss) {
            if (boss.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                PotionEffect potionEffect1 = boss.getPotionEffect(PotionEffectType.WEAKNESS);
                damage -= damage * (potionEffect1.getAmplifier() + 1) * 10 / 100;
            }
        }
        // 포션효과 ( 흉조/힘 :: 우선 공식 2순위. 흉조가 먼저 연산됨. )
        PotionEffect potionEffect = victim.getPotionEffect(PotionEffectType.BAD_OMEN);
        if (potionEffect != null) {
            if (profess.equals("페이탈")) {
                damage += damage * (potionEffect.getAmplifier() + 1) * 20 / 100;
            } else {
                damage += damage * (potionEffect.getAmplifier() + 1) * 15 / 100;
            }
        }
        if (e.getEntity() instanceof Zombie) {
            if (profess.equals("파우스트")) {
                damage += damage * 11 / 100;
            }
            if (profess.equals("제피르")) {
                damage += damage * 8 / 100;
            }
        }
        if (e.getEntity() instanceof Cow) {
            if (profess.equals("인페르노")) {
                if (e.getEntity().getFireTicks() > 0) {
                    damage += damage * 15 / 100;

                } else {
                    damage += damage * 5 / 100;
                }
            } else if (profess.equals("판")) {
                damage += damage * 5 / 100;
            }
        }
        if (attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            damage += damage * ((attacker.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() + 1) * 10) / 100;
        }
        ActiveMob mythicMobInstance = MythicBukkit.inst().getMobManager().getMythicMobInstance(e.getEntity());
        if (mythicMobInstance == null) {
            return;
        }

        // 최종데미지 증가
        AuraRegistry auraRegistry = mythicMobInstance.getAuraRegistry();

        double multiply = 1;


        for (String auraKey : damageMultipliers) {
            for (String auraKeys : auraRegistry.getAuras().keySet()) {
                if (auraKeys.startsWith(auraKey)) {
                    String replace = auraKeys.replace(auraKey + "_", "");
                    double i = Double.parseDouble(replace);
                    multiply += (i / 100);
                }
            }
        }
        damage *= multiply;


//        if (e.getAttack().getDamage().getInitialPacket().hasType(DamageType.WEAPON)) {
//            e.getAttack().getDamage().registerSkillCriticalStrike();
//        }
        boolean skillCriticalStrike = e.getAttack().getDamage().isSkillCriticalStrike();
//        if (attacker.isOp()) {
//            attacker.sendMessage(""+skillCriticalStrike);
//            if(skillCriticalStrike){
//                MMOPlayerData mmoPlayerData = MMOPlayerData.get(attacker);
//
//                double skillCriticalStrikePower = mmoPlayerData.getStatMap().getStat("SKILL_CRITICAL_STRIKE_POWER");
//                attacker.sendMessage(skillCriticalStrikePower + "");
//                attacker.sendMessage("" + damage);
//                damage *= 1 + (skillCriticalStrikePower / 100);
//            }
//
//
//        }

        double originalDamage = e.getDamage().getDamage();
        DamageMetadata damageMetadata = e.getDamage().add(damage - originalDamage);
        double fixedDamage = Math.round(damageMetadata.getDamage());


        if (skillCriticalStrike) {

            if (UserSettingRepository.getInstance().isShowDamageChat(attacker)) {
                if (victim instanceof Cow) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            attacker.sendMessage("    " + victim.getName() + "§f : ᎌ §6" + NumberUtil.applyComma(fixedDamage) + " §cDamage §7(남은 체력: " + NumberUtil.applyComma(BigDecimal.valueOf(victim.getHealth())) + " | " + String.format("%.2f", victim.getHealth() / victim.getMaxHealth() * 100) + "%)");
                        }
                    }, 1L);
                }

            }
            e.getAttacker().getPlayer().sendTitle("", criticalDamageString(fixedDamage), 5, 10, 5);
            return;
        }
        if (UserSettingRepository.getInstance().isShowDamageChat(attacker)) {
            if (UserSettingRepository.getInstance().isShowDamageChat(attacker)) {
                if (victim instanceof Cow) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            attacker.sendMessage("    " + victim.getName() + "§f : ᎈ §c" + NumberUtil.applyComma(fixedDamage) + " §cDamage §7(남은 체력: " + NumberUtil.applyComma(BigDecimal.valueOf(victim.getHealth())) + " | " + String.format("%.2f", victim.getHealth() / victim.getMaxHealth() * 100) + "%)");
                        }
                    }, 1L);
                }
            }
        }
        attacker.sendTitle("", damageString(fixedDamage), 5, 10, 5);

    }

    public String criticalDamageString(double fixedDamage) {
        return "§f                                                                   ᎌ §6" + fixedDamage;
    }

    public String damageString(double fixedDamage) {
        return "§f                                                                   ᎈ §c" + fixedDamage;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelInstantAttack(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getDamager() instanceof Player) { // 기본공격 캔슬
            e.setCancelled(true);
            return;
        }
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing") || e.getEntity().getWorld().getName().equals("tuto")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onMythicHitEvent(MythicDamageEvent e) {

        SkillCaster caster = e.getCaster();
        AbstractEntity target = e.getTarget();
        if (caster.getEntity().isPlayer() && target.isPlayer()) {
            e.setCancelled(true);
        }


    }
}
