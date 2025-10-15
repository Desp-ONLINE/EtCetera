package org.swlab.etcetera.Listener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import fr.skytasul.quests.api.events.QuestFinishEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;
import us.ajg0702.parkour.api.events.PlayerEndParkourEvent;
import us.ajg0702.parkour.api.events.PlayerStartParkourEvent;

import java.util.*;


public class JumpMapListener implements Listener{

    @EventHandler
    public void onJumpmapFinish(PlayerEndParkourEvent e){
        Player player = e.getPlayer();
        int fallScore = e.getFallScore();
        MongoCollection<Document> jumpmapLogCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("JumpmapLog");
        if(fallScore >= 20){
            ItemStack worldCoreItemStack = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_세계의핵");
            worldCoreItemStack.setAmount(5);
            player.getInventory().addItem(worldCoreItemStack);
            player.sendMessage("§a 점프맵 20점 이상 보상으로 §f세계의 핵 x5 §a아이템을 획득했습니다!");
            Document first = jumpmapLogCollection.find().first();
            List<String> players = first.getList("players", String.class);
            if(players.contains(player.getName())){
                player.sendMessage("§c 오늘은 더 이상 점프맵 일일 보상을 수령할 수 없습니다! 내일 다시 시도해주세요!");
                return;
            }
            players.add(player.getName());
            Document updateDocument = new Document().append("players", players);
            jumpmapLogCollection.updateOne(new Document(), new Document("$set", updateDocument));
            ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_점프맵상자");
            player.getInventory().addItem(item);
            player.sendMessage("§a 점프맵 클리어 보상을 획득했습니다!");
        }
    }
}
