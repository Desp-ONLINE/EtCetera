package org.swlab.etcetera.Commands;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class PetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if (strings.length == 0) {
            sendUsage(player);
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
            case "이름":
                if (strings.length < 2) {
                    player.sendMessage("§8[§6펫§8] >> §7/펫 이름 <이름> - §e현재 소환한 펫의 이름을 변경합니다.");
                    return false;
                }
                Pet activePet = MCPetsAPI.getActivePet(player.getUniqueId());
                if (activePet == null) {
                    player.sendMessage("§8[§6펫§8] >> §c펫을 먼저 소환해주세요.");
                    return false;
                }
                Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
                    StringBuilder nameBuilder = new StringBuilder(strings[1]);
                    for (int i = 2; i < strings.length; i++) {
                        nameBuilder.append(" ").append(strings[i]);
                    }
                    String newName = nameBuilder.toString();
                    activePet.setDisplayName(newName, true);
                    player.sendMessage("§8[§6펫§8] >> §e펫의 이름을 §f" + newName + " §e(으)로 변경했습니다.");
                });
                break;
            default:
                sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("§8[§6펫§8] >> §7/펫 소환 - §e귀여운 내 펫들을 소환할 수 있습니다!");
        player.sendMessage("§8[§6펫§8] >> §7/펫 휴식 - §e소환한 펫들을 휴식시킵니다.");
        player.sendMessage("§8[§6펫§8] >> §7/펫 이름 <이름> - §e현재 소환한 펫의 이름을 변경합니다.");
        player.sendMessage("§8[§6펫§8] >> §7/펫 도감 [C/B/A/S] - §e내 펫의 도감 정보를 확인합니다.");
        player.sendMessage("");
        player.sendMessage("§8[§6펫§8] §f루비 상점, 추천 코인 상점, 이벤트, 스피릿 랜턴 등으로 펫을 획득하실 수 있습니다.");
    }

}
