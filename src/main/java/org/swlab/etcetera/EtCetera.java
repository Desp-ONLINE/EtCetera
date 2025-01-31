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
import org.swlab.etcetera.Listener.EquipListener;
import org.swlab.etcetera.Listener.JumpListener;

public final class EtCetera extends JavaPlugin {

    public static String channelType = "";
    public static EtCetera instance;

    public static EtCetera getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration config = getConfig();
        config.addDefault("channelType", "lobby");
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        registerEvents();
        registerCommands();

    }

    public static String getChannelType() {
        return channelType;
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
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNbtTag());
        getCommand("spawn").setExecutor(new SpawnCommand());
    }
}
