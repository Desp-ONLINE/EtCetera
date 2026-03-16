package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

public class ManaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        if (strings.length < 1) {
            return false;
        }
        if (strings[0].contains("%")) {
            String replace = strings[0].replace("%", "");
            double v = Double.parseDouble(replace);
            double maxMana = playerData.getStats().getStat("MAX_MANA");
            playerData.giveMana(maxMana * (v / 100));
            return true;
        } else {
            double v = Double.parseDouble(strings[0]);
            playerData.giveMana(v);
            return false;
        }

    }
}
