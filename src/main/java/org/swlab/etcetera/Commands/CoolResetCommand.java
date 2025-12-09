package org.swlab.etcetera.Commands;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoolResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if(!player.hasPermission("staff")){
            return false;
        }
        MMOPlayerData mmoPlayerData = MMOPlayerData.get(player.getUniqueId());
        mmoPlayerData.getCooldownMap().clearAllCooldowns();
        return true;
    }
}
