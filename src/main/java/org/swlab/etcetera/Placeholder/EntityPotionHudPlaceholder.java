package org.swlab.etcetera.Placeholder;

import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.bukkit.update.BukkitEventUpdateEvent;
import kr.toxicity.hud.api.placeholder.HudPlaceholder;
import kr.toxicity.hud.api.update.UpdateEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.List;

/**
 * BetterHud 팝업(entity_attack 등)의 대상 엔티티가 가진 포션 효과를 읽는 플레이스홀더.
 * 내장 potion_effect_duration은 플레이어 본인만 읽으므로,
 * 팝업을 발동시킨 이벤트의 엔티티(=타격한 보스)를 기준으로 읽는 버전을 등록한다.
 *
 * 사용 예 (hud 설정):
 *   conditions: first: entity_potion_duration:wither / second: 0 / operation: '>'
 *   pattern: "[entity_potion_duration:wither@floor(t//20)]"
 *   pattern: "[entity_potion_amplifier:poison]"  (0부터 시작, 없으면 -1)
 *
 * 슬롯 채움 방식(왼쪽부터 빈칸 없이)용:
 *   entity_potion_effect_at:<n>   — n번째(1부터) 활성 효과의 키 (예: "wither"), 없으면 ""
 *   entity_potion_duration_at:<n> — n번째 활성 효과의 남은 틱, 없으면 0
 *   정렬은 효과 키 알파벳순(지속시간 변화로 아이콘이 자리를 바꾸지 않도록)
 */
public class EntityPotionHudPlaceholder {

    public void register() {
        var numbers = BetterHudAPI.inst().getPlaceholderManager().getNumberContainer();
        var strings = BetterHudAPI.inst().getPlaceholderManager().getStringContainer();

        HudPlaceholder.<Number>builder()
                .requiredArgsLength(1)
                .function((args, event) -> {
                    PotionEffectType type = resolveEffect(args.get(0));
                    LivingEntity entity = eventEntity(event);
                    if (entity == null) return player -> 0;
                    return player -> {
                        PotionEffect effect = entity.getPotionEffect(type);
                        if (effect == null) return 0;
                        // 무한 지속(-1)은 조건식 '> 0'이 통과하도록 큰 값으로 치환
                        return effect.getDuration() < 0 ? 999999 : effect.getDuration();
                    };
                })
                .add("entity_potion_duration", numbers);

        HudPlaceholder.<Number>builder()
                .requiredArgsLength(1)
                .function((args, event) -> {
                    PotionEffectType type = resolveEffect(args.get(0));
                    LivingEntity entity = eventEntity(event);
                    if (entity == null) return player -> -1;
                    return player -> {
                        PotionEffect effect = entity.getPotionEffect(type);
                        return effect == null ? -1 : effect.getAmplifier();
                    };
                })
                .add("entity_potion_amplifier", numbers);

        HudPlaceholder.<String>builder()
                .requiredArgsLength(1)
                .function((args, event) -> {
                    int index = Integer.parseInt(args.get(0));
                    LivingEntity entity = eventEntity(event);
                    if (entity == null) return player -> "";
                    return player -> {
                        PotionEffect effect = effectAt(entity, index);
                        return effect == null ? "" : effect.getType().getKey().getKey();
                    };
                })
                .add("entity_potion_effect_at", strings);

        HudPlaceholder.<Number>builder()
                .requiredArgsLength(1)
                .function((args, event) -> {
                    int index = Integer.parseInt(args.get(0));
                    LivingEntity entity = eventEntity(event);
                    if (entity == null) return player -> 0;
                    return player -> {
                        PotionEffect effect = effectAt(entity, index);
                        if (effect == null) return 0;
                        return effect.getDuration() < 0 ? 999999 : effect.getDuration();
                    };
                })
                .add("entity_potion_duration_at", numbers);
    }

    private static PotionEffect effectAt(LivingEntity entity, int index) {
        if (index < 1) return null;
        List<PotionEffect> sorted = entity.getActivePotionEffects().stream()
                .sorted(Comparator.comparing(e -> e.getType().getKey().toString()))
                .toList();
        return index <= sorted.size() ? sorted.get(index - 1) : null;
    }

    private static PotionEffectType resolveEffect(String name) {
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());
        PotionEffectType type = key == null ? null : Registry.EFFECT.get(key);
        if (type == null) throw new RuntimeException("존재하지 않는 포션 효과: " + name);
        return type;
    }

    private static LivingEntity eventEntity(UpdateEvent event) {
        if (event.source() instanceof BukkitEventUpdateEvent bukkit
                && bukkit.event() instanceof EntityEvent entityEvent
                && entityEvent.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }
}
