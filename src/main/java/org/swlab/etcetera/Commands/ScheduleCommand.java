package org.swlab.etcetera.Commands;

import com.binggre.mmodungeon.api.MMODungeonAPI;
import com.binggre.mmodungeon.objects.PlayerClearLog;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScheduleCommand implements CommandExecutor {

    private static final List<Integer> JIN_REGION_DUNGEON_IDS = List.of(13, 14, 15);
    // 연습모드 레이드(ID 1000 이상)는 스케줄 안내에서 제외
    private static final int PRACTICE_RAID_MIN_DUNGEON_ID = 1000;
    private static final int FIELD_BOSS_DAILY_MAX = 5;
    private static final double FARMING_MAX_FATIGUE = 100.0;

    // 마을별 일일퀘스트 해금 레벨 (해금된 마을만 안내)
    private static final Map<String, Integer> QUEST_VILLAGE_LEVELS = new LinkedHashMap<>();

    static {
        QUEST_VILLAGE_LEVELS.put("엘븐하임", 20);
        QUEST_VILLAGE_LEVELS.put("칼리마", 45);
        QUEST_VILLAGE_LEVELS.put("인페리움", 70);
        QUEST_VILLAGE_LEVELS.put("아르크티카", 100);
        QUEST_VILLAGE_LEVELS.put("엡실론", 130);
    }

    // binggre 계열 플러그인(MongoLibraryPlugin)의 LocalDateTime 직렬화 포맷
    private static final DateTimeFormatter BINGGRE_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        int level = new MMOCoreAPI(EtCetera.getInstance()).getPlayerData(player).getLevel();
        UUID uuid = player.getUniqueId();
        String nickname = player.getName();

        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
            List<String> todos = new ArrayList<>();

            collectSafely(todos, () -> collectDailyQuests(uuid, level));
            collectSafely(todos, () -> collectJumpMap(nickname));
            collectSafely(todos, () -> collectDailyRaids(uuid));
            collectSafely(todos, () -> collectApocalypse(uuid));
            collectSafely(todos, () -> collectFieldBoss(uuid));
            collectSafely(todos, () -> collectFarming(uuid));

            if (!player.isOnline()) {
                return;
            }
            player.sendMessage("");
            player.sendMessage("§6§l   [ 오늘의 스케줄 ]");
            if (todos.isEmpty()) {
                player.sendMessage("§a   오늘의 일일 컨텐츠를 모두 완료했습니다!");
            } else {
                player.sendMessage("§7   아직 완료하지 않은 일일 컨텐츠 목록입니다.");
                player.sendMessage("");
                for (String todo : todos) {
                    player.sendMessage("§e   ● §f" + todo);
                }
            }
            player.sendMessage("");
        });
        return true;
    }

    private interface TodoCollector {
        List<String> collect();
    }

    // 항목 하나의 조회 실패가 전체 안내를 막지 않도록 개별로 감싼다
    private void collectSafely(List<String> todos, TodoCollector collector) {
        try {
            todos.addAll(collector.collect());
        } catch (Exception e) {
            EtCetera.getInstance().getLogger().warning("스케줄 조회 실패: " + e);
        }
    }

    private MongoClient mongoClient() {
        return DatabaseRegister.getInstance().getMongoClient();
    }

    // 일일퀘스트: 마을 보상 수령(villageRewardReceived) 기준, 해금된 마을만 안내
    private List<String> collectDailyQuests(UUID uuid, int level) {
        List<String> result = new ArrayList<>();
        Document doc = mongoClient().getDatabase("DQuest").getCollection("PlayerData")
                .find(Filters.eq("uuid", uuid.toString())).first();

        for (Map.Entry<String, Integer> village : QUEST_VILLAGE_LEVELS.entrySet()) {
            if (level < village.getValue()) {
                continue;
            }
            if (!isVillageQuestDone(doc, village.getKey())) {
                result.add("일일퀘스트 - " + village.getKey());
            }
        }
        return result;
    }

    private boolean isVillageQuestDone(Document doc, String village) {
        if (doc == null) {
            return false;
        }
        List<Document> activeQuests = doc.getList("active_quests", Document.class);
        if (activeQuests == null) {
            return false;
        }
        for (Document activeQuest : activeQuests) {
            if (village.equals(activeQuest.getString("village"))
                    && Boolean.TRUE.equals(activeQuest.getBoolean("villageRewardReceived"))) {
                return true;
            }
        }
        return false;
    }

    // 점프맵: JumpmapLog 의 players 목록(닉네임)에 있으면 오늘 수령 완료
    private List<String> collectJumpMap(String nickname) {
        Document first = DatabaseRegister.getInstance().getMongoDatabase()
                .getCollection("JumpmapLog").find().first();
        if (first != null) {
            List<String> players = first.getList("players", String.class);
            if (players != null && players.contains(nickname)) {
                return List.of();
            }
        }
        return List.of("점프맵 일일 보상 (20점 이상 클리어)");
    }

    // 일일 레이드(replayDay == 1): 진 레기온의 군단장(13/14/15)은 하나로 묶어서 안내
    private List<String> collectDailyRaids(UUID uuid) {
        List<String> result = new ArrayList<>();
        if (Bukkit.getPluginManager().getPlugin("MMODungeon") == null) {
            return result;
        }
        PlayerDungeon playerDungeon = MMODungeonAPI.getPlayerRepository().get(uuid);
        if (playerDungeon == null) {
            return result;
        }

        boolean jinRegionDone = false;
        for (Integer dungeonId : JIN_REGION_DUNGEON_IDS) {
            if (!playerDungeon.getClearLog(dungeonId).isJoinableDate()) {
                jinRegionDone = true;
                break;
            }
        }
        if (!jinRegionDone) {
            result.add("진 레기온의 군단장 레이드");
        }

        MMODungeonAPI.getDungeonRepository().values().stream()
                .filter(Dungeon::isEnable)
                .filter(dungeon -> dungeon.getReplayDay() == 1)
                .filter(dungeon -> dungeon.getId() < PRACTICE_RAID_MIN_DUNGEON_ID)
                .filter(dungeon -> !JIN_REGION_DUNGEON_IDS.contains(dungeon.getId()))
                .sorted(Comparator.comparing(Dungeon::getId))
                .forEach(dungeon -> {
                    PlayerClearLog clearLog = playerDungeon.getClearLog(dungeon.getId());
                    int maxJoin = Math.max(1, dungeon.getMaxJoin());
                    int used = clearLog.isJoinableDate() ? 0 : clearLog.getCount();
                    if (used < maxJoin) {
                        String suffix = maxJoin > 1 ? " (" + used + "/" + maxJoin + ")" : "";
                        result.add(dungeon.getName() + suffix);
                    }
                });
        return result;
    }

    // 아포칼립스: 오늘 입장권을 아직 획득하지 않았으면 안내 (lastDropTime 다음 자정 전이면 획득 완료)
    private List<String> collectApocalypse(UUID uuid) {
        Document doc = mongoClient().getDatabase("MMO-WaveDungeon").getCollection("Player")
                .find(Filters.eq("id", uuid.toString())).first();
        if (doc != null) {
            String lastDropTime = doc.getString("lastDropTime");
            if (lastDropTime != null) {
                LocalDateTime dropTime = LocalDateTime.parse(lastDropTime, BINGGRE_DATE_TIME_FORMAT);
                boolean receivedToday = LocalDateTime.now()
                        .isBefore(dropTime.toLocalDate().plusDays(1).atStartOfDay());
                if (receivedToday) {
                    return List.of();
                }
            }
        }
        return List.of("아포칼립스 (입장권 미획득)");
    }

    // 필드보스: 전체 보스 합산 오늘 처치 수, 하루 최대 5회까지만 카운트
    private List<String> collectFieldBoss(UUID uuid) {
        Document doc = mongoClient().getDatabase("MMO-FieldBoss").getCollection("Player")
                .find(Filters.eq("id", uuid.toString())).first();
        int todayKills = 0;
        if (doc != null) {
            Object joinBossObject = doc.get("joinBoss");
            if (joinBossObject instanceof Document joinBoss) {
                String today = LocalDate.now().toString();
                for (String bossId : joinBoss.keySet()) {
                    if (!(joinBoss.get(bossId) instanceof Document bossLog)) {
                        continue;
                    }
                    if (today.equals(bossLog.getString("killCountDate"))
                            && bossLog.get("todayKillCount") instanceof Number killCount) {
                        todayKills += killCount.intValue();
                    }
                }
            }
        }
        int counted = Math.min(todayKills, FIELD_BOSS_DAILY_MAX);
        if (counted >= FIELD_BOSS_DAILY_MAX) {
            return List.of();
        }
        return List.of("필드보스 토벌 (" + counted + "/" + FIELD_BOSS_DAILY_MAX + ")");
    }

    // 채집: 피로도 100 도달 전이면 안내
    private List<String> collectFarming(UUID uuid) {
        Document doc = mongoClient().getDatabase("Farming").getCollection("PlayerData")
                .find(Filters.eq("uuid", uuid.toString())).first();
        double fatigue = 0.0;
        if (doc != null && doc.get("fatigue") instanceof Number number) {
            fatigue = number.doubleValue();
        }
        if (fatigue >= FARMING_MAX_FATIGUE) {
            return List.of();
        }
        return List.of("채집 (피로도 " + (int) fatigue + "/" + (int) FARMING_MAX_FATIGUE + ")");
    }
}
