package org.swlab.etcetera.Commands;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierType;
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
        StatModifier statModifier = new StatModifier("asd", "skill-damage", 10.3, ModifierType.FLAT);
        statModifier.register(MMOPlayerData.get(player.getUniqueId()));
        return true;

    }
}
