package org.swlab.etcetera.Convinience;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.SkillSequenceRepository;
import org.swlab.etcetera.Util.DataSyncCompat;
import org.swlab.etcetera.Util.MergedWeaponUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 자동 연계 시스템 시전기.
 * 등록한 합성무기를 순서대로 돌며, 인벤토리에 있는 무기의 액티브 스킬을 일정 틱 간격으로 자동 시전한다.
 * - 인벤토리에 없는 무기는 건너뛴다(시전 시점에 다시 확인).
 * - 쿨타임/마나가 부족한 스킬은 조용히 건너뛴다.
 * - 스킬 시전은 MMOItems 의 AbilityData.cast 를 그대로 사용하므로 쿨타임 적용·마나 소모·
 *   PlayerCastSkillEvent(마을 시전 금지 등) 가 손으로 쓸 때와 동일하게 처리된다.
 */
public final class SkillSequenceRunner {

    public static final String MSG_PREFIX = "§a[자동 연계] §f";
    public static final int DEFAULT_TICK_INTERVAL = 3;

    private static final Set<UUID> RUNNING = ConcurrentHashMap.newKeySet();
    /** 자동 연계가 지금 이 순간 cast 를 호출 중인 플레이어. 그 사이 발생한 PlayerCastSkillEvent 는 자동 연계의 것이다. */
    private static final Set<UUID> CASTING = ConcurrentHashMap.newKeySet();
    /** /스킬 디버그 로 켠 플레이어. 판단 단계마다 채팅과 콘솔에 사유를 출력한다. */
    private static final Set<UUID> DEBUG = ConcurrentHashMap.newKeySet();

    private SkillSequenceRunner() {
    }

    public static boolean toggleDebug(Player player) {
        UUID uuid = player.getUniqueId();
        if (DEBUG.remove(uuid)) return false;
        DEBUG.add(uuid);
        return true;
    }

    public static boolean isDebug(Player player) {
        return DEBUG.contains(player.getUniqueId());
    }

    public static void debug(Player player, String message) {
        if (!DEBUG.contains(player.getUniqueId())) return;
        player.sendMessage("§8[연계 디버그] §7" + message);
        EtCetera.getInstance().getLogger().info("[연계 디버그] " + player.getName() + " : " + message);
    }

    /** 시전 한 단계 = (무기 키, 그 무기의 몇 번째 액티브 스킬인지). */
    private record Step(String key, int abilityIndex) {
    }

    public static boolean isRunning(Player player) {
        return RUNNING.contains(player.getUniqueId());
    }

    /** 현재 처리 중인 스킬 시전 이벤트가 자동 연계에서 비롯된 것인지. */
    public static boolean isCastingByRunner(Player player) {
        return CASTING.contains(player.getUniqueId());
    }

    public static int getTickInterval() {
        int interval = EtCetera.getInstance().getConfig().getInt("skillSequence.tickInterval", DEFAULT_TICK_INTERVAL);
        return Math.max(1, interval);
    }

    /** 트리거 아이템 우클릭 / 명령어 : 1번째 무기부터 전부 시전. */
    public static void run(Player player) {
        run(player, 0, true);
    }

    /** 1번째 무기의 스킬을 직접 사용했을 때 : 2번째 무기부터 이어서 시전. 연계할 무기가 없으면 조용히 끝난다. */
    public static void runChained(Player player) {
        run(player, 1, false);
    }

    private static void run(Player player, int startIndex, boolean verbose) {
        UUID uuid = player.getUniqueId();
        debug(player, "run 호출 startIndex=" + startIndex);
        if (RUNNING.contains(uuid)) {
            debug(player, "중단: 이미 시전 중");
            if (verbose) player.sendMessage(MSG_PREFIX + "§c이미 자동 연계를 시전 중입니다.");
            return;
        }
        if (DataSyncCompat.isDataLoading(player)) {
            debug(player, "중단: DataSync 데이터 로드 중");
            if (verbose) player.sendMessage(MSG_PREFIX + "§c데이터가 로드중입니다.");
            return;
        }
        List<String> keys = SkillSequenceRepository.getInstance().getSequence(player);
        debug(player, "등록 목록=" + keys);
        if (keys.isEmpty()) {
            if (verbose) player.sendMessage(MSG_PREFIX + "§c등록된 무기가 없습니다. §7/스킬 §c로 무기를 등록하세요.");
            return;
        }
        if (startIndex >= keys.size()) {
            debug(player, "중단: 이어서 시전할 무기가 없음");
            return;
        }

        // 시작 시점에 인벤토리에 있는 무기만으로 단계를 계획한다. 각 단계는 실행 시점에 다시 검사한다.
        List<Step> steps = new ArrayList<>();
        for (String key : keys.subList(startIndex, keys.size())) {
            ItemStack weapon = MergedWeaponUtil.findInInventory(player, key);
            if (weapon == null) {
                debug(player, key + " : 인벤토리에 없음 → 건너뜀");
                continue;
            }
            List<AbilityData> abilities = MergedWeaponUtil.activeAbilities(weapon);
            debug(player, key + " : 액티브 스킬 " + abilities.size() + "개 " + describe(abilities));
            for (int i = 0; i < abilities.size(); i++) {
                steps.add(new Step(key, i));
            }
        }
        if (steps.isEmpty()) {
            debug(player, "중단: 시전할 단계가 없음");
            if (verbose) player.sendMessage(MSG_PREFIX + "§c인벤토리에 사용할 수 있는 등록 무기가 없습니다.");
            return;
        }

        debug(player, "시작: " + steps.size() + "단계, 간격 " + getTickInterval() + "틱");
        RUNNING.add(uuid);
        new SequenceTask(player, steps).runTaskTimer(EtCetera.getInstance(), 0L, getTickInterval());
    }

    private static String describe(List<AbilityData> abilities) {
        List<String> names = new ArrayList<>();
        for (AbilityData ability : abilities) {
            names.add(ability.getHandler().getId() + "@" + ability.getTrigger().name());
        }
        return names.toString();
    }

    private static final class SequenceTask extends BukkitRunnable {

        private final Player player;
        private final List<Step> steps;
        private int index = 0;
        private String lastAnnouncedKey = null;

        private SequenceTask(Player player, List<Step> steps) {
            this.player = player;
            this.steps = steps;
        }

        @Override
        public void run() {
            if (!player.isOnline() || index >= steps.size()) {
                finish();
                return;
            }
            Step step = steps.get(index++);
            try {
                execute(step);
            } catch (Throwable t) {
                EtCetera.getInstance().getLogger().warning("[자동 연계] " + player.getName() + " 시전 중 오류 (" + step.key() + "): " + t);
            }
            if (index >= steps.size()) {
                finish();
            }
        }

        private void finish() {
            RUNNING.remove(player.getUniqueId());
            cancel();
        }

        private void execute(Step step) {
            // 시전 시점에 인벤토리에 없으면 사용하지 않는다.
            ItemStack weapon = MergedWeaponUtil.findInInventory(player, step.key());
            if (weapon == null) {
                debug(player, step.key() + " : 시전 시점에 인벤토리에 없음");
                return;
            }

            List<AbilityData> abilities = MergedWeaponUtil.activeAbilities(weapon);
            if (step.abilityIndex() >= abilities.size()) return;
            AbilityData ability = abilities.get(step.abilityIndex());
            String abilityName = ability.getHandler().getId();

            MMOPlayerData mmoPlayerData = MMOPlayerData.get(player.getUniqueId());
            if (mmoPlayerData.getCooldownMap().isOnCooldown(ability)) {
                debug(player, abilityName + " : 쿨타임 중 → 건너뜀");
                return;
            }
            if (ability.hasModifier("mana")) {
                PlayerData playerData = PlayerData.get(player);
                if (playerData.getRPG().getMana() < ability.getParameter("mana")) {
                    debug(player, abilityName + " : 마나 부족 → 건너뜀");
                    return;
                }
            }

            if (!step.key().equals(lastAnnouncedKey)) {
                player.sendMessage(MSG_PREFIX + MergedWeaponUtil.displayName(weapon) + "§f의 스킬을 사용합니다!");
                lastAnnouncedKey = step.key();
            }
            // cast 안에서 PlayerCastSkillEvent 가 동기적으로 발생하므로, 그 동안 플래그를 세워 리스너가 자동 연계의 시전임을 알게 한다.
            CASTING.add(player.getUniqueId());
            try {
                var result = ability.cast(new TriggerMetadata(mmoPlayerData, TriggerType.API));
                debug(player, abilityName + " : cast 호출, 핸들러 결과 성공=" + result.isSuccessful());
            } finally {
                CASTING.remove(player.getUniqueId());
            }
        }
    }
}
