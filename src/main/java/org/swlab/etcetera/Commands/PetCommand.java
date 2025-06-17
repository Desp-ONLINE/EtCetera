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
import org.swlab.etcetera.Util.CommandUtil;

public class PetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(strings.length == 0){
            player.sendMessage("§8[§6펫§8] >> §7/펫 소환 - §e귀여운 내 펫들을 소환할 수 있습니다!");
            player.sendMessage("§8[§6펫§8] >> §7/펫 휴식 - §e소환한 펫들을 휴식시킵니다.");
            return false;
        }
        switch (strings[0]){
            case "소환":
                CommandUtil.runCommandAsOP(player, "mcpets");
                break;
            case "휴식":
                CommandUtil.runCommandAsOP(player, "mcpets revoke");
                break;
            default:
                player.sendMessage("§8[§6펫§8] >> §7/펫 소환 - §e귀여운 내 펫들을 소환할 수 있습니다!");
                player.sendMessage("§8[§6펫§8] >> §7/펫 휴식 - §e소환한 펫들을 휴식시킵니다.");
        }

        return true;
    }

}
