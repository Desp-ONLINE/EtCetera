package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.TutorialDTO;

import java.util.HashMap;

public class TutorialRepository {

    public static TutorialRepository instance;
    public HashMap<String, TutorialDTO> tutorialCache = new HashMap<>();

    public static final MongoCollection<Document> tutorialCollection =
            DatabaseRegister.getInstance().getMongoDatabase().getCollection("Tutorial");

    public TutorialRepository() {
        instance = this;
    }

    public static TutorialRepository getInstance() {
        if (instance == null) {
            instance = new TutorialRepository();
        }
        return instance;
    }

    public void loadTutorialData(Player player) {
        Document document = tutorialCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = insertDefaultDocument(player);
        }

        String uuid = document.getString("uuid");
        Boolean tutorialCompleted = document.getBoolean("tutorialCompleted");
        if (tutorialCompleted == null) {
            tutorialCompleted = false;
        }

        TutorialDTO tutorialDTO = TutorialDTO.builder()
                .uuid(uuid)
                .tutorialCompleted(tutorialCompleted)
                .build();

        tutorialCache.put(uuid, tutorialDTO);
    }

    public boolean isTutorialCompleted(Player player) {
        TutorialDTO dto = tutorialCache.get(player.getUniqueId().toString());
        if (dto == null) {
            return false;
        }
        return dto.isTutorialCompleted();
    }

    public void completeTutorial(Player player) {
        String uuid = player.getUniqueId().toString();
        TutorialDTO dto = tutorialCache.get(uuid);

        if (dto == null) {
            dto = TutorialDTO.builder()
                    .uuid(uuid)
                    .tutorialCompleted(true)
                    .build();
        } else {
            dto.setTutorialCompleted(true);
        }

        tutorialCache.put(uuid, dto);
        saveTutorialData(player);
    }

    public void saveTutorialData(Player player) {
        String uuid = player.getUniqueId().toString();

        Document document = tutorialCollection.find(new Document("uuid", uuid)).first();
        if (document == null) {
            document = new Document().append("uuid", uuid);
        }
        document.append("tutorialCompleted", isTutorialCompleted(player));

        tutorialCollection.replaceOne(
                new Document("uuid", uuid),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("tutorialCompleted", false);
        tutorialCollection.insertOne(document);
        return document;
    }
}
