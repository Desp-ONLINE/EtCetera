package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class TrainingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(!EtCetera.getChannelType().equals("lobby")){
            player.sendMessage("§c 로비에서만 이용할 수 있는 명령어입니다.");
            return false;
        }
        if(strings.length == 0){
            CommandUtil.runCommandAsOP(player, "training join");
            player.sendMessage("§6[훈련] §f/훈련 종료 명령어를 통해 훈련을 중단하실 수 있습니다.");
        } else{
            if(strings[0].equals("종료")){
                CommandUtil.runCommandAsOP(player, "training quit");
            } else{
                player.sendMessage("§6[훈련] §f/훈련 종료 명령어를 통해 훈련을 중단하실 수 있습니다.");
            }
        }
        return false;
    }
}
