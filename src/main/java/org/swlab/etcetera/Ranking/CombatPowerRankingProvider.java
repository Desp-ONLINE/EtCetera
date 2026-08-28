package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Util.CombatPowerUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 전투력 랭킹 컨텐츠.
 *
 * <p>온라인 플레이어의 전투력을 계산해 DB에 저장하고 상위 10명을 표시한다.
 * 오프라인 플레이어는 마지막 접속 중 저장된 값으로 랭킹에 남는다.
 */
public class CombatPowerRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /** 랭킹 집계·표시에서 제외할 닉네임 (운영자 계정) */
    private static final Set<String> EXCLUDED_NICKNAMES = Set.of("dople_L");

    /* ===== 색상 팔레트 (hex) ===== */

    /* 빨간색 테마 */
    private static final String C_STAR = hex("#C41E3A");      // 헤더 장식 별 (진홍)
    private static final String C_TITLE = hex("#FF3B3B");     // 헤더 타이틀 (선명한 빨강)
    private static final String C_RANK_1 = hex("#FFD700");    // 1위 금색
    private static final String C_RANK_2 = hex("#C7D6E8");    // 2위 은색
    private static final String C_RANK_3 = hex("#E8883A");    // 3위 동색
    private static final String C_RANK_ETC = hex("#E05252");  // 4위 이하 (붉은 회색)
    private static final String C_NICKNAME = hex("#FFFFFF");  // 닉네임
    private static final String C_SEP = hex("#5A5A6E");       // 구분 기호
    private static final String C_POWER = hex("#FF8C69");     // 전투력 수치 (연한 주홍)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    private final MongoCollection<Document> collection;

    private final Map<UUID, Long> snapshot = new LinkedHashMap<>();
    private final Map<UUID, String> names = new LinkedHashMap<>();

    public CombatPowerRankingProvider() {
        this.collection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("CombatPowerRanking");
    }

    @Override
    public String getContentKey() {
        return "전투력";
    }

    @Override
    public org.bukkit.Color getParticleColor() {
        return org.bukkit.Color.fromRGB(0xFF3B3B);
    }

    @Override
    public void collectSync() {
        snapshot.clear();
        names.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (EXCLUDED_NICKNAMES.contains(player.getName())) {
                continue;
            }
            try {
                snapshot.put(player.getUniqueId(), CombatPowerUtil.calculate(player));
                names.put(player.getUniqueId(), player.getName());
            } catch (Exception e) {
                // MMO 데이터가 아직 로드되지 않은 플레이어는 이번 사이클에서 제외
            }
        }
    }

    @Override
    public List<String> buildLines() {
        Date now = new Date();
        for (Map.Entry<UUID, Long> entry : snapshot.entrySet()) {
            Document document = new Document()
                    .append("uuid", entry.getKey().toString())
                    .append("nickname", names.get(entry.getKey()))
                    .append("combatPower", entry.getValue())
                    .append("updatedAt", now);
            collection.replaceOne(new Document("uuid", entry.getKey().toString()),
                    document, new ReplaceOptions().upsert(true));
        }

        // 과거 사이클에 저장됐을 수 있는 제외 대상도 조회에서 걸러낸다
        List<Document> top = new ArrayList<>();
        collection.find(new Document("nickname", new Document("$nin", new ArrayList<>(EXCLUDED_NICKNAMES))))
                .sort(new Document("combatPower", -1)).limit(RANK_SIZE).into(top);

        List<String> lines = new ArrayList<>();
        lines.add(C_STAR + "✦ " + C_TITLE + "전투력 랭킹 TOP " + RANK_SIZE + C_STAR + " ✦");
        if (top.isEmpty()) {
            lines.add(C_EMPTY + "집계된 데이터가 없습니다.");
        }
        for (int i = 0; i < top.size(); i++) {
            Document document = top.get(i);
            String nickname = document.getString("nickname");
            long combatPower = document.get("combatPower", Number.class).longValue();
            lines.add(rankColor(i + 1) + (i + 1) + "위 "
                    + C_NICKNAME + nickname
                    + C_SEP + " : "
                    + C_POWER + CombatPowerUtil.toKoreanUnit(combatPower));
        }
        return lines;
    }

    private String rankColor(int rank) {
        switch (rank) {
            case 1:
                return C_RANK_1;
            case 2:
                return C_RANK_2;
            case 3:
                return C_RANK_3;
            default:
                return C_RANK_ETC;
        }
    }
}
