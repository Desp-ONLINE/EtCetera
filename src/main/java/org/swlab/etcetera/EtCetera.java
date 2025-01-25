package org.swlab.etcetera;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.Commands.CheckNbtTag;
import org.swlab.etcetera.Listener.BasicListener;
import org.swlab.etcetera.Listener.JumpListener;

public final class EtCetera extends JavaPlugin {

    public String channelType = "";
//    public boolean isLobbyServer = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration config = getConfig();
        config.addDefault("doubleJump", false);
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        registerEvents();
        registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        if (channelType.equals("lobby")) {
            Bukkit.getPluginManager().registerEvents(new JumpListener(), this);
//            World world = Bukkit.getWorld("world");
//            Location loc = new Location(world, 159.574, 4, -737.484);
//            world.playEffect(loc, Effect.REDSTONE_TORCH_BURNOUT, 2003);
        }
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNbtTag());
    }
}
