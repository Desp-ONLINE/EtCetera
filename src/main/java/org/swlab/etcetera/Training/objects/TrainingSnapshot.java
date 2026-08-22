package org.swlab.etcetera.Training.objects;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import lombok.Getter;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Util.CombatPowerUtil;
import org.swlab.etcetera.Util.JobWeaponUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 훈련 종료 시점의 플레이어 상태 기록.
 * 관리자용 훈련 정보 GUI 와 밸런스 분석용 세션 로그(TrainingSessionLogger)의 원본이 된다.
 */
@Getter
public class TrainingSnapshot {

    public record SkillUsage(String itemId, String itemName, int hits, double totalDamage, double maxHit) {
    }

    /** 10초 단위 구간 기록. dps = damage / 구간 길이(초) */
    public record Interval(int startSec, int endSec, double damage, double dps) {
    }

    /** 훈련 종료 시점 주무기. itemId 는 MMOItems 아이템이 아니면 null, tier/enhance 는 직업무기가 아니면 null */
    public record WeaponInfo(String itemId, String name, String tier, String enhance) {
    }

    public static final int INTERVAL_SECONDS = 10;

    /** 밸런스 분석용 수치 스텟 키 (MythicLib 스텟 이름 그대로 저장한다) */
    private static final List<String> STAT_KEYS = List.of(
            "SKILL_DAMAGE", "MAX_HEALTH", "SKILL_CRITICAL_STRIKE_CHANCE", "SKILL_CRITICAL_STRIKE_POWER",
            "MOVEMENT_SPEED", "ADDITIONAL_EXPERIENCE", "MAX_MANA", "CUSTOM_BOSSDAMAGE"
    );

    private final UUID uuid;
    private final String playerName;
    private final String job;
    private final long capturedAt;
    private final double totalDamage;
    private final int hitCount;
    private final double peakDps;
    private final double averageDps;
    /** 실제 진행 시간(초) = 초당 기록 개수. 60 미만이면 중도 이탈 세션 */
    private final int durationSeconds;
    /** /정보 와 동일한 스텟 (표기 라벨 → 표기 값, GUI 용) */
    private final Map<String, String> stats;
    /** 밸런스 분석용 수치 스텟 (스텟 키 → 원본 값) */
    private final Map<String, Double> statValues;
    private final long combatPower;
    private final WeaponInfo weapon;
    /** 데미지 순으로 정렬된 사용 스킬(시전 아이템) 내역 */
    private final List<SkillUsage> skillUsages;
    /** 초당 데미지 (인덱스 = 경과 초) */
    private final List<Double> perSecondDamage;
    /** 10초 단위 구간별 데미지/DPS */
    private final List<Interval> intervals;

    private TrainingSnapshot(Player player, String job, double totalDamage, int hitCount,
                             double peakDps, double averageDps, CombatAnalysis combatAnalysis,
                             List<Double> perSecondDamage) {
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
        this.job = job;
        this.capturedAt = System.currentTimeMillis();
        this.totalDamage = totalDamage;
        this.hitCount = hitCount;
        this.peakDps = peakDps;
        this.averageDps = averageDps;
        this.perSecondDamage = List.copyOf(perSecondDamage);
        this.durationSeconds = this.perSecondDamage.size();
        this.intervals = toIntervals(this.perSecondDamage);
        this.stats = captureStats(player);
        this.statValues = captureStatValues(player);
        this.combatPower = CombatPowerUtil.calculate(player);
        this.weapon = captureWeapon(player);

        List<SkillUsage> usages = new ArrayList<>();
        for (CombatAnalysis.Record record : combatAnalysis.sortedByDamage()) {
            usages.add(new SkillUsage(record.getItemId(), record.getSource(),
                    record.getHits(), record.getTotalDamage(), record.getMaxHit()));
        }
        this.skillUsages = List.copyOf(usages);
    }

    public static TrainingSnapshot capture(Player player, String job, double totalDamage, int hitCount,
                                           double peakDps, double averageDps, CombatAnalysis combatAnalysis,
                                           List<Double> perSecondDamage) {
        return new TrainingSnapshot(player, job, totalDamage, hitCount, peakDps, averageDps,
                combatAnalysis, perSecondDamage);
    }

    private static List<Interval> toIntervals(List<Double> perSecond) {
        List<Interval> intervals = new ArrayList<>();
        for (int start = 0; start < perSecond.size(); start += INTERVAL_SECONDS) {
            int end = Math.min(start + INTERVAL_SECONDS, perSecond.size());
            double damage = 0;
            for (int i = start; i < end; i++) {
                damage += perSecond.get(i);
            }
            intervals.add(new Interval(start, end, damage, damage / Math.max(1, end - start)));
        }
        return List.copyOf(intervals);
    }

    /**
     * /정보 (InformationCommand) 와 동일한 항목을 동일한 반올림 규칙으로 캡처한다.
     */
    private static Map<String, String> captureStats(Player player) {
        StatMap statMap = MMOPlayerData.get(player).getStatMap();
        NumberFormat format = NumberFormat.getInstance();

        Map<String, String> stats = new LinkedHashMap<>();
        stats.put("공격력", "+" + format.format(round2(statMap.getStat("SKILL_DAMAGE"))) + "%");
        stats.put("체력", "+" + format.format(Math.round(round2(statMap.getStat("MAX_HEALTH")))));
        stats.put("크리티컬 확률", "+" + format.format(statMap.getStat("SKILL_CRITICAL_STRIKE_CHANCE")) + "%");
        stats.put("크리티컬 데미지", "+" + format.format(round2(statMap.getStat("SKILL_CRITICAL_STRIKE_POWER"))) + "%");
        stats.put("이동 속도", "+" + format.format(round2(statMap.getStat("MOVEMENT_SPEED"))) + "%");
        stats.put("경험치 획득량", "+" + format.format(round2(statMap.getStat("ADDITIONAL_EXPERIENCE"))) + "%");
        stats.put("최대 마나", "+" + format.format(round2(statMap.getStat("MAX_MANA"))));
        stats.put("보스 대상 공격력", "+" + format.format(round2(statMap.getStat("CUSTOM_BOSSDAMAGE"))) + "%");
        stats.put("전투력", CombatPowerUtil.calculateKorean(player));
        return stats;
    }

    private static Map<String, Double> captureStatValues(Player player) {
        StatMap statMap = MMOPlayerData.get(player).getStatMap();
        Map<String, Double> values = new LinkedHashMap<>();
        for (String key : STAT_KEYS) {
            values.put(key, statMap.getStat(key));
        }
        return values;
    }

    private static WeaponInfo captureWeapon(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return null;
        }
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                ? item.getItemMeta().getDisplayName()
                : item.getType().name();
        JobWeaponUtil.JobWeapon jobWeapon = JobWeaponUtil.parse(item);
        return new WeaponInfo(MMOItems.getID(item), name,
                jobWeapon == null ? null : String.valueOf(jobWeapon.tier()),
                jobWeapon == null ? null : jobWeapon.enhance());
    }

    private static double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
