package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.List;

public class VersusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if(!EtCetera.getChannelType().equals("lobby")){
            player.sendMessage("§c 로비에서만 이용하실 수 있습니다.");
        }
        CommandUtil.runCommandAsOP(player, "아포칼립스매치 매칭");
        player.sendMessage("§3[영혼의 대결] §7§o(BETA) §f/대결 을 한번 더 입력하여 매칭에서 퇴장하실 수 있습니다.");
        return true;
    }
}
