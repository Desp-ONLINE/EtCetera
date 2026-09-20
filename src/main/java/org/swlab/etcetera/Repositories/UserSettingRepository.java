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


    /** /쿨타임감소 기본 대상 슬롯 (핫바 2번). */
    public static final int DEFAULT_COOLDOWN_REDUCE_SLOT = 2;

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
        Boolean showSkillCooldownItem = document.getBoolean("showSkillCooldownItem");
        if(showSkillCooldownItem == null){
            showSkillCooldownItem = true;
        }
        Integer playerTime = document.getInteger("playerTime");
        if(playerTime == null){
            playerTime = -1;
        }
        Integer cooldownReduceSlot = document.getInteger("cooldownReduceSlot");
        if(cooldownReduceSlot == null || cooldownReduceSlot < 1 || cooldownReduceSlot > 9){
            cooldownReduceSlot = DEFAULT_COOLDOWN_REDUCE_SLOT;
        }

        UserSettingDTO userSettingDTO = UserSettingDTO.builder().uuid(uuid).isVisibleInformation(isVisibleInformation).showDamageChat(showDamageChat).showSkillCooldownNotice(showSkillCooldownNotice).showSkillCooldownItem(showSkillCooldownItem).playerTime(playerTime).cooldownReduceSlot(cooldownReduceSlot).build();

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

    public void toggleShowSkillCooldownItem(Player player) {
        boolean current = isShowSkillCooldownItem(player);
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        userSettingDTO.setShowSkillCooldownItem(!current);
        if (current) {
            player.sendMessage("§e 이제 무기 아이템에 스킬 쿨타임이 표시되지 않습니다.");
        } else {
            player.sendMessage("§e 이제 무기 아이템에 스킬 쿨타임이 표시됩니다.");
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

    public void setCooldownReduceSlot(Player player, int slot) {
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        if (userSettingDTO == null) {
            return;
        }
        userSettingDTO.setCooldownReduceSlot(slot);
        userSettingCache.put(player.getUniqueId().toString(), userSettingDTO);
        player.sendMessage("§e 이제 쿨타임 감소 스킬이 §f" + slot + "번 §e슬롯의 무기에 적용됩니다.");
    }

    /** /쿨타임감소 가 적용될 핫바 슬롯 번호(1~9). 캐시가 없으면 기본 2. */
    public int getCooldownReduceSlot(Player player) {
        UserSettingDTO userSettingDTO = userSettingCache.get(player.getUniqueId().toString());
        if (userSettingDTO == null) {
            return DEFAULT_COOLDOWN_REDUCE_SLOT;
        }
        return userSettingDTO.getCooldownReduceSlot();
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

    public boolean isShowSkillCooldownItem(Player player) {
        if (userSettingCache.get(player.getUniqueId().toString()) == null) {
            return true;
        }
        return userSettingCache.get(player.getUniqueId().toString()).isShowSkillCooldownItem();
    }

    public void saveUserSetting(Player player) {
        Document document = userSettingCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        document.append("isVisibleInformation", isVisibleInformation(player));
        document.append("showDamageChat", isShowDamageChat(player));
        document.append("showSkillCooldownNotice", isShowSkillCooldownNotice(player));
        document.append("showSkillCooldownItem", isShowSkillCooldownItem(player));
        document.append("playerTime", getPlayerTime(player));
        document.append("cooldownReduceSlot", getCooldownReduceSlot(player));

        userSettingCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("isVisibleInformation", true)
                .append("showDamageChat", true)
                .append("showSkillCooldownNotice", true)
                .append("showSkillCooldownItem", true)
                .append("playerTime", -1)
                .append("cooldownReduceSlot", DEFAULT_COOLDOWN_REDUCE_SLOT);
        userSettingCollection.insertOne(document);

        return document;
    }
}