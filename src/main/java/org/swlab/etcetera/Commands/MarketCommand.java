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

public class MarketCommand implements CommandExecutor{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (!EtCetera.getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby 시장");
        }
        if (strings.length == 0) {
            CommandUtil.runCommandAsOP(player, "ah");
            player.sendMessage("§7 > 대금 수령은 §6/시장 수령 §7명령어를 이용해주세요.");
            player.sendMessage("§c ※ 고의적으로 시장에 시세보다 훨씬 낮은 가격으로 거래하는 사항은 제재 대상입니다.");
            return false;
        }
        switch (strings[0]) {
            case "판매":
                List<String> lore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
                if (lore == null) {
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;
                }
                if (lore.contains("§6    거래: §c불가")) {
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;
                }
                if (strings.length == 1) {
                    player.sendMessage("§c 가격을 입력하세요. §7§o(/시장 판매 <가격> <개수> : 개수는 입력하지 않으면 손에 든 아이템의 전부를 판매합니다.) ");
                    return false;
                }
                if(strings.length == 3){
                    try{
                        double v = Double.parseDouble(strings[2]);
                        Integer i = Integer.valueOf((int) v);
                        CommandUtil.runCommandAsOP(player, "ah sell " + strings[1] +" "+i);
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                        System.out.println("시장판매됨");
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c/시장 판매 <금액> <개수>: 개수 입력이 잘못되었습니다.");
                    }
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "ah sell " + strings[1]);
                return false;
            case "수령":
                CommandUtil.runCommandAsOP(player, "ah claim");
                return false;
        }
        return false;
    }
}
