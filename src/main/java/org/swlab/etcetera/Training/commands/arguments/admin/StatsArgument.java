package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Training.commands.arguments.user.RankingArgument;
import org.swlab.etcetera.Training.log.TrainingSessionLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 세션 로그 기반 직업 밸런스 통계.
 * 현재 balanceVersion 의 완주 세션만 대상으로, 유저별 최고 기록으로 그룹핑해
 * 반복 훈련으로 인한 표본 왜곡을 줄인다. 대표값은 평균 대신 중앙값을 쓴다.
 */
public class StatsArgument implements CommandArgument {

    private static final int TOP_SKILL_COUNT = 5;

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String job = args[1];
        if (!job.equals("모두") && !RankingArgument.JOBS.contains(job)) {
            sender.sendMessage(ColorManager.format("#FF6B6B존재하지 않는 직업입니다. (직업 이름 또는 '모두')"));
            return false;
        }

        String version = TrainingSessionLogger.balanceVersion();
        sender.sendMessage(ColorManager.format("#AAAAAA훈련 통계 집계 중... (버전 %s)".formatted(version)));

        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
            List<Document> sessions = new ArrayList<>();
            try {
                Bson filter = job.equals("모두")
                        ? Filters.eq("balanceVersion", version)
                        : Filters.and(Filters.eq("balanceVersion", version), Filters.eq("job", job));
                TrainingSessionLogger.collection().find(filter).into(sessions);
            } catch (Exception e) {
                runSync(() -> sender.sendMessage(ColorManager.format("#FF6B6B통계 조회 실패: " + e.getMessage())));
                return;
            }
            String message = buildMessage(job, version, sessions);
            runSync(() -> sender.sendMessage(message));
        });
        return true;
    }

    private static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(EtCetera.getInstance(), runnable);
    }

    private static String buildMessage(String job, String version, List<Document> sessions) {
        String title = job.equals("모두") ? "전체" : job;
        if (sessions.isEmpty()) {
            return ColorManager.format("#999999[%s] %s 훈련 기록이 없습니다.".formatted(version, title));
        }

        // 유저별 최고 기록(누적 데미지 기준)으로 그룹핑
        Map<String, Document> bestByUser = new HashMap<>();
        for (Document session : sessions) {
            String uuid = session.getString("uuid");
            Document best = bestByUser.get(uuid);
            if (best == null || damage(session) > damage(best)) {
                bestByUser.put(uuid, session);
            }
        }

        List<Double> bestDamages = bestByUser.values().stream().map(StatsArgument::damage).sorted().toList();
        List<Double> averageDpsList = sessions.stream().map(d -> toDouble(d.get("averageDps"))).sorted().toList();
        List<Double> combatPowers = bestByUser.values().stream()
                .map(d -> toDouble(d.get("combatPower"))).filter(v -> v > 0).toList();
        List<Double> efficiencies = bestByUser.values().stream()
                .filter(d -> toDouble(d.get("combatPower")) > 0)
                .map(d -> damage(d) / toDouble(d.get("combatPower")) * 10_000)
                .sorted().toList();

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(ColorManager.format("  #FFD700━━━━━ #FFA500%s 훈련 통계 #777777(%s) #FFD700━━━━━"
                .formatted(title, version))).append("\n\n");
        sb.append(ColorManager.format("  §7표본 : §f세션 %d개 §7/ §f유저 %d명".formatted(sessions.size(), bestByUser.size()))).append("\n");
        sb.append(ColorManager.format("  §7유저 최고 누적 데미지 : #FF6347%s §7(중앙값)  §f%s §7(평균)"
                .formatted(comma(median(bestDamages)), comma(mean(bestDamages))))).append("\n");
        sb.append(ColorManager.format("  §7세션 평균 DPS : §f%s §7(중앙값)".formatted(comma(median(averageDpsList))))).append("\n");
        if (!combatPowers.isEmpty()) {
            sb.append(ColorManager.format("  §7평균 전투력 : §f%s".formatted(comma(mean(combatPowers))))).append("\n");
        }
        if (!efficiencies.isEmpty()) {
            sb.append(ColorManager.format("  §7전투력 1만당 데미지 : #FDFFB6%s §7(중앙값)"
                    .formatted(comma(median(efficiencies))))).append("\n");
        }

        appendIntervalProfile(sb, sessions);
        appendTopSkills(sb, sessions);
        sb.append("\n");
        return sb.toString();
    }

    /** 세션 전체의 10초 구간별 평균 데미지 프로파일 (극딜형/지속딜형 구분용) */
    private static void appendIntervalProfile(StringBuilder sb, List<Document> sessions) {
        Map<Integer, double[]> sumAndCount = new LinkedHashMap<>(); // startSec -> [합, 개수]
        for (Document session : sessions) {
            List<Document> intervals = session.getList("intervals10s", Document.class);
            if (intervals == null) {
                continue;
            }
            for (Document interval : intervals) {
                double[] acc = sumAndCount.computeIfAbsent(interval.getInteger("startSec", 0), k -> new double[2]);
                acc[0] += toDouble(interval.get("damage"));
                acc[1]++;
            }
        }
        if (sumAndCount.isEmpty()) {
            return;
        }
        sb.append("\n").append(ColorManager.format("  #FFA500━━ 구간 평균 데미지 (10초) ━━")).append("\n");
        sumAndCount.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sb.append(ColorManager.format("  §7%d~%d초 : #FF6347%s"
                                .formatted(entry.getKey(), entry.getKey() + 10, comma(entry.getValue()[0] / entry.getValue()[1]))))
                        .append("\n"));
    }

    private static void appendTopSkills(StringBuilder sb, List<Document> sessions) {
        Map<String, Double> damageBySkill = new HashMap<>();
        Map<String, Integer> hitsBySkill = new HashMap<>();
        double total = 0;
        for (Document session : sessions) {
            List<Document> skills = session.getList("skills", Document.class);
            if (skills == null) {
                continue;
            }
            for (Document skill : skills) {
                String name = skill.getString("name");
                double skillDamage = toDouble(skill.get("totalDamage"));
                damageBySkill.merge(name, skillDamage, Double::sum);
                hitsBySkill.merge(name, skill.getInteger("hits", 0), Integer::sum);
                total += skillDamage;
            }
        }
        if (damageBySkill.isEmpty()) {
            return;
        }
        double finalTotal = total;
        List<Map.Entry<String, Double>> top = damageBySkill.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(TOP_SKILL_COUNT)
                .toList();
        sb.append("\n").append(ColorManager.format("  #FFA500━━ 스킬 지분 TOP %d ━━".formatted(TOP_SKILL_COUNT))).append("\n");
        int rank = 1;
        for (Map.Entry<String, Double> entry : top) {
            double share = finalTotal <= 0 ? 0 : entry.getValue() / finalTotal * 100;
            sb.append(ColorManager.format("  §f%d. %s §7- #FF6347%s §7(%.1f%% | %d타)"
                            .formatted(rank++, entry.getKey(), comma(entry.getValue()), share, hitsBySkill.get(entry.getKey()))))
                    .append("\n");
        }
    }

    private static double damage(Document session) {
        return toDouble(session.get("totalDamage"));
    }

    private static double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0;
    }

    private static double median(List<Double> sorted) {
        if (sorted.isEmpty()) {
            return 0;
        }
        int mid = sorted.size() / 2;
        return sorted.size() % 2 == 1 ? sorted.get(mid) : (sorted.get(mid - 1) + sorted.get(mid)) / 2;
    }

    private static double mean(List<Double> values) {
        return values.isEmpty() ? 0 : values.stream().mapToDouble(Double::doubleValue).sum() / values.size();
    }

    private static String comma(double value) {
        return NumberUtil.applyComma(Math.round(value));
    }

    @Override
    public String getArg() {
        return "통계";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "<직업|모두> - 현재 밸런스 버전의 훈련 세션 통계를 봅니다.";
    }

    @Override
    public String getPermission() {
        return "training.admin.stats";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
