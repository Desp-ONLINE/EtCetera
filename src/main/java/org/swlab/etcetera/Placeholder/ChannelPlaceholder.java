package org.swlab.etcetera.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

public class ChannelPlaceholder extends PlaceholderExpansion {

    private final EtCetera etCetera;

    public ChannelPlaceholder(EtCetera etCetera) {
        this.etCetera = etCetera;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Channel";
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
        if (identifier.equals("Name")) {
            String channelType = EtCetera.getChannelType();
            switch (channelType) {
                case "lobby":
                    return "로비";
                case "dungeon":
                    return "던전";
                case "afk":
                    return "잠수";
                case "pvp":
                    return "PvP";
                default:
                    return channelType;
            }
        }
        if (identifier.equals("Number")) {
            return String.valueOf(EtCetera.getChannelNumber());
        }
        return null;
    }
}
