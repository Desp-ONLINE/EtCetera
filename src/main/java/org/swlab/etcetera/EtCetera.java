package org.swlab.etcetera;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.Commands.CheckNbtTag;
import org.swlab.etcetera.Commands.SpawnCommand;
import org.swlab.etcetera.Listener.BasicListener;
import org.swlab.etcetera.Listener.JumpListener;

public final class EtCetera extends JavaPlugin {

    public static String channelType = "";
    public static int channelNumber = 0;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration config = getConfig();
        config.addDefault("channelType", "lobby");
        config.addDefault("channelNumber", 0);
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        channelNumber = config.getInt("channelNumber");
        registerEvents();
        registerCommands();

    }

    public static String getChannelType() {
        return channelType;
    }

    public static int getChannelNumber() {
        return channelNumber;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        if (channelType.equals("lobby")) {
            Bukkit.getPluginManager().registerEvents(new JumpListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNbtTag());
        getCommand("spawn").setExecutor(new SpawnCommand());
    }
}
