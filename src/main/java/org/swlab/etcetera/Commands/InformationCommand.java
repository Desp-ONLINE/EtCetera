package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InformationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOPlayerData playerData = MMOPlayerData.get(player);
        StatMap statMap = playerData.getStatMap();
        double skillDamage = Math.round(statMap.getStat("SKILL_DAMAGE") * 100) / 100.0;
        double health = Math.round(statMap.getStat("MAX_HEALTH") * 100) / 100.0;

        double skillCriticalStrikeChance = statMap.getStat("SKILL_CRITICAL_STRIKE_CHANCE");

        double skillCriticalStrikePower = Math.round(statMap.getStat("SKILL_CRITICAL_STRIKE_POWER") * 100) / 100.0;

        double movementSpeed = Math.round(statMap.getStat("MOVEMENT_SPEED") * 100) / 100.0;

        double additionalExperience = Math.round(statMap.getStat("ADDITIONAL_EXPERIENCE") * 100) / 100.0;
        double customBossdamage = Math.round(statMap.getStat("CUSTOM_BOSSDAMAGE") * 100) / 100.0;

        long maxHealth = Math.round(health);
        NumberFormat numberFormat = NumberFormat.getInstance();
        player.sendMessage("§f > " + player.getName() + " 님의 §a스텟 §f정보입니다.");
        player.sendMessage("§f  ");
        player.sendMessage("§f  ᎈ §7공격력: §f+" + numberFormat.format(skillDamage) + "%");
//        for (String key : statMap.getInstance("SKILL_DAMAGE").getKeys()) {
//            player.sendMessage(key);
//        }
        sendDetailedInfo(player, statMap, "SKILL_DAMAGE");

        player.sendMessage(" ");
        player.sendMessage("§f  ᎏ §c체력: §f+" + numberFormat.format(maxHealth));
        sendDetailedInfo(player, statMap, "MAX_HEALTH");

        player.sendMessage(" ");
        player.sendMessage("§f  ᎋ §e크리티컬 확률: §f+" + numberFormat.format(skillCriticalStrikeChance) + "%");
        sendDetailedInfo(player, statMap, "SKILL_CRITICAL_STRIKE_CHANCE");

        player.sendMessage(" ");
        player.sendMessage("§f  ᎌ §6크리티컬 데미지: §f+" + numberFormat.format(skillCriticalStrikePower) + "%");
        player.sendMessage("§7§o    ㄴ> 크리티컬 데미지의 각 스텟 출처는 현재 표기되지 않습니다.");
//        sendDetailedInfo(player, statMap, "SKILL_CRITICAL_STRIKE_POWER");

        player.sendMessage(" ");
        player.sendMessage("§f  ᎒ §b이동 속도: §f+" + numberFormat.format(movementSpeed) + "%");
        sendDetailedInfo(player, statMap, "MOVEMENT_SPEED");

        player.sendMessage(" ");
        player.sendMessage("§f  Ӻ §a경험치 획득량: §f+" + numberFormat.format(additionalExperience) + "%");
        sendDetailedInfo(player, statMap, "ADDITIONAL_EXPERIENCE");

        player.sendMessage(" ");
        player.sendMessage(ColorManager.format("§f  ｦ #8C5FBA보스 대상 공격력: §f+") + numberFormat.format(customBossdamage) + "%");
        sendDetailedInfo(player, statMap, "CUSTOM_BOSSDAMAGE");

        player.sendMessage("§7§o    ※ /스텟 을 통한 스텟은 따로 표시되지 않습니다.");

        player.sendMessage("");
        return true;
    }

    public void sendDetailedInfo(Player player, StatMap statMap, String statName) {
        Collection<StatModifier> skillDamageModifiers =
                statMap.getInstance(statName).getModifiers();

        /* key = StatModifier.getKey(), value = 합산된 스킬 데미지 */
        Map<String, Double> mergedSkillDamage = new HashMap<>();

        for (StatModifier modifier : skillDamageModifiers) {

            double value = statMap.getInstance(statName)
                    .getFilteredTotal(m -> m.equals(modifier));

            if (value <= 0) continue;

            String key = modifier.getKey();

            mergedSkillDamage.put(
                    key,
                    mergedSkillDamage.getOrDefault(key, 0D) + value
            );
        }

        String resultMessage = "§7§o    ㄴ> ";
        /* 출력 */
        for (Map.Entry<String, Double> entry : mergedSkillDamage.entrySet()) {
            if(getStatSource(entry.getKey()).equals("스텟")){
                continue;
            }
            resultMessage += "§7§o" + getStatSource(entry.getKey()) + ": §f§o" + (String.format("%.1f", entry.getValue()));
            if (!statName.equals("MAX_HEALTH")) {
                resultMessage += "%  |  ";
                continue;
            }
            resultMessage += "  |  ";

        }
        player.sendMessage(resultMessage);


    }

    public String getStatSource(String key) {
        switch (key) {
            case "CollectionBook":
                return "도감";
            case "MMOCollectionBookCouple":
                return "인연";
            case "MMOItems":
                return "(구) 장비 시스템";
            case "Equipment":
                return "장비";
            case "ItemSet":
                return "세트 효과";
            case "Alliance":
                return "얼라이언스";
            case "MMOGuild":
                return "길드";
            case "PetCollection":
                return "펫 도감";
            case "Decoration":
                return "치장";
            case "ContributeTower":
                return "공헌의 탑";
            default:
                return "스텟";
        }
    }
}
