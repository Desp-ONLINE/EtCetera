package org.swlab.etcetera.Util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;

public class PetUtil {
    private static MongoCollection<Document> petCollection;
    private static PetUtil instance;

    public PetUtil(){
        if(instance == null){
            instance = this;
        }
        petCollection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("Pet");
    }


    public static void loadPlayerPetData(Player player) {
        String name = player.getName();

        Document first = petCollection.find(new Document("uuid", player.getUniqueId().toString())).first();

        if(first == null){
            insertPlayerData(player);
        }
        first = petCollection.find(new Document("uuid", player.getUniqueId().toString())).first();

        String latestPetID = first.getString("latestPetID");
        if(latestPetID.equals("")){
            return;
        }
        String command = "mcpets spawn " + latestPetID + " " + name + " true";
        System.out.println(" command = " +  command);
        CommandUtil.runCommandAsOP(player, command);
    }

    public static void insertPlayerData(Player player) {
        petCollection.insertOne(new Document().append("uuid", player.getUniqueId().toString()).append("nickname", player.getName()).append("latestPetID", ""));
    }

    public static void savePlayerPetData(Player player, String latestPetID) {
        petCollection.replaceOne(new Document("uuid", player.getUniqueId().toString()), new Document("uuid", player.getUniqueId().toString()).append("latestPetID", latestPetID).append("nickname", player.getName()));
    }
}
