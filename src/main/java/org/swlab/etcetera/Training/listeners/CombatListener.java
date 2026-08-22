package org.swlab.etcetera.Training.listeners;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import net.Indyuce.mmoitems.MMOItems;
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

    /** 시전 아이템 식별 정보. itemId 는 MMOItems 아이템이 아니면 null */
    public record SourceItem(String itemId, String name) {
    }

    private static final SourceItem UNKNOWN = new SourceItem(null, "알 수 없음");
    private static final SourceItem UNKNOWN_SKILL = new SourceItem(null, "스킬 (알 수 없음)");

    // 스킬 데미지의 AttackMetadata.getAttacker()는 캐스트 시점 SkillMetadata.getCaster()와
    // 동일 인스턴스이므로, 캐스터 메타데이터를 키로 데미지를 시전 아이템에 귀속한다.
    // AttackMetadata가 소멸하면 GC가 엔트리를 정리하므로 별도 정리 로직이 필요 없다.
    private static final Map<StatProvider, SourceItem> CAST_ITEMS = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSkillCast(PlayerCastSkillEvent event) {
        Player player = event.getPlayer();
        if (TrainingController.get(player) == null) {
            return;
        }
        SkillMetadata metadata = event.getMetadata();
        PlayerMetadata caster = metadata.getCaster();
        SourceItem item = handItem(player, caster.getActionHand());

        CAST_ITEMS.put(caster, item);
        // 평타 등 기존 공격에 트리거된 스킬은 데미지가 원본 AttackMetadata에 합산되므로 그쪽 공격자도 매핑
        if (metadata.hasAttackSource() && metadata.getAttackSource().hasAttacker()) {
            CAST_ITEMS.put(metadata.getAttackSource().getAttacker(), item);
        }
    }

    /**
     * 한 번의 타격을 전투 분석에서 어떤 항목으로 집계할지 판별한다.
     * DamageListener 가 최종 데미지와 함께 호출한다.
     */
    public static SourceItem sourceOf(StatProvider attacker, DamageMetadata damage) {
        if (!damage.hasType(DamageType.SKILL)) {
            return UNKNOWN;
        }
        SourceItem item = CAST_ITEMS.get(attacker);
        return item != null ? item : UNKNOWN_SKILL;
    }

    private SourceItem handItem(Player player, EquipmentSlot hand) {
        ItemStack item = hand == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return new SourceItem(null, "맨손");
        }
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                ? item.getItemMeta().getDisplayName()
                : item.getType().name();
        return new SourceItem(MMOItems.getID(item), name);
    }
}
