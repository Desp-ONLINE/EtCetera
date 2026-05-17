package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
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
import java.util.Objects;
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
                            && heldAbilities.contains(replacedName)) {
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

        StatData data = Objects.requireNonNull(MMOItems.plugin.getMMOItem(type, id))
                .getData(ItemStats.ABILITIES);
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
        int cmd = item.hasItemMeta() && item.getItemMeta().hasCustomModelData()
                ? item.getItemMeta().getCustomModelData() : 0;
        String groupName = "w_" + item.getType().name().toLowerCase() + "_" + cmd;
        NamespacedKey groupKey = new NamespacedKey("etcetera", groupName);

        UseCooldown existing = item.getData(DataComponentTypes.USE_COOLDOWN);
        if (existing != null && groupKey.equals(existing.cooldownGroup())) return;

        UseCooldown uc = UseCooldown.useCooldown(0.01f).cooldownGroup(groupKey).build();
        item.setData(DataComponentTypes.USE_COOLDOWN, uc);
    }

    private static boolean matchesWeaponFilter(String displayName) {
        String stripped = ChatColor.stripColor(displayName);
        return stripped.contains("특수무기") || stripped.contains("합성무기");
    }
}
