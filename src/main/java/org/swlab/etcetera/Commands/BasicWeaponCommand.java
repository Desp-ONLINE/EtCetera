package org.swlab.etcetera.Commands;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class BasicWeaponCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
        ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + playerClass.getName() + "0");
        ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
        player.getInventory().addItem(basicArmor);
        player.getInventory().addItem(basicWeapon);
        mmoCoreAPI.getPlayerData(player).setClassPoints(999);
        return true;
    }
}
