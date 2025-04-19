package org.swlab.etcetera.Commands;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.TitleAnimationUtil;

public class CheckNBTTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        TitleAnimationUtil.getInstance().sendAnimation((Player) sender, strings[0], strings[1], 0, 10, 0, "#123456", "#FFFFFF");
        return false;
    }
}
