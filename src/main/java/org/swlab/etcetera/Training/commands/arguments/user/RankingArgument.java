package org.swlab.etcetera.Training.commands.arguments.user;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.ranking.DamageRankEntry;
import org.swlab.etcetera.Training.ranking.DamageRankingManager;

import java.util.ArrayList;
import java.util.List;

public class RankingArgument implements CommandArgument {

    public static final List<String> JOBS = List.of("파우스트", "크루세이더", "오베론", "제피르", "루인드", "인페르노", "판", "페이탈");
    public static final List<String> TAB_OPTIONS = new ArrayList<>();

    static {
        TAB_OPTIONS.add("모두");
        TAB_OPTIONS.addAll(JOBS);
    }

    private static final int DISPLAY_COUNT = 10;

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        String job = args[1];

        if (!job.equals("모두") && !JOBS.contains(job)) {
            sender.sendMessage(ColorManager.format("#FF6B6B존재하지 않는 직업입니다."));
            return false;
        }

        DamageRankingManager manager = TrainingManager.getInstance().getRankingManager();
        String title = job.equals("모두") ? "전체" : job;

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(ColorManager.format("  #FFD700━━━━━ #FFA500%s 훈련장 랭킹 #FFD700━━━━━".formatted(title)));
        sb.append("\n");

        boolean hasEntry = false;
        for (int i = 1; i <= DISPLAY_COUNT; i++) {
            DamageRankEntry entry = manager.getRank(job, i);
            if (entry == null) {
                break;
            }
            hasEntry = true;
            String medal = switch (i) {
                case 1 -> "#FFD700";
                case 2 -> "#C0C0C0";
                case 3 -> "#CD7F32";
                default -> "#AAAAAA";
            };
            if (job.equals("모두")) {
                sb.append(ColorManager.format("\n  %s%d위  #FFFFFF%s  #777777(#AAAAAA%s#777777)  #777777-  #FF6347%s"
                        .formatted(medal, i, entry.getPlayerName(), entry.getJob(), NumberUtil.applyComma(entry.getDamage()))));
            } else {
                sb.append(ColorManager.format("\n  %s%d위  #FFFFFF%s  #777777-  #FF6347%s"
                        .formatted(medal, i, entry.getPlayerName(), NumberUtil.applyComma(entry.getDamage()))));
            }
        }

        if (!hasEntry) {
            sb.append(ColorManager.format("\n  #999999기록이 없습니다."));
        }
        sb.append("\n");

        int myRank = manager.getPlayerRank(player.getUniqueId(), job);
        DamageRankEntry myEntry = manager.getPlayerEntry(player.getUniqueId(), job);
        sb.append(ColorManager.format("\n  #FFD700━━━━━━━━━━━━━━━━━━━━━━"));
        if (myEntry != null) {
            sb.append(ColorManager.format("\n  #55FF55내 순위: #FFFFFF%d위  #777777-  #FF6347%s".formatted(myRank, NumberUtil.applyComma(myEntry.getDamage()))));
        } else {
            sb.append(ColorManager.format("\n  #55FF55내 순위: #999999기록 없음"));
        }
        sb.append("\n");

        player.sendMessage(sb.toString());
        return true;
    }

    @Override
    public String getArg() {
        return "랭킹";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "<직업|모두> - 직업별 랭킹을 조회합니다.";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }
}
