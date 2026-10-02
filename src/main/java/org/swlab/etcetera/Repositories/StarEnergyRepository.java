package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 별의 기운 (해방 재화). 레이드 보상방 진입 시 레이드별 설정 수치를 파티원 수로 나누어 지급한다.
 * 보유량은 MAX_AMOUNT 를 넘을 수 없고, 초과분은 소멸한다.
 */
public class StarEnergyRepository {

    public static final int MAX_AMOUNT = 1500;
    // 이 날짜 0시부터 레이드 클리어 시 획득 가능
    public static final LocalDate START_DATE = LocalDate.of(2026, 10, 5);

    public static StarEnergyRepository instance;
    public record RaidEnergy(int dungeonId, String name, int amount) {
    }

    // 클리어 시 지급 총량(파티원 분배 전). MMODungeon 던전 ID 기준, 표시 순서 그대로.
    public static final List<RaidEnergy> RAIDS = List.of(
            new RaidEnergy(109, "카날로아", 30),
            new RaidEnergy(110, "레이븐", 50),
            new RaidEnergy(111, "레기온의 군단장들", 80),
            new RaidEnergy(112, "바실리스크", 125),
            new RaidEnergy(113, "레기온", 200),
            new RaidEnergy(114, "익스트림 카날로아", 300),
            new RaidEnergy(115, "크로나크", 400),
            new RaidEnergy(116, "KRM-EX", 600),
            new RaidEnergy(117, "아르카디엘", 850)
    );
    // 접속 시 비동기 로드되는 조회 전용 스냅샷. 누적/차감은 DB 기준으로 즉시 기록한다.
    public ConcurrentHashMap<String, Integer> playerAmountCache = new ConcurrentHashMap<>();

    public StarEnergyRepository() {
        instance = this;
    }

    public static StarEnergyRepository getInstance() {
        if (instance == null) {
            instance = new StarEnergyRepository();
        }
        return instance;
    }

    public static final MongoCollection<Document> starEnergyPlayerCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("StarEnergyPlayer");

    public int getRaidAmount(int dungeonId) {
        for (RaidEnergy raid : RAIDS) {
            if (raid.dungeonId() == dungeonId) {
                return raid.amount();
            }
        }
        return 0;
    }

    public static boolean isEarnable() {
        return !LocalDate.now().isBefore(START_DATE);
    }

    // 파티원 1인당 지급량. 나머지는 버린다.
    public int getShareAmount(int dungeonId, int partySize) {
        if (partySize <= 0) {
            return 0;
        }
        return getRaidAmount(dungeonId) / partySize;
    }

    public void loadUserData(Player player) {
        String uuid = player.getUniqueId().toString();
        Document document = starEnergyPlayerCollection.find(new Document("uuid", uuid)).first();
        int amount = document == null ? 0 : document.getInteger("amount", 0);
        playerAmountCache.put(uuid, amount);
    }

    public void unloadUserData(Player player) {
        playerAmountCache.remove(player.getUniqueId().toString());
    }

    // 로드 전이면 null
    public Integer getCachedAmount(Player player) {
        return playerAmountCache.get(player.getUniqueId().toString());
    }

    public int getAmount(String uuid) {
        Document document = starEnergyPlayerCollection.find(new Document("uuid", uuid)).first();
        return document == null ? 0 : document.getInteger("amount", 0);
    }

    /**
     * DB 기준으로 누적한다. 최대 보유량을 넘는 분량은 소멸한다.
     * @return 실제로 적립된 양
     */
    public synchronized int addAmount(String uuid, String nickname, int amount) {
        if (amount <= 0) {
            return 0;
        }
        int current = getAmount(uuid);
        int newAmount = Math.min(MAX_AMOUNT, current + amount);
        writeToDatabase(uuid, nickname, newAmount);
        return Math.max(0, newAmount - current);
    }

    /**
     * 해방 등에서 사용. 보유량이 부족하면 차감하지 않고 false.
     */
    public synchronized boolean consumeAmount(String uuid, String nickname, int amount) {
        if (amount < 0) {
            return false;
        }
        int current = getAmount(uuid);
        if (current < amount) {
            return false;
        }
        writeToDatabase(uuid, nickname, current - amount);
        return true;
    }

    public synchronized void setAmount(String uuid, String nickname, int amount) {
        writeToDatabase(uuid, nickname, Math.max(0, Math.min(MAX_AMOUNT, amount)));
    }

    // 오프라인 대상 관리자 명령용. 마지막으로 기록된 닉네임 기준.
    public String findUuidByNickname(String nickname) {
        Document document = starEnergyPlayerCollection.find(new Document("nickname", nickname)).first();
        return document == null ? null : document.getString("uuid");
    }

    private void writeToDatabase(String uuid, String nickname, int amount) {
        Document document = new Document()
                .append("uuid", uuid)
                .append("nickname", nickname)
                .append("amount", amount);
        starEnergyPlayerCollection.replaceOne(new Document("uuid", uuid), document, new ReplaceOptions().upsert(true));

        // 이 채널에 접속 중인 경우에만 스냅샷 갱신
        playerAmountCache.computeIfPresent(uuid, (k, v) -> amount);
    }
}
