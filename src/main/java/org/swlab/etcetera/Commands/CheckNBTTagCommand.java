package org.swlab.etcetera.Commands;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CheckNBTTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        NBT.get(itemInMainHand, nbt -> {
            Boolean nbtTag = nbt.getBoolean(strings[0]);
            player.sendMessage("결과: " + nbtTag);
        });
        return false;
    }
}
