package org.swlab.etcetera.Repositories;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.RaidCoinDataDTO;
import org.swlab.etcetera.Dto.RaidCoinPlayerDTO;

import java.util.*;

public class RaidCoinRepository {

    public static RaidCoinRepository instance;
    public HashMap<String, RaidCoinPlayerDTO> raidCoinPlayerCache = new HashMap<>();
    public LinkedHashMap<String, RaidCoinDataDTO> raidCoinDataCache = new LinkedHashMap<>();

    public RaidCoinRepository() {
        instance = this;
    }

    public static RaidCoinRepository getInstance() {
        if (instance == null) {
            instance = new RaidCoinRepository();
        }
        return instance;
    }


    public static final MongoCollection<Document> raidCoinPlayerCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("RaidCoinPlayer");
    public static final MongoCollection<Document> raidCoinDataCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("RaidCoinData");

    public void resetDatas() {
        raidCoinPlayerCollection.deleteMany(new Document());
    }

    public void loadCoinData() {
        for (Document document : raidCoinDataCollection.find()) {
            String raidName = document.getString("raidName");
            Integer normalAmount = document.getInteger("normalAmount");
            Integer specialAmount = document.getInteger("specialAmount");

            RaidCoinDataDTO raidCoinDataDTO = RaidCoinDataDTO.builder().raidName(raidName).normalAmount(normalAmount).specialAmount(specialAmount).build();

            raidCoinDataCache.put(raidName, raidCoinDataDTO);
        }
    }


    public void loadUserData(Player player) {

        Document document = raidCoinPlayerCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = insertDefaultDocument(player);
        }
        String uuid = document.getString("uuid");
        String nickname = document.getString("nickname");
        String highestClearedRaid = document.getString("highestClearedRaid");

        RaidCoinPlayerDTO raidCoinPlayerDTO = RaidCoinPlayerDTO.builder().uuid(uuid).nickname(nickname).highestClearedRaid(highestClearedRaid).build();

        raidCoinPlayerCache.put(uuid, raidCoinPlayerDTO);

    }

    public Integer getNormalRewardGapAmount(Player player, String clearedRaidName) {
        RaidCoinPlayerDTO raidCoinPlayerDTO = raidCoinPlayerCache.get(player.getUniqueId().toString());
        String highestClearedRaid = raidCoinPlayerDTO.getHighestClearedRaid();


        RaidCoinDataDTO clearedDataDTO = raidCoinDataCache.get(clearedRaidName);
        RaidCoinDataDTO highestDataDTO = raidCoinDataCache.get(highestClearedRaid);
        if (highestDataDTO == null) {
            return clearedDataDTO.getNormalAmount();
        }

        int result = clearedDataDTO.getNormalAmount() - highestDataDTO.getNormalAmount();


        return Math.max(result, 0);
    }

    public Integer getSpecialRewardGapAmount(Player player, String clearedRaidName) {
        RaidCoinPlayerDTO raidCoinPlayerDTO = raidCoinPlayerCache.get(player.getUniqueId().toString());
        String highestClearedRaid = raidCoinPlayerDTO.getHighestClearedRaid();

        RaidCoinDataDTO clearedDataDTO = raidCoinDataCache.get(clearedRaidName);
        RaidCoinDataDTO highestDataDTO = raidCoinDataCache.get(highestClearedRaid);

        if (highestDataDTO == null) {
            return clearedDataDTO.getSpecialAmount();
        }

        int result = clearedDataDTO.getSpecialAmount() - highestDataDTO.getSpecialAmount();

        return Math.max(result, 0);
    }

    public void updateUserRaidData(Player player, String clearedRaidName) {

        RaidCoinPlayerDTO raidCoinPlayerDTO = raidCoinPlayerCache.get(player.getUniqueId().toString());
        raidCoinPlayerDTO.setHighestClearedRaid(clearedRaidName);

        raidCoinPlayerCache.put(player.getUniqueId().toString(), raidCoinPlayerDTO);

    }

    public boolean giveNormalReward(Player player, String clearedRaidName) {
        Integer normalRewardGapAmount = getNormalRewardGapAmount(player, clearedRaidName);
        if (normalRewardGapAmount == 0) {
            return false;
        }
        List<ItemStack> items = new ArrayList<>();

        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_송편");
        item.setAmount(normalRewardGapAmount);
        items.add(item);

        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();

        Mail mail = mailAPI.createMail("시스템", "§f송편 이벤트 보상입니다.", 0, items);
        mailAPI.sendMail(player.getName(), mail);
        player.sendMessage("§e    [ EVENT ]§f 송편 §6" + normalRewardGapAmount + "§f개를 획득했습니다.");


        return true;


    }

    public boolean giveSpecialReward(Player player, String clearedRaidName) {
        Integer specialRewardGapAmount = getSpecialRewardGapAmount(player, clearedRaidName);
        if (specialRewardGapAmount == 0) {
            return false;
        }
        List<ItemStack> items = new ArrayList<>();

        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_황금송편");
        item.setAmount(specialRewardGapAmount);
        items.add(item);

        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();

        Mail mail = mailAPI.createMail("시스템", "§f황금 송편 이벤트 보상입니다.", 0, items);
        mailAPI.sendMail(player.getName(), mail);

        player.sendMessage("§e    [ EVENT ]§e 황금 송편 §6" + specialRewardGapAmount + "§e개를 획득했습니다.");


        return true;


    }


    public void saveUserData(Player player) {

        RaidCoinPlayerDTO raidCoinPlayerDTO = raidCoinPlayerCache.get(player.getUniqueId().toString());
        Document document = raidCoinPlayerCollection.find(new Document("uuid", player.getUniqueId().toString())).first();
        document.append("nickname", player.getName());
        document.append("highestClearedRaid", raidCoinPlayerDTO.getHighestClearedRaid());

        raidCoinPlayerCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Document insertDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("highestClearedRaid", "");
        raidCoinPlayerCollection.insertOne(document);

        return document;
    }
}