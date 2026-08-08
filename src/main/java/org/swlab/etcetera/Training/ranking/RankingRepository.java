package org.swlab.etcetera.Training.ranking;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class RankingRepository extends MongoCachedRepository<String, RankingData> {

    public RankingRepository(Plugin plugin, String database, String collection, Map<String, RankingData> cache) {
        super(plugin, database, collection, cache);
    }

    @Override
    public Document toDocument(RankingData data) {
        return Document.parse(FileManager.toJson(data));
    }

    @Override
    public RankingData toEntity(Document document) {
        return FileManager.toObject(document.toJson(), RankingData.class);
    }

    public void init() {
        cache.clear();
        findAll().forEach(this::putIn);
    }
}
