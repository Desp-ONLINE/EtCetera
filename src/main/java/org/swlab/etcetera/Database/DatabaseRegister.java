package org.swlab.etcetera.Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class DatabaseRegister {

    private final MongoClient mongoClient;

    @Getter
    private final MongoDatabase mongoDatabase;
    @Getter
    public static DatabaseRegister instance;


    String dbConnectUri = "";

    public DatabaseRegister() {
        DBConfig dbConfig = new DBConfig();
        dbConnectUri = dbConfig.getMongoConnectionContent();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(dbConnectUri)).build();
        this.mongoClient = MongoClients.create(settings);
        this.mongoDatabase = mongoClient.getDatabase("EtCetera");
        instance = this;
    }
}
