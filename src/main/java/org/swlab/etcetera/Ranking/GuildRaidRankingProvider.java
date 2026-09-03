package org.swlab.etcetera.Ranking;

import com.binggre.mmoguild.MMOGuild;
import com.binggre.mmoguild.objects.PlayerGuild;
import com.binggre.mmoguild.repository.PlayerGuildRepository;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Util.CombatPowerUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.swlab.etcetera.Ranking.RankingHologramManager.hex;

/**
 * 길드레이드 랭킹 컨텐츠.
 *
 * <p>MMO-GuildRaid 플러그인의 MongoDB(MMO-GuildRaid.Dungeons / JoinHistory)를 직접 읽는다.
 * MMO-GuildRaid 클래스 의존이 없어 EtCetera 단독 리로드에도 안전하다.
 * 레이드별로 길드 누적 데미지 내림차순 상위 10개 길드를 표시한다.
 * 개인 순위 줄에는 보는 유저가 속한 길드의 순위를 표시하며,
 * 길드 소속 조회는 필수 의존 플러그인인 MMOGuild API를 사용한다.
 */
public class GuildRaidRankingProvider implements RankingProvider {

    private static final int RANK_SIZE = 10;

    /* ===== 색상 팔레트 (hex) ===== */
    /* 노란색 테마 */
    private static final String C_STAR = hex("#FFB300");      // 헤더 장식 별 (호박색)
    private static final String C_TITLE = hex("#FFD84D");     // 헤더 타이틀 (선명한 노랑)
    private static final String C_RAID = hex("#FFE082");      // 레이드 이름 (연노랑)
    private static final String C_RANK_ETC = hex("#D9B84A");  // 4위 이하 (황토빛)
    private static final String C_GUILD = hex("#FFF3C2");     // 4위 이하 길드 이름 (연한 노랑)
    private static final String C_SEP = hex("#5A5A6E");       // 구분 기호
    private static final String C_DAMAGE = hex("#FFF176");    // 데미지 수치 (밝은 노랑)
    private static final String C_EMPTY = hex("#8A8A9A");     // 데이터 없음 안내

    private record GuildDamage(long guildId, String guildName, double damage) {
    }

    private record RaidRanking(String raidName, List<GuildDamage> ranking) {
    }

    private final MongoDatabase database;

    /** 마지막 집계의 레이드별 전체 랭킹. 개인 순위 줄 계산에 사용 */
    private volatile List<RaidRanking> lastRankings = List.of();
    /** 접속자 uuid → 소속 길드 id. 메인 스레드(collectSync/collectViewerSync)에서 채운다 */
    private final Map<UUID, Long> guildIdByViewer = new ConcurrentHashMap<>();

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
    public void collectSync() {
        guildIdByViewer.clear();
        PlayerGuildRepository repository = MMOGuild.getPlugin().getPlayerRepository();
        for (Player player : Bukkit.getOnlinePlayers()) {
            collectGuildId(repository, player);
        }
    }

    @Override
    public void collectViewerSync(Player player) {
        collectGuildId(MMOGuild.getPlugin().getPlayerRepository(), player);
    }

    private void collectGuildId(PlayerGuildRepository repository, Player player) {
        try {
            PlayerGuild playerGuild = repository.get(player.getUniqueId());
            if (playerGuild != null && playerGuild.getGuildId() != null) {
                guildIdByViewer.put(player.getUniqueId(), playerGuild.getGuildId());
            }
        } catch (Exception e) {
            // 길드 데이터 미로드 등은 이번 사이클에서 제외
        }
    }

    @Override
    public List<String> buildLines() {
        // 레이드 정의 (id, name)
        List<Document> raids = database.getCollection("Dungeons").find().into(new ArrayList<>());
        raids.sort(Comparator.comparingInt(d -> d.get("id", Number.class).intValue()));

        // 길드별 참여 기록: { id, name, raidInfo: { "<raidId>": { damage, ... } } }
        List<Document> histories = database.getCollection("JoinHistory").find().into(new ArrayList<>());

        List<RaidRanking> raidRankings = new ArrayList<>();
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
                Number guildId = history.get("id", Number.class);
                String guildName = history.getString("name");
                ranking.add(new GuildDamage(guildId == null ? -1 : guildId.longValue(),
                        guildName == null ? "알 수 없는 길드" : guildName, damage.doubleValue()));
            }
            if (ranking.isEmpty()) {
                continue;
            }
            ranking.sort(Comparator.comparingDouble(GuildDamage::damage).reversed());
            raidRankings.add(new RaidRanking(raidName, ranking));
        }
        lastRankings = raidRankings;

        // 레이드별 랭킹 섹션을 먼저 만들고, 데이터가 있는 레이드가 하나뿐이면
        // 다른 컨텐츠와 똑같은 모양이 되도록 레이드 이름 줄을 생략한다
        List<List<String>> sections = new ArrayList<>();
        for (RaidRanking raidRanking : raidRankings) {
            List<GuildDamage> ranking = raidRanking.ranking();
            List<String> section = new ArrayList<>();
            section.add(C_RAID + "⚔ " + raidRanking.raidName());
            for (int i = 0; i < Math.min(RANK_SIZE, ranking.size()); i++) {
                GuildDamage entry = ranking.get(i);
                section.add(RankingHologramManager.rankLabel(i + 1, C_RANK_ETC)
                        + RankingHologramManager.nicknameColor(i + 1, C_GUILD) + entry.guildName()
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

    @Override
    public Map<UUID, String> buildViewerLines(Map<UUID, String> viewers) {
        List<RaidRanking> rankings = lastRankings;
        Map<UUID, String> lines = new LinkedHashMap<>();
        for (UUID uuid : viewers.keySet()) {
            String prefix = RankingHologramManager.C_VIEWER + "▸ 내 길드" + C_SEP + " : ";
            Long guildId = guildIdByViewer.get(uuid);
            if (guildId == null) {
                lines.put(uuid, prefix + C_EMPTY + "길드 없음");
                continue;
            }
            // 레이드별로 내 길드의 순위를 찾는다. 레이드가 여럿이면 레이드 이름을 앞에 붙여 한 줄로 잇는다
            List<String> parts = new ArrayList<>();
            for (RaidRanking raidRanking : rankings) {
                List<GuildDamage> ranking = raidRanking.ranking();
                for (int i = 0; i < ranking.size(); i++) {
                    if (ranking.get(i).guildId() != guildId) {
                        continue;
                    }
                    String part = RankingHologramManager.rankLabel(i + 1, C_RANK_ETC)
                            + C_GUILD + ranking.get(i).guildName() + " "
                            + C_DAMAGE + CombatPowerUtil.toKoreanUnit(Math.round(ranking.get(i).damage()));
                    parts.add(rankings.size() > 1 ? C_RAID + raidRanking.raidName() + " " + part : part);
                    break;
                }
            }
            if (parts.isEmpty()) {
                lines.put(uuid, prefix + C_EMPTY + "기록 없음");
            } else {
                lines.put(uuid, prefix + String.join(C_SEP + " · ", parts));
            }
        }
        return lines;
    }
}
