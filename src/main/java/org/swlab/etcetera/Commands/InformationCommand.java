package org.swlab.etcetera.Commands;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public class InformationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOPlayerData playerData = MMOPlayerData.get(player);
        StatMap statMap = playerData.getStatMap();
        double skillDamage = Math.round(statMap.getStat("SKILL_DAMAGE")*100)/100.0;
        double health = Math.round(statMap.getStat("MAX_HEALTH")*100)/100.0;
        double skillCriticalStrikeChance = Math.round(statMap.getStat("SKILL_CRITICAL_STRIKE_CHANCE")*100)/100.0;
        double skillCriticalStrikePower = Math.round(statMap.getStat("SKILL_CRITICAL_STRIKE_POWER")*100)/100.0;
        double movementSpeed = Math.round(statMap.getStat("MOVEMENT_SPEED")*100)/100.0;
        double additionalExperience = Math.round(statMap.getStat("ADDITIONAL_EXPERIENCE")*100)/100.0;
        long maxHealth = Math.round(health);
        NumberFormat numberFormat = NumberFormat.getInstance();
        player.sendMessage("§f > "+player.getName()+" 님의 §a스텟 §f정보입니다.");
        player.sendMessage("§f  ");
        player.sendMessage("§f  ᎈ §7공격력: §f+"+numberFormat.format(skillDamage)+"%");
        player.sendMessage("§f  ᎏ §c체력: §f+"+numberFormat.format(maxHealth));
        player.sendMessage("§f  ᎏ §e크리티컬 확률: §f+"+numberFormat.format(skillCriticalStrikeChance)+"%");
        player.sendMessage("§f  ᎏ §6크리티컬 데미지: §f+"+numberFormat.format(skillCriticalStrikePower)+"%");
        player.sendMessage("§f  ᎒ §b이동 속도: §f+"+ numberFormat.format(movementSpeed)+"%");
        player.sendMessage("§f  Ӻ §a경험치 획득량: §f+"+numberFormat.format(additionalExperience)+"%");
        player.sendMessage("");
        return true;
    }
}
