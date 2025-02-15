package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class MergeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        if(mmoCoreAPI.getPlayerData(player).getLevel() <= 45){
            player.sendMessage("§c  45레벨 이상만 사용 가능합니다.");
            return true;
        }
        if(EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "워프 이동 합성");
            return true;
        }
        CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 합성");
        return false;
    }
}
