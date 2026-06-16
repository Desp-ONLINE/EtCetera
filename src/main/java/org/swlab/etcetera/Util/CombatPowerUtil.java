package org.swlab.etcetera.Util;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import org.bukkit.entity.Player;

/**
 * 전투력(Combat Power) 계산 유틸.
 *
 * <p>계산 공식 (계수는 아래 상수에서 조정 가능 - 플레이스홀더 값):
 * <pre>
 *   크리배율   = 1 + (크리확률/100) × (크리데미지/100)
 *   보공배율   = 1 + (보공/100)
 *   공격력전투력 = 공격력% × 크리배율 × 보공배율 × ATTACK_PER_PERCENT
 *   체력전투력   = 체력 / HEALTH_UNIT × HEALTH_VALUE
 *   마나전투력   = 마나 / MANA_UNIT  × MANA_VALUE
 *   전투력      = 공격력전투력 + 체력전투력 + 마나전투력
 * </pre>
 * 보공(보스 대상 공격력)은 크리 연산이 끝난 공격력에 마지막으로 곱해진다.
 */
public class CombatPowerUtil {

    /* ===== 계수 (플레이스홀더 값 - 밸런싱 시 여기만 수정) ===== */

    /** 공격력 데미지 퍼센트 1%당 전투력 */
    public static final double ATTACK_PER_PERCENT = 1000.0;

    /** 체력 기준 단위 (이 수치 당 HEALTH_VALUE 전투력) */
    public static final double HEALTH_UNIT = 10000.0;
    /** 체력 HEALTH_UNIT 당 전투력 */
    public static final double HEALTH_VALUE = 2500.0;

    /** 마나 기준 단위 (이 수치 당 MANA_VALUE 전투력) */
    public static final double MANA_UNIT = 10.0;
    /** 마나 MANA_UNIT 당 전투력 */
    public static final double MANA_VALUE = 10000.0;

    /* ===== MMOCore 스탯 키 ===== */

    private static final String STAT_ATTACK = "SKILL_DAMAGE";
    private static final String STAT_CRIT_CHANCE = "SKILL_CRITICAL_STRIKE_CHANCE";
    private static final String STAT_CRIT_POWER = "SKILL_CRITICAL_STRIKE_POWER";
    private static final String STAT_BOSS_DAMAGE = "CUSTOM_BOSSDAMAGE";
    private static final String STAT_HEALTH = "MAX_HEALTH";
    private static final String STAT_MANA = "MAX_MANA";

    private CombatPowerUtil() {
    }

    /**
     * 플레이어의 전투력을 반올림한 정수로 반환한다.
     */
    public static long calculate(Player player) {
        return Math.round(calculateRaw(player));
    }

    /**
     * 반올림 전 원시 전투력 값.
     */
    public static double calculateRaw(Player player) {
        StatMap statMap = MMOPlayerData.get(player).getStatMap();

        double attackPercent = statMap.getStat(STAT_ATTACK);
        double critChance = statMap.getStat(STAT_CRIT_CHANCE);
        double critPower = statMap.getStat(STAT_CRIT_POWER);
        double bossDamage = statMap.getStat(STAT_BOSS_DAMAGE);
        double health = statMap.getStat(STAT_HEALTH);
        double mana = statMap.getStat(STAT_MANA);

        // 크리티컬 확률 1%당 공격력%에 (크리데미지/100) 만큼 기대값 가산
        double critMultiplier = 1.0 + (critChance / 100.0) * (critPower / 100.0);
        // 보공은 크리 연산이 끝난 공격력에 마지막으로 곱해진다
        double bossMultiplier = 1.0 + (bossDamage / 100.0);

        double attackPower = attackPercent * critMultiplier * bossMultiplier * ATTACK_PER_PERCENT;
        double healthPower = health / HEALTH_UNIT * HEALTH_VALUE;
        double manaPower = mana / MANA_UNIT * MANA_VALUE;

        return attackPower + healthPower + manaPower;
    }

    private static final long EOK = 100_000_000L; // 억
    private static final long MAN = 10_000L;       // 만

    /**
     * 숫자를 억/만 단위 한글 표기로 변환한다.
     * 예: 123456789 -> "1억 2345만 6789", 50000 -> "5만", 9999 -> "9999", 0 -> "0"
     */
    public static String toKoreanUnit(long value) {
        if (value == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        if (value < 0) {
            sb.append("-");
            value = -value;
        }

        long eok = value / EOK;
        long man = (value % EOK) / MAN;
        long rest = value % MAN;

        if (eok > 0) {
            sb.append(eok).append("억 ");
        }
        if (man > 0) {
            sb.append(man).append("만 ");
        }
        if (rest > 0) {
            sb.append(rest);
        }

        return sb.toString().trim();
    }

    /**
     * 플레이어의 전투력을 억/만 단위 한글 표기로 반환한다.
     */
    public static String calculateKorean(Player player) {
        return toKoreanUnit(calculate(player));
    }
}
