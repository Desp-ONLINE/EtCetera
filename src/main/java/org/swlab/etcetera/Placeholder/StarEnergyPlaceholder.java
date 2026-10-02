package org.swlab.etcetera.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Repositories.StarEnergyRepository;

// %starenergy_amount%, %starenergy_max%
public class StarEnergyPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "starenergy";
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
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equals("max")) {
            return String.valueOf(StarEnergyRepository.MAX_AMOUNT);
        }
        if (player == null) {
            return "";
        }
        if (identifier.equals("amount")) {
            Integer amount = StarEnergyRepository.getInstance().getCachedAmount(player);
            return amount == null ? "0" : String.valueOf(amount);
        }
        return null;
    }
}
