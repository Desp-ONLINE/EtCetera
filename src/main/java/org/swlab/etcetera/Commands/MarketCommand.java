package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.List;

public class MarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(!EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby 시장");
        }
        if(strings.length == 0){
            CommandUtil.runCommandAsOP(player,"ah");
            return false;
        }
        switch (strings[0]){
            case "판매":
                List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
                if(lore == null){
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;
                }
                if(lore.contains("§6    거래: §c불가")){
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;
                }
                if(strings.length == 1){
                    player.sendMessage("§c 가격을 입력하세요. §7§o(/시장 판매 <가격>)");
                    return false;
                }
                CommandUtil.runCommandAsOP(player,"ah sell "+strings[1]);
        }
        return false;
    }
}
