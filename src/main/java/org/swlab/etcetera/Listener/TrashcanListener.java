package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.mongodb.client.MongoCollection;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.swlab.etcetera.Commands.TrashcanCommand;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrashcanListener implements Listener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @EventHandler
    public void onTrashcanClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof TrashcanCommand)) {
            return;
        }

        HumanEntity player = e.getPlayer();
        List<Document> trashedItems = new ArrayList<>();
        List<String> trashedNames = new ArrayList<>();
        for (ItemStack content : e.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR) {
                continue;
            }
            trashedNames.add(getItemName(content) + " §7x" + content.getAmount());

            String id = MMOItems.getID(content);
            if (id == null) {
                continue;
            }
            Document itemDocument = new Document()
                    .append("id", id)
                    .append("type", MMOItems.getType(content).getId())
                    .append("amount", content.getAmount());

            ItemMeta itemMeta = content.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                itemDocument.append("displayName", itemMeta.getDisplayName());
            }
            trashedItems.add(itemDocument);
        }

        if (trashedNames.isEmpty()) {
            return;
        }

        player.sendMessage(ColorManager.format("#FF6B6B[쓰레기통] §f다음 아이템을 버렸습니다:"));
        for (String trashedName : trashedNames) {
            player.sendMessage(ColorManager.format("§7 - " + trashedName));
        }

        if (trashedItems.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        Document logDocument = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName())
                .append("time", DATE_FORMAT.format(new Date(now)))
                .append("timestamp", now)
                .append("items", trashedItems);

        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
            MongoCollection<Document> trashcanLog = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TrashcanLog");
            trashcanLog.insertOne(logDocument);
        });
    }

    private String getItemName(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.hasDisplayName()) {
            return itemMeta.getDisplayName();
        }
        return item.getType().name();
    }
}
