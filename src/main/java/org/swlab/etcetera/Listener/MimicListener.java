package org.swlab.etcetera.Listener;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.swlab.etcetera.Dto.MimicDataDTO;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.MimicRepository;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class MimicListener implements Listener {

    private static final long OWNERSHIP_DURATION_MS = 10_000L; // 10초
    private static final double HEALTH_MULTIPLIER = 20.0;
    private static final double DAMAGE_MULTIPLIER = 10.0;

    // 미믹 엔티티 UUID → 소유자 플레이어 UUID
    private final HashMap<UUID, UUID> mimicOwners = new HashMap<>();
    // 미믹 엔티티 UUID → 소유권 만료 시각(ms)
    private final HashMap<UUID, Long> mimicExpiry = new HashMap<>();
    // 미믹 엔티티 UUID → 고정 데미지 (엘리트 데미지 × 10)
    private final HashMap<UUID, Double> mimicDamageMap = new HashMap<>();

    @EventHandler
    public void onEliteDeath(MythicMobDeathEvent e) {
        String internalName = e.getMobType().getInternalName();

        MimicDataDTO data = MimicRepository.getInstance().getByEliteName(internalName);
        if (data == null) return;

        LivingEntity killer = e.getKiller();
        if (!(killer instanceof Player player)) return;

        if (new Random().nextInt(100) >= data.getSpawnChance()) return;

        // 엘리트의 체력/데미지 기록 (사망 전 값)
        LivingEntity eliteEntity = (LivingEntity) e.getEntity();
        var eliteHealthAttr = eliteEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        final double eliteMaxHealth = eliteHealthAttr != null ? eliteHealthAttr.getValue() : 100.0;
        final double eliteDamage = MythicBukkit.inst().getMobManager()
                .getActiveMob(e.getEntity().getUniqueId())
                .map(ActiveMob::getDamage)
                .orElse(10.0);

        Location loc = e.getEntity().getLocation();
        String mimicName = data.getMimicInternalName();

        MythicBukkit.inst().getMobManager().getMythicMob(mimicName).ifPresent(mythicMob -> {
            ActiveMob spawnedMimic = MythicBukkit.inst().getMobManager().spawnMob(mimicName, loc, 1);
            UUID mimicUUID = spawnedMimic.getEntity().getUniqueId();

            // 체력: 엘리트의 20배
            LivingEntity mimicEntity = (LivingEntity) spawnedMimic.getEntity().getBukkitEntity();
            double mimicMaxHealth = eliteMaxHealth * HEALTH_MULTIPLIER;
            var mimicHealthAttr = mimicEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (mimicHealthAttr != null) {
                mimicHealthAttr.setBaseValue(mimicMaxHealth);
            }
            mimicEntity.setHealth(mimicMaxHealth);

            // 데미지: 엘리트의 10배 (고정값으로 저장, 공격 시 적용)
            double mimicDamage = eliteDamage * DAMAGE_MULTIPLIER;
            mimicDamageMap.put(mimicUUID, mimicDamage);

            mimicOwners.put(mimicUUID, player.getUniqueId());
            mimicExpiry.put(mimicUUID, System.currentTimeMillis() + OWNERSHIP_DURATION_MS);

            player.sendMessage("§6[미믹] §f미믹이 나타났습니다! §e10초§f간 소유권을 가집니다.");

            // 10초 후 소유권 자동 만료
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                mimicOwners.remove(mimicUUID);
                mimicExpiry.remove(mimicUUID);
            }, 200L);
        });
    }

    @EventHandler
    public void onMimicDeath(MythicMobDeathEvent e) {
        String internalName = e.getMobType().getInternalName();

        MimicDataDTO data = MimicRepository.getInstance().getByMimicName(internalName);
        if (data == null) return;

        UUID mimicUUID = e.getEntity().getUniqueId();
        mimicOwners.remove(mimicUUID);
        mimicExpiry.remove(mimicUUID);
        mimicDamageMap.remove(mimicUUID);

        LivingEntity killer = e.getKiller();
        if (killer instanceof Player player) {
            MimicRepository.getInstance().giveReward(player, data);
        }
    }

    // 미믹이 공격할 때 데미지를 엘리트×10으로 고정
    @EventHandler(priority = EventPriority.HIGH)
    public void onMimicDealDamage(MythicDamageEvent e) {
        SkillCaster caster = e.getCaster();
        if (caster.getEntity().isPlayer()) return;

        UUID casterUUID = caster.getEntity().getUniqueId();
        Double fixedDamage = mimicDamageMap.get(casterUUID);
        if (fixedDamage == null) return;

        e.setDamage(fixedDamage);
    }

    // 플레이어 스킬 공격 차단 (소유권 외 플레이어)
    @EventHandler(priority = EventPriority.HIGH)
    public void onMimicAttackedBySkill(PlayerAttackEvent e) {
        UUID victimUUID = e.getEntity().getUniqueId();
        UUID ownerUUID = getActiveOwner(victimUUID);
        if (ownerUUID == null) return;

        UUID attackerUUID = e.getAttacker().getPlayer().getUniqueId();
        if (!attackerUUID.equals(ownerUUID)) {
            e.setCancelled(true);
            e.getAttacker().getPlayer().sendMessage("§c[미믹] 현재 다른 플레이어가 소유권을 가지고 있습니다.");
        }
    }

    // MythicMobs 스킬 데미지 차단 (소유권 외 플레이어)
    @EventHandler(priority = EventPriority.HIGH)
    public void onMimicReceiveDamage(MythicDamageEvent e) {
        AbstractEntity target = e.getTarget();
        UUID ownerUUID = getActiveOwner(target.getUniqueId());
        if (ownerUUID == null) return;

        SkillCaster caster = e.getCaster();
        if (!caster.getEntity().isPlayer()) return;

        if (!caster.getEntity().getUniqueId().equals(ownerUUID)) {
            e.setCancelled(true);
        }
    }

    /**
     * 소유권이 유효하면 소유자 UUID 반환, 없거나 만료됐으면 null 반환.
     */
    private UUID getActiveOwner(UUID mimicUUID) {
        if (!mimicOwners.containsKey(mimicUUID)) return null;

        if (System.currentTimeMillis() > mimicExpiry.get(mimicUUID)) {
            mimicOwners.remove(mimicUUID);
            mimicExpiry.remove(mimicUUID);
            return null;
        }
        return mimicOwners.get(mimicUUID);
    }
}
