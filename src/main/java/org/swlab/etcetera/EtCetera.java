package org.swlab.etcetera;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.Commands.*;
import org.swlab.etcetera.Listener.BasicListener;
import org.swlab.etcetera.Listener.MobListener;

public final class EtCetera extends JavaPlugin {

    private static EtCetera pluginInstance;

    public static EtCetera getPluginInstance() {
        return pluginInstance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerEvents();
        registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobListener(), this);
    }
    public void registerCommands(){
        getCommand("nbt검사").setExecutor(new CheckNbtTag());
    }
}
