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
        if (strings.length == 0) {
            player.sendMessage("§8[§6펫§8] >> §7/펫 소환 - §e귀여운 내 펫들을 소환할 수 있습니다!");
            player.sendMessage("§8[§6펫§8] >> §7/펫 휴식 - §e소환한 펫들을 휴식시킵니다.");
            player.sendMessage("§8[§6펫§8] >> §7/펫 도감 [C/B/A/S] - §e내 펫의 도감 정보를 확인합니다.");
            player.sendMessage("");
            player.sendMessage("§8[§6펫§8] §f루비 상점, 추천 코인 상점, 이벤트, 스피릿 랜턴 등으로 펫을 획득하실 수 있습니다.");
            return false;
        }
        switch (strings[0]) {
            case "소환":
                CommandUtil.runCommandAsOP(player, "mcpets");
                break;
            case "휴식":
                CommandUtil.runCommandAsOP(player, "mcpets revoke");
                break;
            case "도감":
                if (strings.length == 1) {
                    player.sendMessage("§8[§6펫§8] >> §7/펫 도감 [C/B/A/S] - §e내 펫의 도감 정보를 확인합니다.");
                    return false;
                }
                String rank = strings[1].toUpperCase();
                if (!rank.equals("C") && !rank.equals("B") && !rank.equals("A") && !rank.equals("S")) {
                    player.sendMessage("§8[§6펫§8] >> §7/펫 도감 [C/B/A/S] - §e내 펫의 도감 정보를 확인합니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "펫도감 " + rank);
                break;
            default:
                player.sendMessage("§8[§6펫§8] >> §7/펫 소환 - §e귀여운 내 펫들을 소환할 수 있습니다!");
                player.sendMessage("§8[§6펫§8] >> §7/펫 휴식 - §e소환한 펫들을 휴식시킵니다.");
                player.sendMessage("§8[§6펫§8] >> §7/펫 도감 [C/B/A/S] - §e내 펫의 도감 정보를 확인합니다.");
                player.sendMessage("");
                player.sendMessage("§8[§6펫§8] §f루비 상점, 추천 코인 상점, 이벤트, 스피릿 랜턴 등으로 펫을 획득하실 수 있습니다.");
        }

        return true;
    }

}
