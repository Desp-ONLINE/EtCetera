package org.swlab.etcetera.Database;

import org.bukkit.configuration.file.YamlConfiguration;
import org.swlab.etcetera.EtCetera;

import java.io.File;

public class DBConfig {

    public String getMongoConnectionContent(){
        File file = new File(EtCetera.getInstance().getDataFolder().getPath() + "/config.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String url = yml.getString("mongodb.url");
        int port = yml.getInt("mongodb.port");
        String address = yml.getString("mongodb.address");

        return String.format("%s%s:%s/EtCetera", url,address, port);
    }
}
