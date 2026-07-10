package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dople.transactionLog.Service.PriceRangeService;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

public class MarketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        int level = mmoCoreAPI.getPlayerData(player).getLevel();
        if (level <= 1) {
            player.sendMessage("§c 1레벨인 경우 시장을 이용하실 수 없습니다.");
            return false;
        }
        if (strings.length == 0) {
            CommandUtil.runCommandAsOP(player, "시장2");
            player.sendMessage("§7 > 아이템 판매는 §e/시장 판매 <금액> §7명령어를 통해 손에 든 아이템을 판매할 수 있습니다. (3000만과 같이 \"만\"글자를 붙여서 사용할 수 있습니다.)");
            player.sendMessage("§c ※ 고의적으로 시장에 시세보다 훨씬 낮은 가격으로 거래하는 사항은 제재 대상입니다.");
            return false;
        }
        switch (strings[0]) {
            case "판매":
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                List<String> lore = itemInMainHand.getItemMeta().getLore();
                String id = MMOItems.getID(itemInMainHand);
                if (lore == null) {
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;
                }
                if (id.startsWith("특수무기") || id.startsWith("합성무기")) {
                    if (id.endsWith("1") || id.endsWith("2") || id.endsWith("3") || id.endsWith("4")) {
                        player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                        return false;
                    }
                }
                if (id.contains("응집")) {
                    player.sendMessage("§c 이 아이템은 판매할 수 없습니다.");
                    return false;

                }
                if (strings.length == 1) {
                    player.sendMessage("§c 가격을 입력하세요. §7§o(/시장 판매 <가격> : 손에 든 아이템을 입력된 가격에 판매합니다. ");
                    return false;
                }
                if (strings.length == 3) {
                    try {
                        String priceString = strings[1].replace("만", "0000");
                        double price = Double.parseDouble(priceString);
                        sellIfPriceInRange(price, player);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c/시장 판매 <금액> : 입력이 잘못되었습니다.");
                    }
                    return false;
                }
                String priceString = strings[1].replace("만", "0000");
                double price = Double.parseDouble(priceString);
                sellIfPriceInRange(price, player);
                return false;
            case "회수":
                CommandUtil.runCommandAsOP(player, "ah");
                return true;
            case "수령":
                CommandUtil.runCommandAsOP(player, "ah claim");
                return true;
        }
        return false;
    }

    // 가격 범위 조회(Mongo)를 비동기로 수행해 메인 스레드 멈춤을 막고,
    // 검사 통과 시에만 메인 스레드로 복귀해 판매 명령을 실행한다.
    private void sellIfPriceInRange(double price, Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        String id = MMOItems.getID(itemInMainHand);
        String type = MMOItems.getType(itemInMainHand).getId();
        int amount = itemInMainHand.getAmount();

        PriceRangeService priceRangeService = PriceRangeService.getInstance();
        priceRangeService.getPriceRangeAsync(id, type, amount, player).whenComplete((range, ex) -> {
            if (ex != null) {
                EtCetera.getInstance().getLogger().warning("시장 가격 범위 계산 실패 (" + id + "): " + ex.getMessage());
                return;
            }
            if (!priceRangeService.isPriceInRange(price, range)) {
                NumberFormat formatter = NumberFormat.getInstance();
                player.sendMessage("§6[ 시장 경제 시스템 ] §e" + formatter.format(range.getMinPrice()) + " §c~ §e" + formatter.format(range.getMaxPrice()) + " §c사이의 금액으로만 판매 할 수 있습니다. §7§o(평균 거래가의 " + range.getGapPercent() + "% 로 판매가 제한되어 있습니다.)");
                return;
            }
            // Bukkit 명령 실행은 메인 스레드에서만 가능하다.
            Bukkit.getScheduler().runTask(EtCetera.getInstance(), () -> {
                // 비동기 검사 중 손의 아이템을 바꿔 가격 제한을 우회하지 못하도록,
                // 검사 시점에 캡처한 id/type/개수와 현재 손의 아이템을 비교한다.
                ItemStack current = player.getInventory().getItemInMainHand();
                String currentType = MMOItems.getType(current) == null ? null : MMOItems.getType(current).getId();
                if (!Objects.equals(id, MMOItems.getID(current))
                        || !type.equals(currentType)
                        || current.getAmount() != amount) {
                    player.sendMessage("§c 판매 검사 중 손에 든 아이템이 바뀌어 판매가 취소되었습니다. 다시 시도해주세요.");
                    return;
                }
                Bukkit.dispatchCommand(player, "판매2 " + price);
            });
        });
    }
}
