package org.swlab.etcetera.Inventory;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VoteRewards implements InventoryHolder {
    private Inventory inventory;
    @Override
    public @NotNull Inventory getInventory() {
        inventory = Bukkit.createInventory(this, 36, "Vote Rewards");
        ItemStack voteCoin = MMOItems.plugin.getItem("MISCELLANEOUS", "기타-추천코인");
        inventory.addItem(voteCoin);
        return null;
    }
}
