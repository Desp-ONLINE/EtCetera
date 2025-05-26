package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
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

public class VersusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if (!EtCetera.getChannelType().equals("lobby")) {
            player.sendMessage("§c 로비에서만 이용하실 수 있습니다.");
            return false;
        }
        if (args.length == 0) {
            player.sendMessage(ColorManager.format("#5798FF /대결 매칭 &f- 영혼의 대결 매칭 대기열에 입/퇴장합니다. &7&o(입장 후 입력 시 퇴장됩니다.)"));
            player.sendMessage(ColorManager.format("#5798FF /대결 정보 &f- 내 영혼의 대결 정보를 확인합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대결 랭킹 &f- 영혼의 대결 랭킹을 확인합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대결 티어 &f- 영혼의 대결 티어 승점 기준을 확인합니다."));
            player.sendMessage("");
            player.sendMessage(ColorManager.format("#5798FF [!] 영혼의 대결 보상은 위키에서 확인하실 수 있습니다!"));
            player.sendMessage("");
            return true;
        }
        switch (args[0]) {
            case "매칭":
                CommandUtil.runCommandAsOP(player, "아포칼립스매치 매칭");
                break;
            case "정보":
                String top = PlaceholderAPI.setPlaceholders(player, "%Versus_myTop%");
                String score = PlaceholderAPI.setPlaceholders(player, "%Versus_myScore%");
                String tier = PlaceholderAPI.setPlaceholders(player, "%Versus_myTier%");
                String rate = PlaceholderAPI.setPlaceholders(player, "%Versus_myRate%");
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#5798FF [영혼의 대결] &f" + player.getName() + "님의 정보입니다."));
                player.sendMessage("");
                player.sendMessage(ColorManager.format("&f  승점: #A3C7FF" + score + "§f점"));
                player.sendMessage(ColorManager.format("&f  티어: &f" + tier));
                player.sendMessage(ColorManager.format("&f  랭킹: #FFFDA3상위 " + top));
                player.sendMessage(ColorManager.format("&f  승률: #D8FFA3" + rate));
                player.sendMessage("");
                break;
            case "랭킹":
                String name1 = PlaceholderAPI.setPlaceholders(player, "%Versus_rankerName_1%");
                String score1 = PlaceholderAPI.setPlaceholders(player, "%Versus_score_1%");
                String tier1 = PlaceholderAPI.setPlaceholders(player, "%Versus_tier_1%");
                String rate1 = PlaceholderAPI.setPlaceholders(player, "%Versus_rate_1%");
                String top1 = PlaceholderAPI.setPlaceholders(player, "%Versus_top_1%");

                String name2 = PlaceholderAPI.setPlaceholders(player, "%Versus_rankerName_2%");
                String score2 = PlaceholderAPI.setPlaceholders(player, "%Versus_score_2%");
                String tier2 = PlaceholderAPI.setPlaceholders(player, "%Versus_tier_2%");
                String rate2 = PlaceholderAPI.setPlaceholders(player, "%Versus_rate_2%");
                String top2 = PlaceholderAPI.setPlaceholders(player, "%Versus_top_2%");

                String name3 = PlaceholderAPI.setPlaceholders(player, "%Versus_rankerName_3%");
                String score3 = PlaceholderAPI.setPlaceholders(player, "%Versus_score_3%");
                String tier3 = PlaceholderAPI.setPlaceholders(player, "%Versus_tier_3%");
                String rate3 = PlaceholderAPI.setPlaceholders(player, "%Versus_rate_3%");
                String top3 = PlaceholderAPI.setPlaceholders(player, "%Versus_top_3%");


                player.sendMessage("");
                player.sendMessage(ColorManager.format("#5798FF [영혼의 대결] &f 랭킹 정보입니다."));
                player.sendMessage("");
                player.sendMessage(ColorManager.format("&a  &7&o영광의 &a1위: &f" + tier1 + " " + name1 + " §8| &e" + score1 + "점 &7&o ( 상위 " + top1 + " ) &3승률: &f"+rate1));
                player.sendMessage(ColorManager.format("&a  &7&o열정의 &b2위: &f" + tier2 + " " + name2 + " §8| &e" + score2 + "점 &7&o ( 상위 " + top2 + " ) &3승률: &f"+rate2));
                player.sendMessage(ColorManager.format("&a  &7&o기적의 &e3위: &f" + tier3 + " " + name3 + " §8| &e" + score3 + "점 &7&o ( 상위 " + top3 + " ) &3승률: &f"+rate3));
                player.sendMessage("");
                break;
            case "티어":
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#5798FF [영혼의 대결] &f 티어 정보입니다."));
                player.sendMessage("");
                player.sendMessage(ColorManager.format("§f  Њ &6브론즈: &f승점 &60&f점 이상"));
                player.sendMessage(ColorManager.format("§f  Ќ &7실버: &f승점 &710&f점 이상"));
                player.sendMessage(ColorManager.format("§f  Ѵ &e골드: &f승점 &e25&f점 이상"));
                player.sendMessage(ColorManager.format("§f  Ѹ #00FF08에메랄드: &f승점 #00FF0840&f점 이상"));
                player.sendMessage(ColorManager.format("§f  љ #00F3FF다이아몬드: &f승점 #00F3FF55&f점 이상"));
                player.sendMessage(ColorManager.format("§f  њ #7544A9마스터: &f승점 #7544A975&f점 이상"));
                player.sendMessage("");
                break;


        }
//        CommandUtil.runCommandAsOP(player, "아포칼립스매치 매칭");
//        player.sendMessage("§3[영혼의 대결] §7§o(BETA) §f/대결 을 한번 더 입력하여 매칭에서 퇴장하실 수 있습니다.");
        return true;
    }
}
