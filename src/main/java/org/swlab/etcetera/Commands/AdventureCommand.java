package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class AdventureCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(!player.isOp()){
            return true;
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        int level = playerData.getLevel();
        if(level < 20){
            CommandUtil.runCommandAsOP(player, "로그라이크_관리 티켓지급 "+player.getName()+" 1");
            player.sendMessage("§a 성공적으로 1레벨 환영 던전 티켓을 획득했습니다.");
        } else if (level <45){
            CommandUtil.runCommandAsOP(player, "로그라이크_관리 티켓지급 "+player.getName()+" 2");
            player.sendMessage("§a 성공적으로 2레벨 환영 던전 티켓을 획득했습니다.");
        }else if (level <70){
            CommandUtil.runCommandAsOP(player, "로그라이크_관리 티켓지급 "+player.getName()+" 3");
            player.sendMessage("§a 성공적으로 3레벨 환영 던전 티켓을 획득했습니다.");
        }else if (level <100){
            CommandUtil.runCommandAsOP(player, "로그라이크_관리 티켓지급 "+player.getName()+" 4");
            player.sendMessage("§a 성공적으로 4레벨 환영 던전 티켓을 획득했습니다.");
        }else{
            CommandUtil.runCommandAsOP(player, "로그라이크_관리 티켓지급 "+player.getName()+" 5");
            player.sendMessage("§a 성공적으로 5레벨 환영 던전 티켓을 획득했습니다.");
        }
        return true;
    }
}
