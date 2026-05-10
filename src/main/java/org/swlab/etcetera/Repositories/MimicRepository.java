package org.swlab.etcetera.Repositories;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.mongodb.client.MongoCollection;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.MimicDataDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MimicRepository {

    private static MimicRepository instance;

    private final HashMap<String, MimicDataDTO> eliteToMimic = new HashMap<>();
    private final HashMap<String, MimicDataDTO> mimicToData = new HashMap<>();

    private static final MongoCollection<Document> collection =
            DatabaseRegister.getInstance().getMongoDatabase().getCollection("MimicData");

    public MimicRepository() {
        instance = this;
    }

    public static MimicRepository getInstance() {
        if (instance == null) {
            instance = new MimicRepository();
        }
        return instance;
    }

    public void loadData() {
        eliteToMimic.clear();
        mimicToData.clear();

        for (Document doc : collection.find()) {
            List<String> eliteNames = doc.getList("eliteInternalNames", String.class);
            String mimicName = doc.getString("mimicInternalName");

            MimicDataDTO dto = MimicDataDTO.builder()
                    .eliteInternalNames(eliteNames)
                    .mimicInternalName(mimicName)
                    .spawnChance(doc.getInteger("spawnChance", 5))
                    .rewardType(doc.getString("rewardType"))
                    .rewardId(doc.getString("rewardId"))
                    .rewardAmount(doc.getInteger("rewardAmount", 1))
                    .build();

            for (String eliteName : eliteNames) {
                eliteToMimic.put(eliteName, dto);
            }
            mimicToData.put(mimicName, dto);
        }
    }

    public MimicDataDTO getByEliteName(String eliteInternalName) {
        return eliteToMimic.get(eliteInternalName);
    }

    public MimicDataDTO getByMimicName(String mimicInternalName) {
        return mimicToData.get(mimicInternalName);
    }

    public void giveReward(Player player, MimicDataDTO dto) {
        if (dto.getRewardType() == null || dto.getRewardId() == null) {
            return;
        }

        ItemStack item = MMOItems.plugin.getItem(dto.getRewardType(), dto.getRewardId());
        if (item == null) {
            return;
        }
        item.setAmount(dto.getRewardAmount());

        List<ItemStack> items = new ArrayList<>();
        items.add(item);

        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
        Mail mail = mailAPI.createMail("시스템", "§f미믹 처치 보상입니다.", 0.0, items);
        mailAPI.sendMail(player.getName(), mail);

        player.sendMessage("§6[미믹] §f처치 보상이 메일함으로 발송되었습니다.");
    }
}
