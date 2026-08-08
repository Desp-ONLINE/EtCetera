package org.swlab.etcetera.Training.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import org.bson.Document;
import org.bukkit.plugin.Plugin;
import org.swlab.etcetera.Training.objects.TrainingRoom;

import java.util.Map;

public class TrainingRoomRepository extends MongoCachedRepository<Integer, TrainingRoom> {

    public TrainingRoomRepository(Plugin plugin, String database, String collection, Map<Integer, TrainingRoom> cache) {
        super(plugin, database, collection, cache);
    }

    @Override
    public Document toDocument(TrainingRoom trainingRoom) {
        return Document.parse(FileManager.toJson(trainingRoom));
    }

    @Override
    public TrainingRoom toEntity(Document document) {
        return FileManager.toObject(document.toJson(), TrainingRoom.class);
    }

    public void init() {
        cache.clear();
        findAll().forEach(this::putIn);
    }
}
