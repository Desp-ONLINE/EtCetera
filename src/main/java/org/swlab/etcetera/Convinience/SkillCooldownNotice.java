package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SkillCooldownNotice {

    private static final String GROUP_NAMESPACE = "etcetera";
    // 아이템 인스턴스마다 박는 고유 UID -> 쿨타임 그룹이 무기마다 무조건 달라져 안 섞임
    private static final NamespacedKey UID_KEY = new NamespacedKey(GROUP_NAMESPACE, "cooldown_uid");

    private static final Map<UUID, Map<String, TrackedSkill>> trackedPerPlayer = new HashMap<>();

    private static class TrackedSkill {
        double previousCooldown;
        String weaponDisplayName;
    }

    public static void scheduleStart() {
        Bukkit.getScheduler().runTaskTimer(EtCetera.getInstance(), () -> {
            trackedPerPlayer.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (EtCetera.getChannelType().equals("lobby")
                        && player.getWorld().getName().equals("fishing")) {
                    trackedPerPlayer.remove(player.getUniqueId());
                    continue;
                }

                Map<String, TrackedSkill> tracked = trackedPerPlayer
                        .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

                Set<String> heldAbilities = refreshHeldWeaponAbilities(player, tracked);
                ItemStack held = player.getInventory().getItemInMainHand();

                Iterator<Map.Entry<String, TrackedSkill>> it = tracked.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, TrackedSkill> entry = it.next();
                    String replacedName = entry.getKey();
                    TrackedSkill ts = entry.getValue();

                    String raw = PlaceholderAPI.setPlaceholders(
                            player, "%mythiclib_cooldown_skill_" + replacedName + "%");
                    double current;
                    try {
                        current = Double.parseDouble(raw);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if (ts.previousCooldown <= 0.01 && current > 0.01
                            && heldAbilities.contains(replacedName)
                            && UserSettingRepository.getInstance().isShowSkillCooldownItem(player)) {
                        int ticks = (int) Math.round(current * 20);
                        if (ticks > 0) {
                            player.setCooldown(held, ticks);
                        }
                    }

                    if (ts.previousCooldown > 0.01 && current <= 0.01
                            && ts.weaponDisplayName != null
                            && matchesWeaponFilter(ts.weaponDisplayName)
                            && UserSettingRepository.getInstance().isShowSkillCooldownNotice(player)) {
                        player.sendMessage(ColorManager.format(
                                "#FFC233[알림] &f" + ts.weaponDisplayName + " #10FF5D스킬이 준비되었습니다!"));
                    }

                    ts.previousCooldown = current;
                }
            }
        }, 20L, 5L);
    }

    private static Set<String> refreshHeldWeaponAbilities(Player player, Map<String, TrackedSkill> tracked) {
        Set<String> heldAbilities = new HashSet<>();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() == Material.AIR) return heldAbilities;

        String id = MMOItems.getID(itemInMainHand);
        Type type = MMOItems.getType(itemInMainHand);
        if (type == null || !type.getId().equals("SWORD")) return heldAbilities;

        if (!itemInMainHand.hasItemMeta() || !itemInMainHand.getItemMeta().hasDisplayName()) return heldAbilities;
        String displayName = itemInMainHand.getItemMeta().getDisplayName();
        if (!matchesWeaponFilter(displayName)) return heldAbilities;

        // 합성무기는 템플릿(getMMOItem)이 없어 requireNonNull 에서 NPE -> 실제 아이템(NBT)에서 읽는다.
        LiveMMOItem liveMMOItem = new LiveMMOItem(itemInMainHand);
        if (!liveMMOItem.hasData(ItemStats.ABILITIES)) return heldAbilities;
        StatData data = liveMMOItem.getData(ItemStats.ABILITIES);
        AbilityListData abilityList = (AbilityListData) data;
        if (abilityList == null) return heldAbilities;

        // 비정상 아이템에서 예외가 나도 추적은 계속되도록 감싼다.
        try {
            injectCooldownGroup(player, itemInMainHand);
        } catch (Throwable ignored) {
        }

        for (AbilityData ability : abilityList.getAbilities()) {
            String name = ability.getAbility().getName();
            String replacedName = name.replace(" ", "_").toLowerCase();
            TrackedSkill ts = tracked.computeIfAbsent(replacedName, k -> new TrackedSkill());
            ts.weaponDisplayName = displayName;
            heldAbilities.add(replacedName);
        }

        return heldAbilities;
    }

    // 손에 든 무기에 '인스턴스 고유 UID 기반 쿨타임 그룹'을 박고 실제 인벤토리에 반영(write-back).
    // PDC(UID)와 쿨타임 그룹을 모두 ItemMeta API 로만 설정한다(setData 와 섞으면 한쪽이 날아감).
    private static void injectCooldownGroup(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String uid = pdc.get(UID_KEY, PersistentDataType.STRING);
        boolean metaChanged = false;
        if (uid == null || uid.isEmpty()) {
            uid = UUID.randomUUID().toString();
            pdc.set(UID_KEY, PersistentDataType.STRING, uid);
            metaChanged = true;
        }

        // UUID 문자열(소문자 16진수+하이픈)은 NamespacedKey 에 그대로 써도 유효.
        NamespacedKey groupKey = new NamespacedKey(GROUP_NAMESPACE, "w_" + uid);

        boolean groupOk = meta.hasUseCooldown()
                && groupKey.equals(meta.getUseCooldown().getCooldownGroup());

        // UID 도 그룹도 이미 박혀 있으면 매 틱 다시 쓰지 않는다(플리커 방지).
        if (!metaChanged && groupOk) return;

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownSeconds(1.0f);
        cd.setCooldownGroup(groupKey);
        meta.setUseCooldown(cd);
        item.setItemMeta(meta);

        // getItemInMainHand() 가 복사본을 줄 수 있으므로 실제 인벤토리에 다시 써넣어야 영구 반영됨.
        player.getInventory().setItemInMainHand(item);
    }

    private static boolean matchesWeaponFilter(String displayName) {
        String stripped = ChatColor.stripColor(displayName);
        return stripped.contains("특수무기") || stripped.contains("합성무기");
    }
}
