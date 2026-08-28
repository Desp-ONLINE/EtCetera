package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.ArrayList;
import java.util.List;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 바벨탑 랭킹 컨텐츠.
 *
 * <p>BabelTower 플러그인의 MongoDB(BabelTower.PlayerData)를 직접 읽는다.
 * 플러그인 간 클래스 의존이 없어 EtCetera 단독 리로드에도 안전하다.
 * 최고 클리어 층 내림차순, 동률이면 먼저 클리어한 순으로 상위 10명을 표시한다.
 */
public class BabelTowerRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /* ===== 색상 팔레트 (hex) ===== */
    /* 검은색(차콜·은회색) 테마 — 순수 검정은 홀로그램 배경에 묻혀서 차콜/은회색으로 명암을 줬다 */
    private static final String C_STAR = hex("#55556A");      // 헤더 장식 별 (차콜)
    private static final String C_TITLE = hex("#D8D8E0");     // 헤더 타이틀 (은백)
    private static final String C_RANK_ETC = hex("#C9CFE0");  // 4~5위 (밝은 은회색, 6위 이하 공통 회색과 구분)
    private static final String C_NICKNAME = hex("#E6E6EE");  // 4위 이하 닉네임 (연한 은회색)
    private static final String C_SEP = hex("#3C3C46");       // 구분 기호 (거의 검정)
    private static final String C_FLOOR = hex("#B0B0C8");     // 층수 (은회색)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    private final MongoCollection<Document> collection;

    public BabelTowerRankingProvider() {
        this.collection = DatabaseRegister.getInstance().getMongoClient()
                .getDatabase("BabelTower").getCollection("PlayerData");
    }

    @Override
    public String getContentKey() {
        return "바벨탑";
    }

    @Override
    public org.bukkit.Color getParticleColor() {
        // 검은 파티클은 안 보여서 테마의 은백색을 사용
        return org.bukkit.Color.fromRGB(0xD8D8E0);
    }

    @Override
    public List<String> buildLines() {
        // 경고 누적(5회 이상) 유저 제외. 바벨탑 문서의 user_id는 닉네임이다
        WarnedPlayerFilter.Excluded warned = WarnedPlayerFilter.load();
        List<Document> top = collection.find(
                        new Document("user_id", new Document("$nin", new ArrayList<>(warned.nicknames()))))
                .sort(Sorts.orderBy(Sorts.descending("clearFloor"), Sorts.ascending("latestClearedDate")))
                .limit(RANK_SIZE)
                .into(new ArrayList<>());

        List<String> lines = new ArrayList<>();
        lines.add(C_STAR + "✦ " + C_TITLE + "바벨탑 랭킹 TOP " + RANK_SIZE + C_STAR + " ✦");
        if (top.isEmpty()) {
            lines.add(C_EMPTY + "집계된 데이터가 없습니다.");
        }
        for (int i = 0; i < top.size(); i++) {
            Document document = top.get(i);
            String nickname = document.getString("user_id");
            int clearFloor = document.get("clearFloor", Number.class).intValue();
            lines.add(RankingHologramManager.rankLabel(i + 1, C_RANK_ETC)
                    + RankingHologramManager.nicknameColor(i + 1, C_NICKNAME) + nickname
                    + C_SEP + " : "
                    + C_FLOOR + clearFloor + "층");
        }
        return lines;
    }
}
