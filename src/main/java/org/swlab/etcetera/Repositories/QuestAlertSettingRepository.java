package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestAlertSettingRepository {

    public static QuestAlertSettingRepository instance;

    // 기존 QuestAnnouncer 플러그인의 설정 데이터를 그대로 사용하기 위해 동일 DB/컬렉션 유지
    private final MongoCollection<Document> collection;
    private final Map<UUID, Boolean> cache = new HashMap<>();

    public QuestAlertSettingRepository() {
        collection = DatabaseRegister.getInstance().getMongoClient().getDatabase("QuestAnnouncer").getCollection("player_settings");
        instance = this;
    }

    public static QuestAlertSettingRepository getInstance() {
        if (instance == null) {
            instance = new QuestAlertSettingRepository();
        }
        return instance;
    }

    public boolean isEnabled(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadFromDB);
    }

    public void toggle(UUID uuid) {
        boolean newValue = !isEnabled(uuid);
        cache.put(uuid, newValue);
        saveToDB(uuid, newValue);
    }

    public void remove(UUID uuid) {
        cache.remove(uuid);
    }

    private boolean loadFromDB(UUID uuid) {
        Document doc = collection.find(Filters.eq("_id", uuid.toString())).first();
        return doc == null || doc.getBoolean("enabled", true);
    }

    private void saveToDB(UUID uuid, boolean enabled) {
        Document doc = new Document("_id", uuid.toString()).append("enabled", enabled);
        collection.replaceOne(Filters.eq("_id", uuid.toString()), doc, new ReplaceOptions().upsert(true));
    }
}
