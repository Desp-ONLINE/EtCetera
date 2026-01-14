package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.api.DungeonClearEvent;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmoguild.MMOGuild;
import com.binggre.mmoguild.objects.Guild;
import com.binggre.mmoguild.objects.PlayerGuild;
import com.binggre.mmoguild.repository.GuildRepository;
import com.binggre.mmoguild.repository.PlayerGuildRepository;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.binggre.mmotimeraid.api.TimeRaidClearEvent;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
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


public class FirstClearListener implements Listener {

    private final GuildRepository gr = MMOGuild.getPlugin().getGuildRepository();
    private final PlayerGuildRepository pgr = MMOGuild.getPlugin().getPlayerRepository();

    @Getter
    public static MongoCollection<Document> timeRaidPlayerDocument;
    public static MongoCollection<Document> raidPlayerDocument;
    public static MongoCollection<Document> raidFirstClearReward;
    public static MongoCollection<Document> timeRaidFirstClearReward;


    public FirstClearListener() {
        timeRaidPlayerDocument = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TimeDungeonPlayer");
        raidPlayerDocument = DatabaseRegister.getInstance().getMongoDatabase().getCollection("RaidPlayer");
        timeRaidFirstClearReward = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TimeDungeonReward");
        raidFirstClearReward = DatabaseRegister.getInstance().getMongoDatabase().getCollection("RaidReward");
    }

    public Document getDefaultPlayerDocument(Player player) {
        Document document = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("cleared", new ArrayList<String>());

        return document;
    }




    public void updateTimeRaidPlayerDocument(Player player, Document document, String clearedDungeonKey) {
        List<String> list = document.getList("cleared", String.class);
        list.add(clearedDungeonKey);
        document.append("cleared", list);

        timeRaidPlayerDocument.replaceOne(new Document("uuid", player.getUniqueId().toString()), document);
    }
    public void updateRaidPlayerDocument(Player player, Document document, String clearedDungeonKey) {
        List<String> list = document.getList("cleared", String.class);
        list.add(clearedDungeonKey);
        document.append("cleared", list);

        raidPlayerDocument.replaceOne(new Document("uuid", player.getUniqueId().toString()), document);
    }

    @EventHandler
    public void onRaidClear(DungeonClearEvent e){

        boolean clear = e.getDungeonRoom().isClear();
        if(!clear){
            return;
        }
        DungeonRoom dungeonRoom = e.getDungeonRoom();
        Dungeon connected = dungeonRoom.getConnected();

        if(dungeonRoom.getMembers().size() >= 2 || connected.getReplayDay() == 1){
            return;
        }
        if(dungeonRoom.getConnected().getLife() != dungeonRoom.getLife()){
            return;
        }
        String raidName = connected.getName();





        Document raidDocument = raidFirstClearReward.find(new Document("id", raidName)).first();
        List<String> list = raidDocument.getList("rewards", String.class);

        for (Player player : dungeonRoom.getMembers()) {
            String broadcastMessage = "§c  [ RAID CHALLENGE ] §f"+player.getName() +" 님께서 §6"+raidName+" §f레이드 챌린지에 성공하셨습니다!";

            Document playerDocument = raidPlayerDocument.find(new Document("uuid", player.getUniqueId().toString())).first();
            if (playerDocument == null) {
                playerDocument = getDefaultPlayerDocument(player);
                raidPlayerDocument.insertOne(playerDocument);
            }
            if(playerDocument.getList("cleared", String.class).contains(raidName)){
                return;
            }

            ArrayList<ItemStack> rewardItems = new ArrayList<>();
            for (String s : list) {
                String[] split = s.split(":");
                String type = split[0];
                String mmoitemID = split[1];
                Integer amount = Integer.parseInt(split[2]);

                ItemStack item = MMOItems.plugin.getItem(type, mmoitemID);
                item.setAmount(amount);

                rewardItems.add(item);
            }

            Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "레이드 챌린지 성공 보상입니다.", 0, rewardItems);
            MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);

            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(broadcastMessage);
            Bukkit.broadcastMessage("");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, broadcastMessage);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");

            updateRaidPlayerDocument(player, playerDocument, raidName);
        }


    }

    @EventHandler
    public void onTimeRaidClear(TimeRaidClearEvent e) {

        if (e.getDifficulty() <= 6) {
            expLogic(e, 1);
        } else if (e.getDifficulty() <= 9) {

            expLogic(e, 2);
        } else if (e.getDifficulty() <= 12) {

            expLogic(e, 3);
        }

        if (e.getPlayers().size() == 1 && e.getDifficulty() > 9) {

            for (Player player : e.getPlayers()) {
                Document first = timeRaidPlayerDocument.find(new Document("uuid", player.getUniqueId().toString())).first();
                if (first == null) {
                    first = getDefaultPlayerDocument(player);
                    timeRaidPlayerDocument.insertOne(first);
                }


                // 황금의 미궁 10레벨 짜리면 1-10
                String clearedDungeonKey = e.getTimeRaid().getId() +"-"+e.getDifficulty();

                List<String> list = first.getList("cleared", String.class);
                if(list.contains(clearedDungeonKey)) {
                    return;
                } else {
                    Document rewardDocument = timeRaidFirstClearReward.find(new Document("id", clearedDungeonKey)).first();
                    Integer gold = rewardDocument.getInteger("gold");
                    Integer exp = rewardDocument.getInteger("exp");
                    ArrayList<ItemStack> itemList = new ArrayList<>();
                    if(e.getDifficulty() == 10){
                        player.getInventory().addItem(MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_익스트림황금의미궁LV1"));
                    }
                    for (String reward : rewardDocument.getList("rewards", String.class)) {
                        System.out.println(reward);
                        String[] split = reward.split(":");

                        String type = split[0];
                        String mmoitemID = split[1];
                        String amount = split[2];

                        ItemStack item = MMOItems.plugin.getItem(type, mmoitemID);
                        item.setAmount(Integer.parseInt(amount));

                        itemList.add(item);
                    }

                    MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
                    Mail mail = mailAPI.createMail("시스템", "타임 던전 첫 클리어 보상입니다. (" + e.getTimeRaid().getName() + ", Lv." + e.getDifficulty(), gold, itemList);
                    mailAPI.sendMail(player.getName(), mail);

                    player.sendMessage(ColorManager.format("#41B07A  타임 던전: §f"+e.getTimeRaid().getName()+" §7§o(Lv."+e.getDifficulty()+") #41B07A첫 클리어 보상이 메일로 지급되었습니다! §7§o(/메일함 또는 /ㅁ)"));

                    System.out.println("player = " + player.getName() + " 타임 던전 첫 클리어 보상 지급 완료: "+clearedDungeonKey);

                    updateTimeRaidPlayerDocument(player, first, clearedDungeonKey);
                }

            }

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
            gr.saveAsync(guild);
        }
    }
}