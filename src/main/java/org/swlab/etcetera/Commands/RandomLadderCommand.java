package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.dople.iDEParty.api.IDEPartyAPI;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/** 파티 사다리타기. IDEParty 파티 중 이 서버에 접속한 파티원만 대상으로 한다 */
public class RandomLadderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if (!IDEPartyAPI.isInParty(player)) {
            player.sendMessage("§c 파티가 없습니다!");
            return false;
        }
        List<Player> onlineMembers = IDEPartyAPI.getLocalMembers(player);
        Collections.shuffle(onlineMembers);

        int i = 0;

        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ofPattern("(hh시 mm분 ss초 시행)"));

        for (Player onlineMemberPlayer : onlineMembers) {
            onlineMemberPlayer.sendMessage("");
            onlineMemberPlayer.sendMessage(ColorManager.format("    "+player.getName()+" #89FFF1님께서 진행한 사다리타기 결과입니다! §7§o" + format));
            onlineMemberPlayer.sendMessage("");
            for (Player member : onlineMembers) {
                i++;
                String nickname = member.getName();
                onlineMemberPlayer.sendMessage(ColorManager.format("#89C1FF    "+i+"위: §f"+nickname));
            }
            i = 0;
            onlineMemberPlayer.sendMessage("");
        }
        return true;
    }
}
