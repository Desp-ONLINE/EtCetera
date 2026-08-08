package org.swlab.etcetera.Training.objects;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Util.CommandUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

@Getter
public class TrainingController {

    private static final Map<UUID, TrainingController> controllers = new java.util.HashMap<>();

    public static TrainingController get(Player player) {
        return controllers.get(player.getUniqueId());
    }

    private static final BukkitAPIHelper mythicMobAPI = new BukkitAPIHelper();
    private static final double DECAY_RATE = 0.9;
    private static final int MAX_COUNT = 60;

    private final TrainingRoom room;
    private final Player player;
    private final Location previousLocation;
    private final CombatAnalysis combatAnalysis = new CombatAnalysis();
    private int hitCount;
    private double totalDamage;
    private double dps;
    private double peakDps;
    private int taskId;

    private Entity entity;

    private int count;

    public TrainingController(TrainingRoom room, Player player) {
        this.previousLocation = player.getLocation();
        this.room = room;
        this.player = player;
        controllers.put(player.getUniqueId(), this);
    }

    /**
     * 누적 데미지는 EtCetera 의 DamageListener 가 커스텀 보정까지 마친 최종 데미지를 그대로 받아 쌓는다.
     * (MythicLib 원본 패킷 합을 쓰면 보스데미지·포션·오라 보정이 빠져 실제 표기값과 어긋난다)
     */
    public void addDamage(double damage, String source) {
        this.totalDamage += damage;
        this.dps += damage;
        this.peakDps = Math.max(peakDps, dps);

        combatAnalysis.record(source, damage);
        hitCount += 1;
        actionbar();
    }


    private void actionbar() {
        String format = String.format(
                "§f[§e남은 시간 %s§f] [§c누적 데미지 %s§f] [§d타수 %s§f] [§6DPS %s§f]",
                (MAX_COUNT - count),
                NumberUtil.applyComma(totalDamage),
                hitCount,
                NumberUtil.applyComma(dps)
        );
        TextComponent text = Component.text(format);
        player.sendActionBar(text);
    }

    protected void start() {
        player.teleport(room.getLocation().toLocation());
        try {
            entity = mythicMobAPI.spawnMythicMob("허수아비", room.getEntityLocation().toLocation());
        } catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }

        taskId = Bukkit.getScheduler().runTaskTimer(EtCetera.getInstance(), () -> {
            if (count == MAX_COUNT) {
                room.quit();
                CommandUtil.runCommandAsOP(player, "spawn");
                return;
            }

            dps *= DECAY_RATE;

            if (dps < 0.01) {
                dps = 0;
            }

            count++;
            actionbar();
        }, 20, 20).getTaskId();
    }


    protected void stop() {
        entity.remove();
        player.teleport(previousLocation);
        controllers.remove(player.getUniqueId());
        Bukkit.getScheduler().cancelTask(taskId);

        String job = getPlayerJob(player);
        TrainingManager.getInstance().getRankingManager()
                .addRecord(player.getUniqueId(), player.getName(), job, totalDamage);

        double averageDps = totalDamage / Math.max(1, count);
        StringBuilder result = new StringBuilder("""

                  #FFD700━━━━━ #FFA500훈련 결과 #FFD700━━━━━

                  #55FF55직업 #777777: #FFFFFF%s
                  #FF6347누적 데미지 #777777: #FFFFFF%s
                  #DA70D6타수 #777777: #FFFFFF%s
                  #FFA500평균 DPS #777777: #FFFFFF%s
                  #FFA500최고 DPS #777777: #FFFFFF%s
                """.formatted(job, NumberUtil.applyComma(totalDamage), hitCount,
                NumberUtil.applyComma(averageDps), NumberUtil.applyComma(peakDps)));

        if (!combatAnalysis.isEmpty()) {
            double analysisTotal = combatAnalysis.getTotalDamage();
            result.append("\n  #FFD700━━━━━ #FFA500전투 분석 #FFD700━━━━━\n\n");
            int rank = 1;
            for (CombatAnalysis.Record record : combatAnalysis.sortedByDamage()) {
                double share = analysisTotal <= 0 ? 0 : record.getTotalDamage() / analysisTotal * 100;
                result.append("  #FFFFFF%d. %s #777777- #FF6347%s #777777(%.1f%% | %d타 | 최대 %s)\n"
                        .formatted(rank++, record.getSource(), NumberUtil.applyComma(record.getTotalDamage()),
                                share, record.getHits(), NumberUtil.applyComma(record.getMaxHit())));
            }
        }
        result.append("\n");
        player.sendMessage(ColorManager.format(result.toString()));

        MMOPlayerData.get(player).getCooldownMap().clearAllCooldowns();
    }

    private static String getPlayerJob(Player player) {
        try {
            if (Bukkit.getPluginManager().getPlugin("MMOCore") == null) {
                return "기본";
            }
            Class<?> playerDataClass = Class.forName("net.Indyuce.mmocore.api.player.PlayerData");
            Method getMethod = playerDataClass.getMethod("get", UUID.class);
            Object playerData = getMethod.invoke(null, player.getUniqueId());
            Object profess = playerData.getClass().getMethod("getProfess").invoke(playerData);
            String name = (String) profess.getClass().getMethod("getName").invoke(profess);
            return name != null && !name.isEmpty() ? name : "기본";
        } catch (Exception e) {
            return "기본";
        }
    }
}
