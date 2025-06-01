package org.swlab.etcetera.Placeholder;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Listener.LeapListener;

import java.util.Iterator;
import java.util.Objects;

public class LevelPlaceholder extends PlaceholderExpansion {

    private final EtCetera etCetera;

    public LevelPlaceholder(EtCetera etCetera) {
        this.etCetera = etCetera;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "LevelPlaceholder";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dople";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        final String string = identifier.split("_")[0];
        if(Objects.equals(string, "level")) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(etCetera);
            if(EtCetera.getChannelType().equals("afk")){
                return "잠수중....";
            }
            int level = mmoCoreAPI.getPlayerData(player).getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + " #BDFFB9[Lv." + level + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + " #FFFC9B[Lv." + level + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + " #E257FF[Lv." + level + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + " #FF6557[Lv." + level + "] ");
            } else {
                format = ColorManager.format("§f" + " #5C2DB2[Lv." + level + "] ");
            }
            return format;
        }
        return "-1";
    }


}
