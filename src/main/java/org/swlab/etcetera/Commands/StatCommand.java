package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * /스텟                          → MMOCore 속성 GUI
 * /스텟 분배 <스텟> <수치|전부>   → 보유 스텟 포인트를 해당 속성에 직접 분배
 */
public class StatCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§f > ";
    private static final Set<String> ALL_KEYWORDS = Set.of("전부", "모두", "all", "max", "최대");

    /**
     * 서버 MMOCore 속성 ID 가 한글 표시명과 다를 경우를 위한 폴백 별칭.
     * 입력값이 속성 ID / 표시명과 직접 일치하지 않을 때만 사용된다.
     */
    private static final Map<String, List<String>> ALIASES = Map.of(
            "공격력", List.of("strength", "attack", "damage", "power", "str"),
            "체력", List.of("vitality", "health", "hp", "constitution", "vit"),
            "마나", List.of("intelligence", "mana", "mp", "int", "wisdom"),
            "민첩성", List.of("dexterity", "agility", "dex", "agi", "민첩")
    );

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            CommandUtil.runCommandAsOP(player, "attributes");
            return true;
        }

        if (args[0].equals("분배")) {
            return allocate(player, args);
        }

        sendUsage(player);
        return true;
    }

    private boolean allocate(Player player, String[] args) {
        if (args.length < 3) {
            sendUsage(player);
            return true;
        }

        PlayerAttribute attribute = resolveAttribute(args[1]);
        if (attribute == null) {
            player.sendMessage(PREFIX + "§c'" + args[1] + "' 스텟을 찾을 수 없습니다.");
            player.sendMessage(PREFIX + "§7사용 가능: §f" + String.join("§7, §f", attributeNames()));
            return true;
        }

        PlayerData data = PlayerData.get(player);
        PlayerAttributes.AttributeInstance instance = data.getAttributes().getInstance(attribute);
        int available = data.getAttributePoints();
        int base = instance.getBase();
        int remainingToMax = attribute.hasMax() ? Math.max(0, attribute.getMax() - base) : Integer.MAX_VALUE;
        String name = stripColor(attribute.getName());

        if (available < 1) {
            player.sendMessage(PREFIX + "§c분배할 수 있는 스텟 포인트가 없습니다.");
            return true;
        }
        if (remainingToMax <= 0) {
            player.sendMessage(PREFIX + "§c" + name + " §c스텟은 이미 최대치§7(" + attribute.getMax() + ")§c입니다.");
            return true;
        }

        int amount;
        if (ALL_KEYWORDS.contains(args[2].toLowerCase(Locale.ROOT))) {
            amount = Math.min(available, remainingToMax);
        } else {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + "§c수치는 1 이상의 정수 또는 '전부' 여야 합니다.");
                return true;
            }
            if (amount < 1) {
                player.sendMessage(PREFIX + "§c수치는 1 이상이어야 합니다.");
                return true;
            }
            if (amount > available) {
                player.sendMessage(PREFIX + "§c스텟 포인트가 부족합니다. §7(보유: §f" + available + "§7, 요청: §f" + amount + "§7)");
                return true;
            }
            if (amount > remainingToMax) {
                player.sendMessage(PREFIX + "§c" + name + " §c스텟 최대치를 초과합니다. §7(현재: §f" + base
                        + "§7 / 최대: §f" + attribute.getMax() + "§7, 분배 가능: §f" + remainingToMax + "§7)");
                return true;
            }
        }

        // MMOCore AttributeView 의 분배 로직과 동일
        instance.addBase(amount);
        data.giveAttributePoints(-amount);
        attribute.updateAdvancement(data, instance.getBase());

        String maxText = attribute.hasMax() ? " §7/ " + attribute.getMax() : "";
        player.sendMessage(PREFIX + "§a" + name + " §f스텟에 §e" + amount + " §f포인트를 분배했습니다. §7(현재: §f"
                + instance.getBase() + maxText + "§7, 남은 포인트: §f" + data.getAttributePoints() + "§7)");
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(PREFIX + "§e/스텟 §7- 스텟 GUI 열기");
        player.sendMessage(PREFIX + "§e/스텟 분배 <스텟> <수치|전부> §7- 스텟 포인트 분배");
        player.sendMessage(PREFIX + "§7사용 가능: §f" + String.join("§7, §f", attributeNames()));
    }

    /* ===== 속성 조회 ===== */

    private static PlayerAttribute resolveAttribute(String input) {
        String query = input.toLowerCase(Locale.ROOT);

        // 1) 속성 ID 또는 표시명 직접 일치
        for (PlayerAttribute attribute : MMOCore.plugin.attributeManager.getAll()) {
            if (attribute.getId().equalsIgnoreCase(query)
                    || stripColor(attribute.getName()).equalsIgnoreCase(query)) {
                return attribute;
            }
        }

        // 2) 한글 별칭 폴백 (서버 속성 ID 가 영문인 경우)
        List<String> candidates = ALIASES.get(input);
        if (candidates != null) {
            for (String id : candidates) {
                PlayerAttribute attribute = MMOCore.plugin.attributeManager.get(id);
                if (attribute != null) return attribute;
            }
        }
        return null;
    }

    private static List<String> attributeNames() {
        List<String> names = new ArrayList<>();
        for (PlayerAttribute attribute : MMOCore.plugin.attributeManager.getAll()) {
            names.add(stripColor(attribute.getName()));
        }
        return names;
    }

    private static String stripColor(String text) {
        if (text == null) return "";
        return text.replaceAll("(?i)[§&]x(?:[§&][0-9a-f]){6}", "")
                .replaceAll("(?i)[§&][0-9a-fk-orx]", "")
                .replaceAll("<[^>]+>", "")
                .trim();
    }

    /* ===== 탭 완성 ===== */

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(List.of("분배"), args[0]);
        }
        if (args.length == 2 && args[0].equals("분배")) {
            return filter(attributeNames(), args[1]);
        }
        if (args.length == 3 && args[0].equals("분배")) {
            return filter(Arrays.asList("1", "5", "10", "전부"), args[2]);
        }
        return Collections.emptyList();
    }

    private static List<String> filter(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) result.add(option);
        }
        return result;
    }
}
