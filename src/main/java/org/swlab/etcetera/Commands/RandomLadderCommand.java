package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomLadderCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        AbstractParty party = playerData.getParty();
        if(party == null){
            player.sendMessage("§c 파티가 없습니다!");
            return false;
        }
        List<PlayerData> onlineMembers = party.getOnlineMembers();
        Collections.shuffle(onlineMembers);

        int i = 0;

        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ofPattern("(hh시 mm분 ss초 시행)"));

        for (PlayerData onlineMember : onlineMembers) {
            Player onlineMemberPlayer = onlineMember.getPlayer();
            onlineMemberPlayer.sendMessage("");
            onlineMemberPlayer.sendMessage(ColorManager.format("    "+player.getName()+" #89FFF1님께서 진행한 사다리타기 결과입니다! §7§o" + format));
            onlineMemberPlayer.sendMessage("");
            for (PlayerData member : onlineMembers) {
                i++;
                String nickname = member.getPlayer().getName();
                onlineMemberPlayer.sendMessage(ColorManager.format("#89C1FF    "+i+"위: §f"+nickname));
            }
            i = 0;
            onlineMemberPlayer.sendMessage("");
        }
        return true;
    }
}
