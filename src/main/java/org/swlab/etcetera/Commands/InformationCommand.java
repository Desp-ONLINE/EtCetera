package org.swlab.etcetera.Commands;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class InformationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOPlayerData playerData = MMOPlayerData.get(player);
        StatMap statMap = playerData.getStatMap();
        double skillDamage = statMap.getStat("SKILL_DAMAGE");
        double health = statMap.getStat("MAX_HEALTH");
        double skillCriticalStrikeChance = statMap.getStat("SKILL_CRITICAL_STRIKE_CHANCE");
        double skillCriticalStrikePower = statMap.getStat("SKILL_CRITICAL_STRIKE_POWER");
        double movementSpeed = statMap.getStat("MOVEMENT_SPEED");
        long maxHealth = Math.round(health);

        player.sendMessage("§f > "+player.getName()+" 님의 §a스텟 §f정보입니다.");
        player.sendMessage("§f  ");
        player.sendMessage("§f  ᎈ §7공격력: §f+"+skillDamage+"%");
        player.sendMessage("§f  ᎏ §c체력: §f+"+maxHealth);
        player.sendMessage("§f  ᎏ §e크리티컬 확률: §f+"+skillCriticalStrikeChance+"%");
        player.sendMessage("§f  ᎏ §6크리티컬 데미지: §f+"+skillCriticalStrikePower+"%");
        player.sendMessage("§f  ᎒ §b이동 속도: §f+"+movementSpeed);
        player.sendMessage("");
        return true;
    }
}
