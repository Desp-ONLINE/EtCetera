package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.HiddenExchangeDTO;
import org.swlab.etcetera.Util.HiddenMaterialConfig;

import java.util.HashMap;

/**
 * 히든 재료 교환의 주간 사용 횟수를 관리하는 저장소.
 * 월요일 자정 초기화는 {@code WeeklyResetEvent} 에서 {@link #resetDatas()} 로 처리한다.
 */
public class HiddenExchangeRepository {

    private static HiddenExchangeRepository instance;

    private final HashMap<String, HiddenExchangeDTO> cache = new HashMap<>();

    public HiddenExchangeRepository() {
        instance = this;
    }

    public static HiddenExchangeRepository getInstance() {
        if (instance == null) {
            instance = new HiddenExchangeRepository();
        }
        return instance;
    }

    public static final MongoCollection<Document> collection =
            DatabaseRegister.getInstance().getMongoDatabase().getCollection("HiddenExchange");

    public void loadUserData(Player player) {
        String uuid = player.getUniqueId().toString();
        Document document = collection.find(new Document("uuid", uuid)).first();
        if (document == null) {
            document = insertDefaultDocument(player);
        }
        HiddenExchangeDTO dto = HiddenExchangeDTO.builder()
                .uuid(document.getString("uuid"))
                .nickname(document.getString("nickname"))
                .usedCount(document.getInteger("usedCount", 0))
                .build();
        cache.put(uuid, dto);
    }

    public void saveUserData(Player player) {
        String uuid = player.getUniqueId().toString();
        HiddenExchangeDTO dto = cache.get(uuid);
        if (dto == null) {
            return;
        }
        Document document = new Document()
                .append("uuid", uuid)
                .append("nickname", player.getName())
                .append("usedCount", dto.getUsedCount());
        collection.replaceOne(new Document("uuid", uuid), document, new ReplaceOptions().upsert(true));
    }

    private Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("usedCount", 0);
        collection.insertOne(document);
        return document;
    }

    public int getUsedCount(Player player) {
        HiddenExchangeDTO dto = cache.get(player.getUniqueId().toString());
        return dto == null ? 0 : dto.getUsedCount();
    }

    public int getRemaining(Player player) {
        return Math.max(0, HiddenMaterialConfig.WEEKLY_LIMIT - getUsedCount(player));
    }

    public boolean canExchange(Player player) {
        return getRemaining(player) > 0;
    }

    public void increaseUsedCount(Player player) {
        String uuid = player.getUniqueId().toString();
        HiddenExchangeDTO dto = cache.get(uuid);
        if (dto == null) {
            loadUserData(player);
            dto = cache.get(uuid);
        }
        dto.setUsedCount(dto.getUsedCount() + 1);
        saveUserData(player);
    }

    /**
     * 월요일 자정 주간 초기화. 모든 유저의 사용 횟수를 0 으로 되돌린다.
     */
    public void resetDatas() {
        collection.deleteMany(new Document());
        cache.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadUserData(player);
        }
    }
}
