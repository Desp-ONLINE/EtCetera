package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 얼라이언스 레벨(전 클래스 총 합 레벨) 랭킹 컨텐츠.
 *
 * <p>MMO-Alliance의 티어 해금 기준인 총 합 레벨(저장된 모든 클래스 레벨 + 현재 클래스 레벨)을
 * 온라인 플레이어에서 계산해 DB에 저장하고 상위 10명을 표시한다.
 * 접속 기록이 없는 유저는 MMO-Alliance DB의 해금 기록에서 최고 티어 레벨(총 합 레벨의 하한)을
 * 가져와 랭킹에 포함하고, 접속해서 정확한 값이 계산되면 그 값이 우선한다.
 */
public class AllianceLevelRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /** 랭킹 집계·표시에서 제외할 닉네임 (운영자 계정) */
    private static final Set<String> EXCLUDED_NICKNAMES = Set.of("dople_L");

    /* ===== 색상 팔레트 (hex) ===== */
    /* 파란색 테마 */
    private static final String C_STAR = hex("#1E6FC4");      // 헤더 장식 별 (코발트)
    private static final String C_TITLE = hex("#4DA6FF");     // 헤더 타이틀 (선명한 파랑)
    private static final String C_RANK_ETC = hex("#5B8DD9");  // 4위 이하 (푸른 회색)
    private static final String C_NICKNAME = hex("#C9E4FF");  // 4위 이하 닉네임 (연한 파랑)
    private static final String C_SEP = hex("#5A5A6E");       // 구분 기호
    private static final String C_LEVEL = hex("#8FD3FF");     // 레벨 수치 (하늘색)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    private final MongoCollection<Document> collection;
    private final com.mongodb.client.MongoDatabase allianceDatabase;
    private final MMOCoreAPI mmoCoreAPI;

    private final Map<UUID, Integer> snapshot = new LinkedHashMap<>();
    private final Map<UUID, String> names = new LinkedHashMap<>();

    /** 마지막 집계의 uuid → {공동 순위, 총 합 레벨}. 개인 순위 줄 계산에 사용 */
    private volatile Map<String, int[]> lastRanking = Map.of();

    public AllianceLevelRankingProvider() {
        this.collection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("AllianceLevelRanking");
        this.allianceDatabase = DatabaseRegister.getInstance().getMongoClient().getDatabase("MMO-Alliance");
        this.mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
    }

    @Override
    public String getContentKey() {
        return "얼라이언스";
    }

    @Override
    public org.bukkit.Color getParticleColor() {
        return org.bukkit.Color.fromRGB(0x4DA6FF);
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
                snapshot.put(player.getUniqueId(), calculateLevelSum(player));
                names.put(player.getUniqueId(), player.getName());
            } catch (Exception e) {
                // MMO 데이터가 아직 로드되지 않은 플레이어는 이번 사이클에서 제외
            }
        }
    }

    /** MMO-Alliance의 티어 해금 기준과 동일한 총 합 레벨 (저장된 클래스 레벨 합 + 현재 클래스 레벨) */
    private int calculateLevelSum(Player player) {
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        int sum = 0;
        for (String savedClass : playerData.getSavedClasses()) {
            SavedClassInformation classInfo = playerData.getClassInfo(savedClass);
            if (classInfo == null) {
                continue;
            }
            sum += classInfo.getLevel();
        }
        sum += playerData.getLevel();
        return sum;
    }

    @Override
    public List<String> buildLines() {
        Date now = new Date();
        for (Map.Entry<UUID, Integer> entry : snapshot.entrySet()) {
            Document document = new Document()
                    .append("uuid", entry.getKey().toString())
                    .append("nickname", names.get(entry.getKey()))
                    .append("levelSum", entry.getValue())
                    .append("updatedAt", now);
            collection.replaceOne(new Document("uuid", entry.getKey().toString()),
                    document, new ReplaceOptions().upsert(true));
        }

        // 경고 누적(5회 이상) 유저 제외
        WarnedPlayerFilter.Excluded warned = WarnedPlayerFilter.load();

        // 접속 중 계산된 정확한 값 (우선)
        Map<String, Integer> levelByUuid = new LinkedHashMap<>();
        Map<String, String> nicknameByUuid = new LinkedHashMap<>();
        for (Document document : collection.find()) {
            String uuid = document.getString("uuid");
            String nickname = document.getString("nickname");
            if (uuid == null || nickname == null || EXCLUDED_NICKNAMES.contains(nickname)
                    || warned.contains(uuid, nickname)) {
                continue;
            }
            levelByUuid.put(uuid, document.get("levelSum", Number.class).intValue());
            nicknameByUuid.put(uuid, nickname);
        }

        // 접속 기록이 없는 유저는 MMO-Alliance 해금 기록의 최고 티어 레벨(총 합 레벨의 하한)로 보충
        Map<Integer, Integer> tierLevels = new LinkedHashMap<>();
        for (Document tier : allianceDatabase.getCollection("Alliance").find()) {
            Number id = tier.get("id", Number.class);
            Number level = tier.get("level", Number.class);
            if (id != null && level != null) {
                tierLevels.merge(id.intValue(), level.intValue(), Math::max);
            }
        }
        for (Document player : allianceDatabase.getCollection("Player").find()) {
            String uuid = player.getString("id");
            String nickname = player.getString("nickname");
            if (uuid == null || nickname == null
                    || EXCLUDED_NICKNAMES.contains(nickname) || warned.contains(uuid, nickname)
                    || levelByUuid.containsKey(uuid)) {
                continue;
            }
            List<Number> allianceIds = player.getList("allianceIds", Number.class);
            if (allianceIds == null) {
                continue;
            }
            int maxTierLevel = 0;
            for (Number allianceId : allianceIds) {
                maxTierLevel = Math.max(maxTierLevel, tierLevels.getOrDefault(allianceId.intValue(), 0));
            }
            if (maxTierLevel <= 0) {
                continue;
            }
            levelByUuid.put(uuid, maxTierLevel);
            nicknameByUuid.put(uuid, nickname);
        }

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(levelByUuid.entrySet());
        ranking.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        List<String> lines = new ArrayList<>();
        lines.add(C_STAR + "✦ " + C_TITLE + "얼라이언스 레벨 랭킹 TOP " + RANK_SIZE + C_STAR + " ✦");
        if (ranking.isEmpty()) {
            lines.add(C_EMPTY + "집계된 데이터가 없습니다.");
        }
        // 동점자는 공동 순위(같은 "N위")로 표시하고, 다음 순위는 인원수만큼 건너뛴다 (1,1,3위 방식)
        // 개인 순위 줄을 위해 전체 순위를 계산해 캐시하고, 홀로그램에는 상위 RANK_SIZE명만 표시한다
        Map<String, int[]> rankByUuid = new LinkedHashMap<>();
        int rank = 0;
        int previousLevel = Integer.MIN_VALUE;
        for (int i = 0; i < ranking.size(); i++) {
            Map.Entry<String, Integer> entry = ranking.get(i);
            if (entry.getValue() != previousLevel) {
                rank = i + 1;
                previousLevel = entry.getValue();
            }
            rankByUuid.put(entry.getKey(), new int[]{rank, entry.getValue()});
            if (i < RANK_SIZE) {
                lines.add(RankingHologramManager.rankLabel(rank, C_RANK_ETC)
                        + RankingHologramManager.nicknameColor(rank, C_NICKNAME) + nicknameByUuid.get(entry.getKey())
                        + C_SEP + " : "
                        + C_LEVEL + "Lv." + entry.getValue());
            }
        }
        lastRanking = rankByUuid;
        return lines;
    }

    @Override
    public Map<UUID, String> buildViewerLines(Map<UUID, String> viewers) {
        Map<String, int[]> ranking = lastRanking;
        Map<UUID, String> lines = new LinkedHashMap<>();
        for (UUID uuid : viewers.keySet()) {
            int[] rankAndLevel = ranking.get(uuid.toString());
            if (rankAndLevel == null) {
                lines.put(uuid, RankingHologramManager.C_VIEWER + "▸ 내 순위" + C_SEP + " : " + C_EMPTY + "기록 없음");
                continue;
            }
            lines.put(uuid, RankingHologramManager.C_VIEWER + "▸ 내 순위" + C_SEP + " : "
                    + RankingHologramManager.rankLabel(rankAndLevel[0], C_RANK_ETC)
                    + C_LEVEL + "Lv." + rankAndLevel[1]);
        }
        return lines;
    }
}
