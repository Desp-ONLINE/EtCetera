package org.swlab.etcetera.Commands;

import io.lumine.mythic.lib.math3.analysis.function.Min;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dople.transactionLog.Database.TransactionLogRepository;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.List;

public class MarketCommand implements CommandExecutor {

    private long SELL_PERCENTAGE = 20;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (!EtCetera.getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(player, "채널 워프 lobby 시장");
        }
        if (strings.length == 0) {
            CommandUtil.runCommandAsOP(player, "ah");
            player.sendMessage("§7 > 대금 수령은 §6/시장 수령 §7명령어를 이용해주세요.");
            player.sendMessage("§7 > 아이템 판매는 §e/시장 판매 <금액> §7명령어를 통해 손에 든 아이템을 판매할 수 있습니다. (3000만과 같이 \"만\"글자를 붙여서 사용할 수 있습니다.)");
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
                if (strings.length == 3) {
                    try {
                        String priceString = strings[1].replace("만", "0000");
                        long price = Long.parseLong(priceString);
                        double v = Double.parseDouble(strings[2]);
                        Integer i = Integer.valueOf((int) v);
                        if (!checkPriceInRange(price, player)) {
                            return false;
                        }
                        CommandUtil.runCommandAsOP(player, "ah sell " + price + " " + i);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c/시장 판매 <금액> <개수>: 개수 입력이 잘못되었습니다.");
                    }
                    return false;
                }
                String priceString = strings[1].replace("만", "0000");
                long price = Long.parseLong(priceString);
                if (!checkPriceInRange(price, player)) {
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "ah sell " + price);
                return false;
            case "수령":
                CommandUtil.runCommandAsOP(player, "ah claim");
                return false;
        }
        return false;
    }

    public boolean checkPriceInRange(long price, Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        String id = MMOItems.getID(itemInMainHand);
        String type = MMOItems.getType(itemInMainHand).getId();
        long averagePrice = TransactionLogRepository.getInstance().getAveragePrice(id, type, 5);
        if(averagePrice == 0){
            return true;
        }
        averagePrice *= itemInMainHand.getAmount();
        long maximumPrice = averagePrice + averagePrice * SELL_PERCENTAGE / 100;
        long minimumPrice = averagePrice - averagePrice * SELL_PERCENTAGE / 100;
        NumberFormat formatter = NumberFormat.getInstance();
        if (averagePrice != 0) {
            if ((price > maximumPrice) || (price < minimumPrice)) {
                player.sendMessage("§6[ 시장 경제 시스템 ] §e" + formatter.format(minimumPrice) + " §c~ §e" + formatter.format(maximumPrice) + " §c사이의 금액으로만 판매 할 수 있습니다. §7§o(평균 거래가의 " + SELL_PERCENTAGE + "% 로 판매가 제한되어 있습니다.)");
                return false;
            }
        }
        return true;
    }
}
