package org.swlab.etcetera.Placeholder;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

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
        String[] split = identifier.split("_");
        final String arg1 = split[0];
        if (Objects.equals(arg1, "level")) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(etCetera);
            if (EtCetera.getChannelType().equals("afk")) {
                return "§7§o 잠수중...";
            }
            int level = mmoCoreAPI.getPlayerData(player).getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + " #BDFFB9[Lv." + level + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + " #FFFC9B[Lv." + level + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + " #F1ADFF[Lv." + level + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + " #AC7EFF[Lv." + level + "] ");
            } else if (level < 130) {
                format = ColorManager.format("§f" + " #7ECBFF[Lv." + level + "] ");
            } else if (level < 160){
                format = ColorManager.format("§f" + " #7A89FF[Lv." + level + "] ");
            } else {
                format = ColorManager.format("§f" + " #5D64CB§n[Lv." + level + "]§r ");
            }
            return format;
        } else if (Objects.equals(arg1, "classLevel")) {
            final String arg2 = split[1];

            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(etCetera);
            if (EtCetera.getChannelType().equals("afk")) {
                return "§7§o 잠수중...";
            }
            SavedClassInformation classInfo = mmoCoreAPI.getPlayerData(player).getClassInfo(arg2);
            if (classInfo == null) {
                return " §7§o(레벨 정보가 없습니다!)";
            }
            int level = classInfo.getLevel();
            String format = "";
            if (level < 20) {
                format = ColorManager.format("§f" + " #BDFFB9[Lv." + level + "] ");
            } else if (level < 45) {
                format = ColorManager.format("§f" + " #FFFC9B[Lv." + level + "] ");
            } else if (level < 70) {
                format = ColorManager.format("§f" + " #F1ADFF[Lv." + level + "] ");
            } else if (level < 100) {
                format = ColorManager.format("§f" + " #AC7EFF[Lv." + level + "] ");
            } else if (level < 130) {
                format = ColorManager.format("§f" + " #7ECBFF[Lv." + level + "] ");
            } else if (level < 160){
                format = ColorManager.format("§f" + " #7A89FF[Lv." + level + "] ");
            } else {
                format = ColorManager.format("§f" + " #5D64CB§n[Lv." + level + "]§r ");
            }
            return format;
        }
        return "-1";
    }


}
