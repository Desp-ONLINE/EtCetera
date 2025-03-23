package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class FriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            CommandUtil.runCommandAsOP(player, "f");
        }
        if (strings.length == 1) {
            switch (strings[0]) {
                case "신청":
                    player.sendMessage("§7 > §f/친구 신청 <닉네임> : 해당 유저에게 친구를 신청할 수 있습니다.");
                    return true;
                case "수락":
                    player.sendMessage("§7 > §f/친구 수락 <닉네임> : 해당 유저의 친구 신청을 수락합니다.");
                    return true;
                case "거절":
                    player.sendMessage("§7 > §f/친구 거절 <닉네임> : 해당 유저의 친구 신청을 거절합니다.");
                    return true;
                case "삭제":
                    player.sendMessage("§7 > §f/친구 삭제 <닉네임> : 해당 유저와의 친구 관계를 끊습니다.");
                    return true;
                default:
                    player.sendMessage("§7 > §f/친구 신청 <닉네임> : 해당 유저에게 친구를 신청할 수 있습니다.");
                    player.sendMessage("§7 > §f/친구 수락 <닉네임> : 해당 유저의 친구 신청을 수락합니다.");
                    player.sendMessage("§7 > §f/친구 거절 <닉네임> : 해당 유저의 친구 신청을 거절합니다.");
                    player.sendMessage("§7 > §f/친구 삭제 <닉네임> : 해당 유저와의 친구 관계를 끊습니다.");
                    return true;
            }
        }
        if(strings.length==2){
            switch (strings[0]){
                case "신청":
                    CommandUtil.runCommandAsOP(player, "forwardcmd friend add "+strings[1]);
                    return true;
                case "수락":
                    CommandUtil.runCommandAsOP(player, "forwardcmd friend accept "+strings[1]);
                    return true;
                case "거절":
                    CommandUtil.runCommandAsOP(player, "forwardcmd friend deny "+strings[1]);
                    return true;
                case "삭제":
                    CommandUtil.runCommandAsOP(player, "forwardcmd friend remove "+strings[1]);
                    return true;
                default:
                    player.sendMessage("§7 > §f/친구 신청 <닉네임> : 해당 유저에게 친구를 신청할 수 있습니다.");
                    player.sendMessage("§7 > §f/친구 수락 <닉네임> : 해당 유저의 친구 신청을 수락합니다.");
                    player.sendMessage("§7 > §f/친구 거절 <닉네임> : 해당 유저의 친구 신청을 거절합니다.");
                    player.sendMessage("§7 > §f/친구 삭제 <닉네임> : 해당 유저와의 친구 관계를 끊습니다.");
                    return true;
            }
        }
        return false;
    }
}
