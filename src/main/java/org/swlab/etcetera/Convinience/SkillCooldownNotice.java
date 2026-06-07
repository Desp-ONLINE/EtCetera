package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
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
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SkillCooldownNotice {

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

                try {
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
                } catch (Throwable t) {
                    // 한 플레이어의 비정상 아이템(합성무기 등)이 스케줄러 전체를 죽이지 않도록 보호
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

        // 합성무기는 등록된 템플릿(getMMOItem)이 없어 NPE 가 났음.
        // 실제 손에 든 아이템(NBT)에서 능력치를 읽도록 LiveMMOItem 사용.
        LiveMMOItem liveMMOItem = new LiveMMOItem(itemInMainHand);
        if (!liveMMOItem.hasData(ItemStats.ABILITIES)) return heldAbilities;
        StatData data = liveMMOItem.getData(ItemStats.ABILITIES);
        AbilityListData abilityList = (AbilityListData) data;
        if (abilityList == null) return heldAbilities;

        injectCooldownGroup(itemInMainHand);

        for (AbilityData ability : abilityList.getAbilities()) {
            String name = ability.getAbility().getName();
            String replacedName = name.replace(" ", "_").toLowerCase();
            TrackedSkill ts = tracked.computeIfAbsent(replacedName, k -> new TrackedSkill());
            ts.weaponDisplayName = displayName;
            heldAbilities.add(replacedName);
        }

        return heldAbilities;
    }

    private static void injectCooldownGroup(ItemStack item) {
        // 쿨타임 그룹 주입은 베스트-에포트.
        // 실패해도(합성무기 등 비정상 데이터 컴포넌트) 능력치 추적과 기본 쿨타임 표시는 계속돼야 하므로 전부 감싼다.
        try {
            // 1.21.4 신규 custom_model_data 포맷에서 레거시 getCustomModelData() 가
            // NoSuchElementException 을 던질 수 있으므로 별도로 방어.
            int cmd = 0;
            try {
                if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                    cmd = item.getItemMeta().getCustomModelData();
                }
            } catch (Throwable ignored) {
                cmd = 0;
            }

            String groupName = "w_" + item.getType().name().toLowerCase() + "_" + cmd;
            NamespacedKey groupKey = new NamespacedKey("etcetera", groupName);

            UseCooldown existing = item.getData(DataComponentTypes.USE_COOLDOWN);
            if (existing != null && groupKey.equals(existing.cooldownGroup())) return;

            UseCooldown uc = UseCooldown.useCooldown(0.01f).cooldownGroup(groupKey).build();
            item.setData(DataComponentTypes.USE_COOLDOWN, uc);
        } catch (Throwable t) {
            // 그룹 주입 실패 시 무시 (setCooldown 은 재질 기반으로라도 동작)
        }
    }

    private static boolean matchesWeaponFilter(String displayName) {
        String stripped = ChatColor.stripColor(displayName);
        return stripped.contains("특수무기") || stripped.contains("합성무기");
    }
}
