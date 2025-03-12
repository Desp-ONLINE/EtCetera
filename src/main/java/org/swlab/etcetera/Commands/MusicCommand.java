package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class MusicCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(EtCetera.getChannelType().equals("lobby")){
            if(strings.length == 0){
                player.sendMessage("§a /음악 [켜기/끄기]");
            } else {
                if(strings[0].equals("켜기")){
                    CommandUtil.runCommandAsOP(player, "music on");
                    return false;
                }
                else if(strings[0].equals("끄기")){
                    CommandUtil.runCommandAsOP(player, "music off");
                    return false;
                }
                else {
                    player.sendMessage("§a /음악 [켜기/끄기]");
                    return false;
                }
            }
            return true;
        }
        else{
            player.sendMessage("§c 현재 로비에서만 쓸 수 있습니다.");
            return true;
        }
    }
}
