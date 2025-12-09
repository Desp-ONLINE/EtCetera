package org.swlab.etcetera.Util;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;

public class PetUtil {
    private static MongoCollection<Document> petCollection;
    private static PetUtil instance;

    public PetUtil() {
        if (instance == null) {
            instance = this;
        }
        petCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("Pet");
    }


    public static void loadPlayerPetData(Player player) {
        String name = player.getName();

        Document first = petCollection.find(new Document("uuid", player.getUniqueId().toString())).first();

        if (first == null) {
            insertPlayerData(player);
        }
        first = petCollection.find(new Document("uuid", player.getUniqueId().toString())).first();

        String latestPetID = first.getString("latestPetID");
        String command = "";
        if (latestPetID.isEmpty()) {
            command = "mcpets revoke";
        }
        else {
            command = "mcpets spawn " + latestPetID + " " + name + " true";
        }
        CommandUtil.runCommandAsOP(player, command);
//        System.out.println("command = " + command);
    }

    public static void insertPlayerData(Player player) {
        petCollection.insertOne(new Document().append("uuid", player.getUniqueId().toString()).append("nickname", player.getName()).append("latestPetID", ""));
    }

    public static void savePlayerPetData(Player player, String latestPetID) {

        petCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), new Document("uuid", player.getUniqueId().toString()).append("latestPetID", latestPetID).append("nickname", player.getName()));
    }

    public static String getPlayerData(Player player) {
        return petCollection.find(new Document("uuid", player.getUniqueId().toString())).first().getString("latestPetID");
    }
}
