package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dople.transactionLog.Database.TransactionLogRepository;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class MarketCommand implements CommandExecutor {

    private long BASIC_GAP = 20;
    // 미거래 일수에 따른 판매 허용 폭(±%) 구간. 두 배열의 같은 인덱스가 (일수, 폭) 쌍이며,
    // 사이 구간은 선형 보간한다. 0일=±20%, 10일=±30%, 14일=±50%, 17일=±70%, 21일 이상=±100%.
    private static final int[] GAP_DAYS = {0, 10, 14, 17, 21};
    private static final long[] GAP_PERCENT = {20, 30, 50, 70, 100};

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
                        if (!checkPriceInRange(price, player)) {
                            return false;
                        }
                        Bukkit.dispatchCommand(player, "판매2 " + price);
                    } catch (NumberFormatException | ParseException e) {
                        player.sendMessage("§c/시장 판매 <금액> : 입력이 잘못되었습니다.");
                    }
                    return false;
                }
                String priceString = strings[1].replace("만", "0000");
                double price = Double.parseDouble(priceString);
                try {
                    if (!checkPriceInRange(price, player)) {
                        return false;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Bukkit.dispatchCommand(player, "판매2 " + price);
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

    public boolean checkPriceInRange(double price, Player player) throws ParseException {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        String id = MMOItems.getID(itemInMainHand);
        String type = MMOItems.getType(itemInMainHand).getId();
        long averagePrice = TransactionLogRepository.getInstance().getAveragePrice(id, type, 20);
        if (averagePrice == 0) {
            return true;
        }

        // 거래가 10일 넘게 끊긴 경우에만 미거래 일수 기반으로 폭을 넓히고(calcGap),
        // 그 외에는 평소처럼 거래량 기반 폭(getGap)을 사용한다.
        int untransactedDays = TransactionLogRepository.getInstance().getUntransactedDays(id, type, 1, player);
        long gap = untransactedDays > 10 ? calcGap(untransactedDays) : getGap(id);

        averagePrice *= itemInMainHand.getAmount();
        long maximumPrice = averagePrice + averagePrice * gap / 100;
        long minimumPrice = averagePrice - averagePrice * gap / 100;

        NumberFormat formatter = NumberFormat.getInstance();
        if ((price > maximumPrice) || (price < minimumPrice)) {
            player.sendMessage("§6[ 시장 경제 시스템 ] §e" + formatter.format(minimumPrice) + " §c~ §e" + formatter.format(maximumPrice) + " §c사이의 금액으로만 판매 할 수 있습니다. §7§o(평균 거래가의 " + gap + "% 로 판매가 제한되어 있습니다.)");
            return false;
        }
        return true;
    }

    // 미거래 일수에 따른 판매 허용 폭(±%)을 GAP_DAYS/GAP_PERCENT 구간으로 선형 보간한다.
    private long calcGap(int untransactedDays) {
        if (untransactedDays <= GAP_DAYS[0]) {
            return GAP_PERCENT[0];
        }
        for (int i = 1; i < GAP_DAYS.length; i++) {
            if (untransactedDays < GAP_DAYS[i]) {
                int d0 = GAP_DAYS[i - 1];
                long g0 = GAP_PERCENT[i - 1];
                return g0 + (GAP_PERCENT[i] - g0) * (untransactedDays - d0) / (GAP_DAYS[i] - d0);
            }
        }
        return GAP_PERCENT[GAP_PERCENT.length - 1];
    }

    public long getGap(String mmoitemID) {

        int volume = TransactionLogRepository
                .getInstance()
                .getItemLogAmountWeekly(mmoitemID);

        // 최대 거래량 제한 (600 초과 방지)
        int maxVolume = 600;
        volume = Math.min(volume, maxVolume);

        double minGap = 10.0;
        double maxGap = 20.0;

        // 로그 정규화
        double t = Math.log(1.0 + volume) / Math.log(1.0 + maxVolume);

        // 20 → 10 으로 감소
        double gap = maxGap - (maxGap - minGap) * t;

        // 안전 클램프
        gap = Math.max(minGap, Math.min(maxGap, gap));

        return Math.round(gap);
    }
}
