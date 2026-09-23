package org.swlab.etcetera.Util;

import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 합성무기 식별/조회 유틸.
 * 합성무기의 MMOItems ID 는 "합성무기_<이름><강화단계>" 꼴이므로 (예: 합성무기_초월자의대검0)
 * 강화 단계 숫자를 뗀 "합성무기_<이름>" 을 키로 사용해 강화 후에도 같은 무기로 인식한다.
 */
public final class MergedWeaponUtil {

    public static final String PREFIX = "합성무기_";

    /** 자동 연계에서 자동 시전할 스킬의 트리거(플레이어가 직접 조작해 쓰는 액티브 스킬만). */
    private static final Set<String> ACTIVE_TRIGGERS = Set.of(
            TriggerType.RIGHT_CLICK.name(),
            TriggerType.SHIFT_RIGHT_CLICK.name(),
            TriggerType.LEFT_CLICK.name(),
            TriggerType.SHIFT_LEFT_CLICK.name(),
            TriggerType.DROP_ITEM.name(),
            TriggerType.SHIFT_DROP_ITEM.name(),
            TriggerType.SWAP_ITEMS.name(),
            TriggerType.SHIFT_SWAP_ITEMS.name()
    );

    private MergedWeaponUtil() {
    }

    public static boolean isMergedWeapon(ItemStack item) {
        return keyOf(item) != null;
    }

    /** 아이템의 연계 등록 키(강화 단계를 뗀 ID). 합성무기가 아니면 null. */
    public static String keyOf(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        String id = MMOItems.getID(item);
        if (id == null || !id.startsWith(PREFIX)) return null;
        return baseKey(id);
    }

    /** ID 끝의 강화 단계 숫자를 제거한다. */
    public static String baseKey(String id) {
        int end = id.length();
        while (end > 0 && Character.isDigit(id.charAt(end - 1))) end--;
        return id.substring(0, end);
    }

    /** 플레이어 인벤토리(핫바 포함 36칸)에서 키가 일치하는 합성무기를 찾는다. 없으면 null. */
    public static ItemStack findInInventory(Player player, String key) {
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType() == Material.AIR) continue;
            if (key.equals(keyOf(content))) return content;
        }
        return null;
    }

    /** 무기에 박힌 액티브(클릭형) 스킬 목록. 합성무기는 템플릿이 없으므로 실제 아이템 NBT 에서 읽는다. */
    public static List<AbilityData> activeAbilities(ItemStack item) {
        List<AbilityData> result = new ArrayList<>();
        try {
            LiveMMOItem live = new LiveMMOItem(item);
            if (!live.hasData(ItemStats.ABILITIES)) return result;
            StatData data = live.getData(ItemStats.ABILITIES);
            if (!(data instanceof AbilityListData list)) return result;
            for (AbilityData ability : list.getAbilities()) {
                if (ability.getTrigger() != null && ACTIVE_TRIGGERS.contains(ability.getTrigger().name())) {
                    result.add(ability);
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    public static String displayName(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();
        }
        String key = keyOf(item);
        return key != null ? key.substring(PREFIX.length()) : (item == null ? "?" : item.getType().name());
    }

    /** 인벤토리에 없는 무기의 이름을 표시할 때 사용. 0강 템플릿이 있으면 그 이름, 없으면 키의 이름 부분. */
    public static String displayNameOfKey(String key) {
        try {
            ItemStack template = MMOItems.plugin.getItem("SWORD", key + "0");
            if (template == null) template = MMOItems.plugin.getItem("SWORD", key);
            if (template != null && template.hasItemMeta() && template.getItemMeta().hasDisplayName()) {
                return template.getItemMeta().getDisplayName();
            }
        } catch (Throwable ignored) {
        }
        return key.startsWith(PREFIX) ? key.substring(PREFIX.length()) : key;
    }

    public static String stripColor(String s) {
        return s == null ? "" : ChatColor.stripColor(s);
    }
}
