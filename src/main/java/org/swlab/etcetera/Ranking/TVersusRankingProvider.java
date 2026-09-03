package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 대결(TVersus) 랭킹 컨텐츠.
 *
 * <p>TVersus 플러그인의 MongoDB(TVersus.VersusPlayer)를 직접 읽는다.
 * 플러그인 간 클래스 의존이 없어 EtCetera 단독 리로드에도 안전하다.
 * 레이팅 내림차순(동점이면 닉네임순) 상위 10명을 점수·티어와 함께 표시한다.
 * 티어는 문서에 저장되지 않고 레이팅에서 계산하며, 기준은 TVersus의 Tier enum과 동일하다.
 */
public class TVersusRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /** 랭킹 집계·표시에서 제외할 닉네임 (운영자 계정) */
    private static final Set<String> EXCLUDED_NICKNAMES = Set.of("dople_L");

    /* ===== 색상 팔레트 (hex) ===== */
    /* 보라색 테마 */
    private static final String C_STAR = hex("#7A3FB5");      // 헤더 장식 별 (진보라)
    private static final String C_TITLE = hex("#B266FF");     // 헤더 타이틀 (선명한 보라)
    private static final String C_RANK_ETC = hex("#9B7BC9");  // 4~5위 (보랏빛 회색)
    private static final String C_NICKNAME = hex("#E3CCFF");  // 4~5위 닉네임 (연한 보라)
    private static final String C_SEP = hex("#5A5A6E");       // 구분 기호
    private static final String C_SCORE = hex("#CBA6FF");     // 점수 수치 (밝은 라벤더)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    /* ===== 티어 (TVersus Tier enum과 동일 기준·색상) ===== */
    private static final int[] TIER_MIN_RATINGS = {100, 80, 60, 40, 25, 10, 0};
    private static final String[] TIER_NAMES = {"그랜드마스터", "마스터", "다이아몬드", "플래티넘", "골드", "실버", "브론즈"};
    private static final String[] TIER_COLORS = {
            hex("#FF4444"), hex("#9B59B6"), hex("#00BFFF"), hex("#00FFCC"),
            hex("#FFD700"), hex("#C0C0C0"), hex("#CD7F32")};

    private final MongoCollection<Document> collection;

    /** 마지막 집계의 전체 랭킹 (내림차순). 개인 순위 줄 계산에 사용 */
    private volatile List<Document> lastRanking = List.of();

    public TVersusRankingProvider() {
        this.collection = DatabaseRegister.getInstance().getMongoClient()
                .getDatabase("TVersus").getCollection("VersusPlayer");
    }

    @Override
    public String getContentKey() {
        return "대결";
    }

    @Override
    public org.bukkit.Color getParticleColor() {
        return org.bukkit.Color.fromRGB(0xB266FF);
    }

    @Override
    public List<String> buildLines() {
        // 경고 누적(5회 이상) 유저 제외
        WarnedPlayerFilter.Excluded warned = WarnedPlayerFilter.load();
        List<String> excludedNicknames = new ArrayList<>(EXCLUDED_NICKNAMES);
        excludedNicknames.addAll(warned.nicknames());

        // isExample: 스키마 예시 문서 제외. 접속만으로 rating 0 문서가 생기므로 한 판이라도 한 유저만 집계
        Document filter = new Document("isExample", new Document("$ne", true))
                .append("playerName", new Document("$nin", excludedNicknames))
                .append("playerId", new Document("$nin", new ArrayList<>(warned.uuids())))
                .append("$or", List.of(
                        new Document("rating", new Document("$gt", 0)),
                        new Document("wins", new Document("$gt", 0)),
                        new Document("losses", new Document("$gt", 0)),
                        new Document("draws", new Document("$gt", 0))));

        List<Document> ranking = collection.find(filter)
                .sort(Sorts.orderBy(Sorts.descending("rating"), Sorts.ascending("playerName")))
                .into(new ArrayList<>());
        lastRanking = ranking;
        List<Document> top = ranking.subList(0, Math.min(RANK_SIZE, ranking.size()));

        List<String> lines = new ArrayList<>();
        lines.add(C_STAR + "✦ " + C_TITLE + "대결 랭킹 TOP " + RANK_SIZE + C_STAR + " ✦");
        if (top.isEmpty()) {
            lines.add(C_EMPTY + "집계된 데이터가 없습니다.");
        }
        for (int i = 0; i < top.size(); i++) {
            Document document = top.get(i);
            String nickname = document.getString("playerName");
            int rating = document.get("rating", Number.class).intValue();
            lines.add(RankingHologramManager.rankLabel(i + 1, C_RANK_ETC)
                    + RankingHologramManager.nicknameColor(i + 1, C_NICKNAME) + nickname
                    + C_SEP + " : "
                    + C_SCORE + rating + "점 "
                    + C_SEP + "(" + tierLabel(rating) + C_SEP + ")");
        }
        return lines;
    }

    @Override
    public Map<UUID, String> buildViewerLines(Map<UUID, String> viewers) {
        List<Document> ranking = lastRanking;
        // playerId(uuid) 우선, 과거 문서에 playerId가 없을 수 있어 닉네임으로도 매칭한다
        Map<String, Integer> indexByUuid = new HashMap<>();
        Map<String, Integer> indexByNickname = new HashMap<>();
        for (int i = 0; i < ranking.size(); i++) {
            String playerId = ranking.get(i).getString("playerId");
            if (playerId != null) {
                indexByUuid.put(playerId, i);
            }
            String playerName = ranking.get(i).getString("playerName");
            if (playerName != null) {
                indexByNickname.putIfAbsent(playerName, i);
            }
        }
        Map<UUID, String> lines = new LinkedHashMap<>();
        for (Map.Entry<UUID, String> viewer : viewers.entrySet()) {
            Integer index = indexByUuid.get(viewer.getKey().toString());
            if (index == null) {
                index = indexByNickname.get(viewer.getValue());
            }
            if (index == null) {
                lines.put(viewer.getKey(), RankingHologramManager.C_VIEWER + "▸ 내 순위" + C_SEP + " : " + C_EMPTY + "기록 없음");
                continue;
            }
            int rating = ranking.get(index).get("rating", Number.class).intValue();
            lines.put(viewer.getKey(), RankingHologramManager.C_VIEWER + "▸ 내 순위" + C_SEP + " : "
                    + RankingHologramManager.rankLabel(index + 1, C_RANK_ETC)
                    + C_SCORE + rating + "점 "
                    + C_SEP + "(" + tierLabel(rating) + C_SEP + ")");
        }
        return lines;
    }

    /** 레이팅 이하의 가장 높은 티어를 티어 색으로 칠해 반환한다. */
    private String tierLabel(int rating) {
        for (int i = 0; i < TIER_MIN_RATINGS.length; i++) {
            if (rating >= TIER_MIN_RATINGS[i]) {
                return TIER_COLORS[i] + TIER_NAMES[i];
            }
        }
        return TIER_COLORS[TIER_COLORS.length - 1] + TIER_NAMES[TIER_NAMES.length - 1];
    }
}
