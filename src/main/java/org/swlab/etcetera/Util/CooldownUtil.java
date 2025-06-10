package org.swlab.etcetera.Util;

import com.binggre.binggreapi.utils.ColorManager;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import io.lumine.mythic.lib.skill.Skill;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.SkillManager;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

import java.util.Collection;

public class CooldownUtil {

    public static void resetAllSkillCooldown(Player player) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        CooldownMap cooldownMap = mmoCoreAPI.getPlayerData(player).getCooldownMap();
        SkillManager skillManager = new SkillManager();
        for (RegisteredSkill registeredSkill : skillManager.getAll()) {
            String name = registeredSkill.getName();
            System.out.println("name = " + name);
            cooldownMap.resetCooldown(name);

        }
    }
}
