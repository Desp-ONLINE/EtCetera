package org.swlab.etcetera;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.Commands.CheckNbtTag;
import org.swlab.etcetera.Listener.BasicListener;
import org.swlab.etcetera.Listener.JumpListener;

public final class EtCetera extends JavaPlugin {

    public boolean isDoubleJumpAllowed = false;
//    public boolean isLobbyServer = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration config = getConfig();
        config.addDefault("doubleJump", false);
        config.options().copyDefaults(true);
        saveConfig();
        isDoubleJumpAllowed = config.getBoolean("doubleJump");
        registerEvents();
        registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        if (isDoubleJumpAllowed) {
            Bukkit.getPluginManager().registerEvents(new JumpListener(), this);
            System.out.println("asdgasdgasdg");
        }
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNbtTag());
    }
}
