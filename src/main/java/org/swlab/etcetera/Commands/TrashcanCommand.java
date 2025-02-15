package org.swlab.etcetera.Commands;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class TrashcanCommand implements CommandExecutor, InventoryHolder {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        player.openInventory(getInventory());
        return true;
    }

    private Inventory inventory;
    @Override
    public @NotNull Inventory getInventory() {
        inventory = Bukkit.createInventory(this, 36, "쓰레기통 ( 복구 불가 )");
        return inventory;
    }
}
