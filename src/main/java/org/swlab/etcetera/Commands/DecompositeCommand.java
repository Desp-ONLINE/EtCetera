package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.commands.admin.arguments.StopArgument;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class DecompositeCommand implements CommandExecutor {

    private static ArrayList<String> commonItems = new ArrayList<>(Arrays.asList("무한의포션", "폭파광", "바람의검", "호루스의눈"));
    private static ArrayList<String> rareItems = new ArrayList<>(Arrays.asList("라이트소드", "배틀댄서", "아이언헬버드", "라이트스피어", "윈드시클", "허리케인보우", "사이안스피어"));
    private static ArrayList<String> epicItems = new ArrayList<>(Arrays.asList("익스플로즈", "마나보우", "트릭오어트릿", "헤카테_화염", "하드글래스스피어", "적수정검"));
    private static ArrayList<String> uniqueItems = new ArrayList<>(Arrays.asList("래피드보우", "레드나이프", "에너지소드", "스톰사이드", "헤븐리해머"));

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length != 1) {
            sendUsageMessage(player);
            return true;
        }
        switch (strings[0]) {
            case "분해확인":
                ArrayList<ItemStack> items = new ArrayList<>();
                Integer commonAmount = 0;
                Integer rareAmount = 0;
                Integer epicAmount = 0;
                Integer uniqueAmount = 0;

                for (@Nullable ItemStack storageContent : player.getInventory().getStorageContents()) {
                    if (MMOItems.getID(storageContent) == null) {
                        continue;
                    }
                    String id = MMOItems.getID(storageContent);
                    if (!(id.startsWith("특수무기_") && id.endsWith("0"))) {
                        continue;
                    }
                    id = id.replace("특수무기_", "").replace("0", "");

                    ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_세계의핵");
                    if (commonItems.contains(id)) {
                        storageContent.setAmount(0);
                        item.setAmount(5);
                        items.add(item);
                        commonAmount++;
                    } else if (rareItems.contains(id)) {
                        storageContent.setAmount(0);
                        item.setAmount(7);
                        items.add(item);
                        rareAmount++;
                    } else if (epicItems.contains(id)) {
                        storageContent.setAmount(0);
                        item.setAmount(10);
                        items.add(item);
                        epicAmount++;
                    } else if (uniqueItems.contains(id)) {
                        storageContent.setAmount(0);
                        item.setAmount(15);
                        items.add(item);
                        uniqueAmount++;
                    }


                }
                Mail mail = MMOMail.getInstance().getMailAPI().createMail("시스템", "일괄 분해 보상입니다.", 0, items);
                MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);

                player.sendMessage("");
                player.sendMessage(ColorManager.format("§f  일반 등급 무기를 총 §e"+commonAmount+"§f 개 분해했습니다."));
                player.sendMessage(ColorManager.format("#90FF78  레어 등급 무기를 총 §e"+rareAmount+"#90FF78 개 분해했습니다."));
                player.sendMessage(ColorManager.format("#F0A9FF  에픽 등급 무기를 총 §e"+epicAmount+"#F0A9FF 개 분해했습니다."));
                player.sendMessage(ColorManager.format("#A9FFFC 유니크 등급 무기를 총 §e"+uniqueAmount+"#A9FFFC 개 분해했습니다."));
                player.sendMessage("");

                return true;
            default:
                sendUsageMessage(player);
        }
        return false;
    }

    public void sendUsageMessage(Player player) {
        player.sendMessage("");
        player.sendMessage("§7§o  /일괄분해 분해확인 - 내 인벤토리의 특수무기 +0 강화 (레전더리 미만) 등급의 무기들을 일괄 분해합니다.");
        player.sendMessage(ColorManager.format("§f  일반 등급 -> 세계의 핵 5개"));
        player.sendMessage(ColorManager.format("§f  #90FF78레어 등급 -> 세계의 핵 7개"));
        player.sendMessage(ColorManager.format("§f  #F0A9FF에픽 등급 -> 세계의 핵 10개"));
        player.sendMessage(ColorManager.format("§f  #A9FFFC유니크 등급 -> 세계의 핵 15개"));
        player.sendMessage("");
    }

}
