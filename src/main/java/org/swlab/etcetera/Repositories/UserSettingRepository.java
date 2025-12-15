package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.UserSettingDTO;

import java.util.HashMap;

public class UserSettingRepository {

    public static UserSettingRepository instance;
    public HashMap<String, UserSettingDTO> userSettingCache = new HashMap<>();

    public UserSettingRepository() {
        instance = this;
    }

    public static UserSettingRepository getInstance() {


        if (instance == null) {
            instance = new UserSettingRepository();

        }
        return instance;
    }


    public static final MongoCollection<Document> userSettingCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("UserSetting");

    public void loadUserSetting(Player player) {

        Document document = userSettingCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = insertDefaultDocument(player);
        }

        String uuid = document.getString("uuid");
        Boolean isVisibleInformation = document.getBoolean("isVisibleInformation");

        UserSettingDTO userSettingDTO = UserSettingDTO.builder().uuid(uuid).isVisibleInformation(isVisibleInformation).build();

        userSettingCache.put(uuid, userSettingDTO);

    }

    public void toggleVisibleInformation(Player player) {
        boolean visibleInformation = isVisibleInformation(player);
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        userSettingDTO.setVisibleInformation(!visibleInformation);
        if (visibleInformation) {
            player.sendMessage("§e 이제 더 이상 다른 사람이 전체 채팅에서 내 정보를 열람할 수 없습니다.");
        } else {
            player.sendMessage("§e 이제 다른 사람이 전체 채팅에서 내 정보를 열람할 수 있습니다.");
        }
        userSettingCache.put(player.getUniqueId().toString(), userSettingDTO);
    }

    public boolean isVisibleInformation(Player player) {
        return userSettingCache.get(player.getUniqueId().toString()).isVisibleInformation();
    }

    public void saveUserSetting(Player player) {
        Document document = userSettingCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        document.append("isVisibleInformation", isVisibleInformation(player));

        userSettingCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("isVisibleInformation", true);
        userSettingCollection.insertOne(document);

        return document;
    }
}