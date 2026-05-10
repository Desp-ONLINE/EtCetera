package org.swlab.etcetera.Commands;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import com.mongodb.client.MongoCollection;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Listener.FirstClearListener;

import java.util.ArrayList;
import java.util.List;

public class RaidChallengeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("*")) {
            commandSender.sendMessage("§c 권한이 없습니다.");
            return false;
        }
        if (strings.length < 2) {
            commandSender.sendMessage("§c 사용법: /레이드챌린지 <플레이어> <레이드이름>");
            return false;
        }
        Player player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            commandSender.sendMessage("§c 미접속중");
            return false;
        }
        StringBuilder raidNameBuilder = new StringBuilder(strings[1]);
        for (int i = 2; i < strings.length; ++i) {
            raidNameBuilder.append(" ").append(strings[i]);
        }
        String raidName = raidNameBuilder.toString();
        MongoCollection<Document> raidPlayerDocument = FirstClearListener.raidPlayerDocument;
        MongoCollection<Document> raidFirstClearReward = FirstClearListener.raidFirstClearReward;
        Document raidDocument = raidFirstClearReward.find(new Document("id", raidName)).first();
        if (raidDocument == null) {
            commandSender.sendMessage("§c 존재하지 않는 레이드입니다: " + raidName);
            return false;
        }
        Document playerDocument = raidPlayerDocument.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (playerDocument == null) {
            playerDocument = new Document().append("uuid", player.getUniqueId().toString()).append("nickname", player.getName()).append("cleared", new ArrayList());
            raidPlayerDocument.insertOne(playerDocument);
        }
        if (playerDocument.getList("cleared", String.class).contains(raidName)) {
            commandSender.sendMessage("§c " + player.getName() + " 님은 이미 " + raidName + " 레이드 챌린지를 클리어했습니다.");
            return false;
        }
        List<String> rewards = raidDocument.getList("rewards", String.class);
        ArrayList<ItemStack> rewardItems = new ArrayList<ItemStack>();
        for (String reward : rewards) {
            String[] split = reward.split(":");
            String type = split[0];
            String mmoitemID = split[1];
            int amount = Integer.parseInt(split[2]);
            ItemStack item = MMOItems.plugin.getItem(type, mmoitemID);
            item.setAmount(amount);
            rewardItems.add(item);
        }
        Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "레이드 챌린지 성공 보상입니다.", 0.0, rewardItems);
        MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);
        String broadcastMessage = "§c  [ RAID CHALLENGE ] §f" + player.getName() + " 님께서 §6" + raidName + " §f레이드 챌린지에 성공하셨습니다!";
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(broadcastMessage);
        Bukkit.broadcastMessage("");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, new String[]{""});
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, new String[]{broadcastMessage});
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, new String[]{""});
        List clearedList = playerDocument.getList("cleared", String.class);
        clearedList.add(raidName);
        playerDocument.append("cleared", clearedList);
        raidPlayerDocument.replaceOne(new Document("uuid", player.getUniqueId().toString()), playerDocument);
        commandSender.sendMessage("§a " + player.getName() + " 님의 " + raidName + " 레이드 챌린지 클리어 처리 완료!");
        return true;
    }
}
