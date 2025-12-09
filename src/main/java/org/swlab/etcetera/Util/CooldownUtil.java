package org.swlab.etcetera.Util;

import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.manager.SkillManager;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

public class CooldownUtil {

    public static void resetAllSkillCooldown(Player player) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        CooldownMap cooldownMap = mmoCoreAPI.getPlayerData(player).getCooldownMap();
        SkillManager skillManager = new SkillManager();
        for (RegisteredSkill registeredSkill : skillManager.getAll()) {
            String name = registeredSkill.getName();
//            System.out.println("name = " + name);
            cooldownMap.resetCooldown(name);

        }
    }
}
