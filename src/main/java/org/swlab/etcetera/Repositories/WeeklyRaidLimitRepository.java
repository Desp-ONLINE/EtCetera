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
import java.util.HashMap;

public class WeeklyRaidLimitRepository {

    public static final int MAX_WEEKLY_CLEAR = 5;

    public static WeeklyRaidLimitRepository instance;
    public HashMap<String, WeeklyRaidClearDTO> weeklyRaidClearCache = new HashMap<>();

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

    public Integer getClearCount(Player player) {
        WeeklyRaidClearDTO dto = getOrLoad(player);
        if (!getCurrentWeekStart().equals(dto.getWeekStart())) {
            dto.setWeekStart(getCurrentWeekStart());
            dto.setClearCount(0);
            saveUserData(player);
        }
        return dto.getClearCount();
    }

    public boolean canEnter(Player player) {
        return getClearCount(player) < MAX_WEEKLY_CLEAR;
    }

    public void resetClearCount(Player player) {
        WeeklyRaidClearDTO dto = getOrLoad(player);
        dto.setWeekStart(getCurrentWeekStart());
        dto.setClearCount(0);
        saveUserData(player);
    }

    public void incrementClearCount(Player player) {
        Integer clearCount = getClearCount(player);
        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        dto.setClearCount(clearCount + 1);
        saveUserData(player);
    }

    private WeeklyRaidClearDTO getOrLoad(Player player) {
        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        if (dto == null) {
            dto = loadUserData(player);
        }
        return dto;
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

    public void saveUserData(Player player) {
        WeeklyRaidClearDTO dto = weeklyRaidClearCache.get(player.getUniqueId().toString());
        if (dto == null) {
            return;
        }
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("clearCount", dto.getClearCount())
                .append("weekStart", dto.getWeekStart());

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
