package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.serializers.ItemStackSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class RaidEquipmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(!EtCetera.getChannelType().equals("lobby")){
            player.sendMessage("§c 로비에서만 사용하실 수 있습니다.");
            return false;
        }
        CommandUtil.runCommandAsOP(player, "워프 이동 보스장비");
        return true;
    }
}