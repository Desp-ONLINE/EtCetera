package org.swlab.etcetera.Util;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

public class NameTagUtil {

    public static void setPlayerNameTag(Player player) {
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if(!player.isOnline()){
                return;
            }
            String job = PlaceholderAPI.setPlaceholders(player, "%Title_class%");
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerData playerData = mmoCoreAPI.getPlayerData(player);
            System.out.println("job = " + job);
            System.out.println("player.getName() = " + player.getName());

            int level = playerData.getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + " #BDFFB9[Lv." + playerData.getLevel() + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + " #FFFC9B[Lv." + playerData.getLevel() + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + " #E257FF[Lv." + playerData.getLevel() + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + " #FF6557[Lv." + playerData.getLevel() + "] ");
            } else {
                format = ColorManager.format("§f" + " #FF3D3D[Lv." + playerData.getLevel() + "] ");
            }

            CommandUtil.runCommandAsOP(player, "nametagedit player " + player.getName() + " prefix " + job +" "+ format + "&f");
        }, 20L);


    }
}
