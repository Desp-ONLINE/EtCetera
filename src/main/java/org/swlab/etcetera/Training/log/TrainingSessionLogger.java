package org.swlab.etcetera.Training.log;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Training.objects.TrainingSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * 밸런스 분석용 훈련 세션 로그.
 * 세션 1회 = 문서 1개(append-only)로 쌓는다. 캐시 없이 비동기 insert 만 한다.
 * 패치 전후 비교를 위해 config.yml 의 training.balanceVersion 을 함께 기록한다.
 */
public final class TrainingSessionLogger {

    public static final String COLLECTION = "TrainingSessionLog";

    private TrainingSessionLogger() {
    }

    public static MongoCollection<Document> collection() {
        return DatabaseRegister.getInstance().getMongoDatabase().getCollection(COLLECTION);
    }

    public static String balanceVersion() {
        return EtCetera.getInstance().getConfig().getString("training.balanceVersion", "v1");
    }

    public static void saveAsync(TrainingSnapshot snapshot) {
        Document document = toDocument(snapshot);
        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
            try {
                collection().insertOne(document);
            } catch (Exception e) {
                EtCetera.getInstance().getLogger().warning("훈련 세션 로그 저장 실패: " + e.getMessage());
            }
        });
    }

    private static Document toDocument(TrainingSnapshot snapshot) {
        Document document = new Document()
                .append("uuid", snapshot.getUuid().toString())
                .append("playerName", snapshot.getPlayerName())
                .append("job", snapshot.getJob())
                .append("timestamp", snapshot.getCapturedAt())
                .append("balanceVersion", balanceVersion())
                .append("durationSeconds", snapshot.getDurationSeconds())
                .append("totalDamage", snapshot.getTotalDamage())
                .append("hitCount", snapshot.getHitCount())
                .append("peakDps", snapshot.getPeakDps())
                .append("averageDps", snapshot.getAverageDps())
                .append("combatPower", snapshot.getCombatPower())
                .append("stats", new Document(new java.util.LinkedHashMap<String, Object>(snapshot.getStatValues())))
                .append("dpsPerSecond", snapshot.getPerSecondDamage());

        // 10초 단위 구간별 데미지/DPS
        List<Document> intervals = new ArrayList<>();
        for (TrainingSnapshot.Interval interval : snapshot.getIntervals()) {
            intervals.add(new Document()
                    .append("startSec", interval.startSec())
                    .append("endSec", interval.endSec())
                    .append("damage", interval.damage())
                    .append("dps", interval.dps()));
        }
        document.append("intervals10s", intervals);

        TrainingSnapshot.WeaponInfo weapon = snapshot.getWeapon();
        if (weapon != null) {
            document.append("weapon", new Document()
                    .append("itemId", weapon.itemId())
                    .append("name", weapon.name())
                    .append("tier", weapon.tier())
                    .append("enhance", weapon.enhance()));
        }

        List<Document> skills = new ArrayList<>();
        for (TrainingSnapshot.SkillUsage usage : snapshot.getSkillUsages()) {
            skills.add(new Document()
                    .append("itemId", usage.itemId())
                    .append("name", usage.itemName())
                    .append("hits", usage.hits())
                    .append("totalDamage", usage.totalDamage())
                    .append("maxHit", usage.maxHit()));
        }
        document.append("skills", skills);
        return document;
    }
}
