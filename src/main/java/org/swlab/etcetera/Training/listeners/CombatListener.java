package org.swlab.etcetera.Training.listeners;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Training.objects.TrainingController;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 전투 분석용 출처(시전 아이템) 추적만 담당한다.
 * 데미지 누적 자체는 EtCetera 의 DamageListener 가 최종 계산값으로 직접 넘겨준다.
 */
public class CombatListener implements Listener {

    // 스킬 데미지의 AttackMetadata.getAttacker()는 캐스트 시점 SkillMetadata.getCaster()와
    // 동일 인스턴스이므로, 캐스터 메타데이터를 키로 데미지를 시전 아이템에 귀속한다.
    // AttackMetadata가 소멸하면 GC가 엔트리를 정리하므로 별도 정리 로직이 필요 없다.
    private static final Map<StatProvider, String> CAST_ITEM_NAMES = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSkillCast(PlayerCastSkillEvent event) {
        Player player = event.getPlayer();
        if (TrainingController.get(player) == null) {
            return;
        }
        SkillMetadata metadata = event.getMetadata();
        PlayerMetadata caster = metadata.getCaster();
        String itemName = handItemName(player, caster.getActionHand());

        CAST_ITEM_NAMES.put(caster, itemName);
        // 평타 등 기존 공격에 트리거된 스킬은 데미지가 원본 AttackMetadata에 합산되므로 그쪽 공격자도 매핑
        if (metadata.hasAttackSource() && metadata.getAttackSource().hasAttacker()) {
            CAST_ITEM_NAMES.put(metadata.getAttackSource().getAttacker(), itemName);
        }
    }

    /**
     * 한 번의 타격을 전투 분석에서 어떤 항목으로 집계할지 판별한다.
     * DamageListener 가 최종 데미지와 함께 호출한다.
     */
    public static String sourceOf(StatProvider attacker, DamageMetadata damage) {
        if (!damage.hasType(DamageType.SKILL)) {
            return "알 수 없음";
        }
        String itemName = CAST_ITEM_NAMES.get(attacker);
        return itemName != null ? itemName : "스킬 (알 수 없음)";
    }

    private String handItemName(Player player, EquipmentSlot hand) {
        ItemStack item = hand == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return "맨손";
        }
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().name();
    }
}
