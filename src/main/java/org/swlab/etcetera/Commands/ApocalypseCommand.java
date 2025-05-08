package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class ApocalypseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        String joinMessage = ColorManager.format("#D293FF /아포칼립스 입장 §f- 아포칼립스 던전에 입장할 수 있는 UI를 오픈합니다. ");
        String checkMessage = ColorManager.format("#D293FF /아포칼립스 확인 §f- 오늘 나의 아포칼립스 던전 입장권 획득 여부를 확인합니다. ");
        String receiveMessage = ColorManager.format("#D293FF /아포칼립스 보상받기 §f- 현재 보상을 수령합니다. 수령 시 입장권이 소모됩니다.");
        String rewardInfoMessage = ColorManager.format("#D293FF /아포칼립스 보상정보 §f- 현재 수령 가능한 보상의 정보를 확인하실 수 있습니다.");
        if (strings.length == 0) {
            player.sendMessage(joinMessage);
            player.sendMessage(checkMessage);
            player.sendMessage(receiveMessage);
            player.sendMessage(rewardInfoMessage);
            return false;
        }
        switch (strings[0]) {
            case "입장":
                if (!EtCetera.getChannelType().equals("lobby")) {
                    player.sendMessage("§c 아포칼립스 던전은 로비 채널에서만 이용할 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "gui open 아포칼립스");
                return true;
            case "확인":
                CommandUtil.runCommandAsOP(player, "웨이브던전 확인");
                return true;
            case "보상받기":
                CommandUtil.runCommandAsOP(player, "웨이브던전 보상받기");
                return true;
            case "보상정보":
                CommandUtil.runCommandAsOP(player, "웨이브던전 보상정보");
                return true;
            default:

                player.sendMessage(joinMessage);
                player.sendMessage(checkMessage);
                player.sendMessage(receiveMessage);
                player.sendMessage(rewardInfoMessage);
                return false;
        }
    }
}
