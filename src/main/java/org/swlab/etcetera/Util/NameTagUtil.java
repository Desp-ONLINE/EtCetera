package org.swlab.etcetera.Util;

import com.binggre.binggreapi.utils.ColorManager;
import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.profiles.event.ProfileSelectedEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

import java.util.List;

public class NameTagUtil {

    public static void setPlayerNameTag(Player player) {
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if(!player.isOnline()){
                return;
            }
            String job = PlaceholderAPI.setPlaceholders(player, "%Title_class%");
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerData playerData = mmoCoreAPI.getPlayerData(player);


            int level = playerData.getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + " #BDFFB9[Lv." + playerData.getLevel() + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + " #FFFC9B[Lv." + playerData.getLevel() + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + " #E257FF[Lv." + playerData.getLevel() + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + " #5C2DB2[Lv." + playerData.getLevel() + "] ");
            }else if (level < 130) {
                format = ColorManager.format("§f" + " #223783[Lv." + playerData.getLevel() + "] ");
            } else {
                format = ColorManager.format("§f" + " #7A89FF[Lv." + playerData.getLevel() + "] ");
            }


        }, 20L);


    }
}
