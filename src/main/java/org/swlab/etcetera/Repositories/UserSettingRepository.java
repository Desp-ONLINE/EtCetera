package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.entity.Player;
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
        Boolean showDamageChat = document.getBoolean("showDamageChat");
        if(showDamageChat == null){
            showDamageChat = false;
        }
        Boolean showSkillCooldownNotice = document.getBoolean("showSkillCooldownNotice");
        if(showSkillCooldownNotice == null){
            showSkillCooldownNotice = true;
        }
        Integer playerTime = document.getInteger("playerTime");
        if(playerTime == null){
            playerTime = -1;
        }

        UserSettingDTO userSettingDTO = UserSettingDTO.builder().uuid(uuid).isVisibleInformation(isVisibleInformation).showDamageChat(showDamageChat).showSkillCooldownNotice(showSkillCooldownNotice).playerTime(playerTime).build();

        userSettingCache.put(uuid, userSettingDTO);

        applyPlayerTime(player);

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
    public void toggleShowDamageChat(Player player) {
        boolean isShowDamageChat = isShowDamageChat(player);
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        userSettingDTO.setShowDamageChat(!isShowDamageChat);
        if (isShowDamageChat) {
            player.sendMessage("§e 이제 데미지가 채팅에 출력되지 않습니다.");
        } else {
            player.sendMessage("§e 이제 데미지가 채팅에 출력됩니다.");
        }
        userSettingCache.put(player.getUniqueId().toString(), userSettingDTO);
    }

    public void toggleShowSkillCooldownNotice(Player player) {
        boolean current = isShowSkillCooldownNotice(player);
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        userSettingDTO.setShowSkillCooldownNotice(!current);
        if (current) {
            player.sendMessage("§e 이제 스킬 쿨타임 종료 알림이 출력되지 않습니다.");
        } else {
            player.sendMessage("§e 이제 스킬 쿨타임 종료 알림이 출력됩니다.");
        }
        userSettingCache.put(player.getUniqueId().toString(), userSettingDTO);
    }

    public void setPlayerTime(Player player, int time, String displayName) {
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        if (userSettingDTO == null) {
            return;
        }
        userSettingDTO.setPlayerTime(time);
        userSettingCache.put(player.getUniqueId().toString(), userSettingDTO);
        applyPlayerTime(player);
        if (time < 0) {
            player.sendMessage("§e 이제 시간이 서버 시간을 따라갑니다.");
        } else {
            player.sendMessage("§e 이제 시간이 §f" + displayName + "§e(으)로 고정됩니다.");
        }
    }

    public void applyPlayerTime(Player player) {
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        if (userSettingDTO == null) {
            return;
        }
        int time = userSettingDTO.getPlayerTime();
        if (time < 0) {
            player.resetPlayerTime();
        } else {
            player.setPlayerTime(time, false);
        }
    }

    public int getPlayerTime(Player player) {
        if (userSettingCache.get(player.getUniqueId().toString()) == null) {
            return -1;
        }
        return userSettingCache.get(player.getUniqueId().toString()).getPlayerTime();
    }

    public boolean isVisibleInformation(Player player) {
        if (userSettingCache.get(player.getUniqueId().toString()) == null) {
            return false;
        }
        return userSettingCache.get(player.getUniqueId().toString()).isVisibleInformation();
    }

    public boolean isShowDamageChat(Player player) {
        if (userSettingCache.get(player.getUniqueId().toString()) == null) {
            return false;
        }
        return userSettingCache.get(player.getUniqueId().toString()).isShowDamageChat();
    }

    public boolean isShowSkillCooldownNotice(Player player) {
        if (userSettingCache.get(player.getUniqueId().toString()) == null) {
            return false;
        }
        return userSettingCache.get(player.getUniqueId().toString()).isShowSkillCooldownNotice();
    }

    public void saveUserSetting(Player player) {
        Document document = userSettingCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        document.append("isVisibleInformation", isVisibleInformation(player));
        document.append("showDamageChat", isShowDamageChat(player));
        document.append("showSkillCooldownNotice", isShowSkillCooldownNotice(player));
        document.append("playerTime", getPlayerTime(player));

        userSettingCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("isVisibleInformation", true)
                .append("showDamageChat", true)
                .append("showSkillCooldownNotice", true)
                .append("playerTime", -1);
        userSettingCollection.insertOne(document);

        return document;
    }
}