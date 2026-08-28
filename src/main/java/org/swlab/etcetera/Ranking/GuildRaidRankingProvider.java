package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Util.CombatPowerUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 길드레이드 랭킹 컨텐츠.
 *
 * <p>MMO-GuildRaid 플러그인의 MongoDB(MMO-GuildRaid.Dungeons / JoinHistory)를 직접 읽는다.
 * 플러그인 간 클래스 의존이 없어 EtCetera 단독 리로드에도 안전하다.
 * 레이드별로 길드 누적 데미지 내림차순 상위 10개 길드를 표시한다.
 */
public class GuildRaidRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /* ===== 색상 팔레트 (hex) ===== */
    /* 노란색 테마 */
    private static final String C_STAR = hex("#FFB300");      // 헤더 장식 별 (호박색)
    private static final String C_TITLE = hex("#FFD84D");     // 헤더 타이틀 (선명한 노랑)
    private static final String C_RAID = hex("#FFE082");      // 레이드 이름 (연노랑)
    private static final String C_RANK_1 = hex("#FFD700");    // 1위 금색
    private static final String C_RANK_2 = hex("#C7D6E8");    // 2위 은색
    private static final String C_RANK_3 = hex("#E8883A");    // 3위 동색
    private static final String C_RANK_ETC = hex("#D9B84A");  // 4위 이하 (황토빛)
    private static final String C_GUILD = hex("#FFFFFF");     // 길드 이름
    private static final String C_SEP = hex("#5A5A6E");       // 구분 기호
    private static final String C_DAMAGE = hex("#FFF176");    // 데미지 수치 (밝은 노랑)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    private record GuildDamage(String guildName, double damage) {
    }

    private final MongoDatabase database;

    public GuildRaidRankingProvider() {
        this.database = DatabaseRegister.getInstance().getMongoClient().getDatabase("MMO-GuildRaid");
    }

    @Override
    public String getContentKey() {
        return "길드레이드";
    }

    @Override
    public org.bukkit.Color getParticleColor() {
        return org.bukkit.Color.fromRGB(0xFFD84D);
    }

    @Override
    public List<String> buildLines() {
        // 레이드 정의 (id, name)
        List<Document> raids = database.getCollection("Dungeons").find().into(new ArrayList<>());
        raids.sort(Comparator.comparingInt(d -> d.get("id", Number.class).intValue()));

        // 길드별 참여 기록: { id, name, raidInfo: { "<raidId>": { damage, ... } } }
        List<Document> histories = database.getCollection("JoinHistory").find().into(new ArrayList<>());

        // 레이드별 랭킹 섹션을 먼저 만들고, 데이터가 있는 레이드가 하나뿐이면
        // 다른 컨텐츠와 똑같은 모양이 되도록 레이드 이름 줄을 생략한다
        List<List<String>> sections = new ArrayList<>();
        for (Document raid : raids) {
            int raidId = raid.get("id", Number.class).intValue();
            String raidName = raid.getString("name");

            List<GuildDamage> ranking = new ArrayList<>();
            for (Document history : histories) {
                Document raidInfoMap = history.get("raidInfo", Document.class);
                if (raidInfoMap == null) {
                    continue;
                }
                Document raidInfo = raidInfoMap.get(String.valueOf(raidId), Document.class);
                if (raidInfo == null) {
                    continue;
                }
                Number damage = raidInfo.get("damage", Number.class);
                if (damage == null || damage.doubleValue() <= 0) {
                    continue;
                }
                String guildName = history.getString("name");
                ranking.add(new GuildDamage(guildName == null ? "알 수 없는 길드" : guildName, damage.doubleValue()));
            }
            if (ranking.isEmpty()) {
                continue;
            }
            ranking.sort(Comparator.comparingDouble(GuildDamage::damage).reversed());

            List<String> section = new ArrayList<>();
            section.add(C_RAID + "⚔ " + raidName);
            for (int i = 0; i < Math.min(RANK_SIZE, ranking.size()); i++) {
                GuildDamage entry = ranking.get(i);
                section.add(rankColor(i + 1) + (i + 1) + "위 "
                        + C_GUILD + entry.guildName()
                        + C_SEP + " : "
                        + C_DAMAGE + CombatPowerUtil.toKoreanUnit(Math.round(entry.damage())));
            }
            sections.add(section);
        }

        List<String> lines = new ArrayList<>();
        lines.add(C_STAR + "✦ " + C_TITLE + "길드레이드 랭킹 TOP " + RANK_SIZE + C_STAR + " ✦");
        if (sections.isEmpty()) {
            lines.add(C_EMPTY + "집계된 데이터가 없습니다.");
        } else if (sections.size() == 1) {
            lines.addAll(sections.get(0).subList(1, sections.get(0).size()));
        } else {
            for (List<String> section : sections) {
                lines.add("");
                lines.addAll(section);
            }
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
