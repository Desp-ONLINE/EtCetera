package org.swlab.etcetera.Commands;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class AccCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        CommandUtil.runCommandAsOP(player, "mmoinv");
        return false;
    }
}
