package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.api.DungeonFailedEvent;
import com.binggre.mmoguild.MMOGuild;
import com.binggre.mmoguild.objects.Guild;
import com.binggre.mmoguild.objects.PlayerGuild;
import com.binggre.mmoguild.repository.GuildRepository;
import com.binggre.mmoguild.repository.PlayerGuildRepository;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmotimeraid.api.TimeRaidClearEvent;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.ArrayList;
import java.util.List;


public class TimeDungeonListener implements Listener {

    private final GuildRepository gr = MMOGuild.getPlugin().getGuildRepository();
    private final PlayerGuildRepository pgr = MMOGuild.getPlugin().getPlayerRepository();

    @Getter
    public static MongoCollection<Document> playerDocument;
    public static MongoCollection<Document> firstClearReward;


    public TimeDungeonListener() {
        playerDocument = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TimeDungeonPlayer");
        firstClearReward = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TimeDungeonReward");
    }

    public Document getDefaultDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("cleared", new ArrayList<String>());

        return document;
    }

    public void updateDocument(Player player, Document document, String clearedDungeonKey) {
        List<String> list = document.getList("cleared", String.class);
        list.add(clearedDungeonKey);
        document.append("cleared", list);

        playerDocument.replaceOne(new Document("uuid", player.getUniqueId().toString()), document);
    }

    @EventHandler

    public void onTimeRaidClear(TimeRaidClearEvent e) {



//        if (e.getPlayers().size() == 1 && e.getDifficulty() > 9) {
//            for (Player player : e.getPlayers()) {
//                Document first = playerDocument.find(new Document("uuid", player.getUniqueId().toString())).first();
//                if (first == null) {
//                    first = getDefaultDocument(player);
//                    playerDocument.insertOne(first);
//                }
//
//                // 황금의 미궁 10레벨 짜리면 1-10
//                String clearedDungeonKey = e.getTimeRaid().getId() +"-"+e.getDifficulty();
//
//                List<String> list = first.getList("cleared", String.class);
//                if(list.contains(clearedDungeonKey)) {
//                    return;
//                } else {
//                    Document rewardDocument = firstClearReward.find(new Document("id", clearedDungeonKey)).first();
//                    Integer gold = rewardDocument.getInteger("gold");
//                    Integer exp = rewardDocument.getInteger("exp");
//                    ArrayList<ItemStack> itemList = new ArrayList<>();
//                    for (String reward : rewardDocument.getList("rewards", String.class)) {
//                        String[] split = reward.split(":");
//
//                        String type = split[0];
//                        String mmoitemID = split[1];
//                        String amount = split[2];
//
//                        ItemStack item = MMOItems.plugin.getItem(type, mmoitemID);
//                        item.setAmount(Integer.parseInt(amount));
//
//                        itemList.add(item);
//                    }
//
//                    MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
//                    Mail mail = mailAPI.createMail("시스템", "타임 던전 첫 클리어 보상입니다. (" + e.getTimeRaid().getName() + ", Lv." + e.getDifficulty(), gold, itemList);
//                    mailAPI.sendMail(player.getName(), mail);
//
//                    player.sendMessage(ColorManager.format("#41B07A  타임 던전: §f"+e.getTimeRaid().getName()+" §7§o(Lv."+e.getDifficulty()+") #41B07A첫 클리어 보상이 메일로 지급되었습니다! §7§o(/메일함 또는 /ㅁ)"));
//
//                    System.out.println("player = " + player.getName() + " 타임 던전 첫 클리어 보상 지급 완료: "+clearedDungeonKey);
//
//                    updateDocument(player, first, clearedDungeonKey);
//                }
//
//            }
//
//        }
        if (e.getDifficulty() <= 6) {
            expLogic(e, 1);
        } else if (e.getDifficulty() <= 9) {
            expLogic(e, 2);
        } else if (e.getDifficulty() <= 12) {
            expLogic(e, 3);
        }
    }

    private void expLogic(TimeRaidClearEvent e, int exp) {
        for (Player player : e.getPlayers()) {
            PlayerGuild playerGuild = pgr.get(player.getUniqueId());
            if (playerGuild == null) {
                continue;
            }
            Guild guild = playerGuild.getGuild();
            if (guild == null) {
                continue;
            }
            guild.getInfo().addExp(exp);
            guild.updateRedis();
            // ㄱㄱ요
            gr.saveAsync(guild);
        }
    }
}