package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.WeeklyRaidClearDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.ConcurrentHashMap;

public class WeeklyRaidLimitRepository {

    public static final int MAX_WEEKLY_CLEAR = 5;

    public static WeeklyRaidLimitRepository instance;
    // 접속 5초 후 비동기 로드되는 조회 전용 스냅샷. 누적/초기화는 캐싱 없이 DB에 즉시 기록한다.
    public ConcurrentHashMap<String, WeeklyRaidClearDTO> weeklyRaidClearCache = new ConcurrentHashMap<>();

    public WeeklyRaidLimitRepository() {
        instance = this;
    }

    public static WeeklyRaidLimitRepository getInstance() {
        if (instance == null) {
            instance = new WeeklyRaidLimitRepository();
        }
        return instance;
    }

    public static final MongoCollection<Document> weeklyRaidClearCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("WeeklyRaidClearPlayer");

    // 이번 주 월요일 날짜를 주차 키로 사용. 저장된 키와 다르면 주가 바뀐 것이므로 카운트를 초기화한다.
    // 별도 스케줄러 없이 월요일 자정 초기화가 보장되고, 서버 재시작/멀티 채널에서도 안전하다.
    public String getCurrentWeekStart() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
    }

    public boolean isLoaded(Player player) {
        return weeklyRaidClearCache.containsKey(player.getUniqueId().toString());
    }

    // 로드 전이면 null 반환. 호출부에서 isLoaded 로 먼저 확인해야 한다.
    public Integer getClearCount(Player player) {
        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        if (dto == null) {
            return null;
        }
        String currentWeekStart = getCurrentWeekStart();
        if (!currentWeekStart.equals(dto.getWeekStart())) {
            dto.setWeekStart(currentWeekStart);
            dto.setClearCount(0);
            writeToDatabase(player, 0, currentWeekStart);
        }
        return dto.getClearCount();
    }

    public boolean canEnter(Player player) {
        Integer clearCount = getClearCount(player);
        return clearCount != null && clearCount < MAX_WEEKLY_CLEAR;
    }

    public void resetClearCount(Player player) {
        String currentWeekStart = getCurrentWeekStart();
        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        if (dto != null) {
            dto.setWeekStart(currentWeekStart);
            dto.setClearCount(0);
        }
        writeToDatabase(player, 0, currentWeekStart);
    }

    // DB 기준으로 누적하여 즉시 기록. 스냅샷은 표시/입장 체크용으로만 갱신한다.
    public int incrementClearCount(Player player) {
        String currentWeekStart = getCurrentWeekStart();
        Document document = weeklyRaidClearCollection.find(new Document("uuid", player.getUniqueId().toString())).first();

        int clearCount = 0;
        if (document != null && currentWeekStart.equals(document.getString("weekStart"))) {
            clearCount = document.getInteger("clearCount", 0);
        }
        int newClearCount = clearCount + 1;
        writeToDatabase(player, newClearCount, currentWeekStart);

        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        if (dto != null) {
            dto.setWeekStart(currentWeekStart);
            dto.setClearCount(newClearCount);
        }
        return newClearCount;
    }

    public WeeklyRaidClearDTO loadUserData(Player player) {
        Document document = weeklyRaidClearCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = insertDefaultDocument(player);
        }

        WeeklyRaidClearDTO dto = WeeklyRaidClearDTO.builder()
                .uuid(document.getString("uuid"))
                .nickname(document.getString("nickname"))
                .clearCount(document.getInteger("clearCount", 0))
                .weekStart(document.getString("weekStart"))
                .build();

        weeklyRaidClearCache.put(dto.getUuid(), dto);
        return dto;
    }

    public void unloadUserData(Player player) {
        weeklyRaidClearCache.remove(player.getUniqueId().toString());
    }

    private void writeToDatabase(Player player, int clearCount, String weekStart) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("clearCount", clearCount)
                .append("weekStart", weekStart);

        weeklyRaidClearCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("clearCount", 0)
                .append("weekStart", getCurrentWeekStart());
        weeklyRaidClearCollection.insertOne(document);

        return document;
    }
}
