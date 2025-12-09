package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.mongodb.client.MongoCollection;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Listener.TimeDungeonListener;

import java.text.NumberFormat;
import java.util.List;

public class TimeDungeonFirstClearCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        String clearedDungeonKey = strings[0];
        Player player = (Player) sender;
        MongoCollection<Document> firstClearReward = TimeDungeonListener.firstClearReward;

        player.sendMessage(ColorManager.format("#41B07A 해당 §cEXTREME #41B07A등급의 타임 던전 첫 클리어 보상 정보입니다."));
        player.sendMessage("§f");

        Document document
                = firstClearReward.find(new Document("id", clearedDungeonKey)).first();

        Integer gold = document.getInteger("gold");
        List<String> list = document.getList("rewards", String.class);

        for (String ls : list) {
            String[] split = ls.split(":");
            String type = split[0];
            String mmoitemID = split[1];
            String amount = split[2];

            ItemStack item = MMOItems.plugin.getItem(Type.get(type), mmoitemID);

            String displayName = item.getItemMeta().getDisplayName();


            player.sendMessage(ColorManager.format("§f   §7- " + displayName + " §fx" + amount));
        }
        player.sendMessage("§f   §7- §e" + NumberFormat.getIntegerInstance().format(gold) + " §f골드");
        player.sendMessage("§f");


        return true;
    }


}
