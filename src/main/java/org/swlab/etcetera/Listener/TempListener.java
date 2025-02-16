package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TempListener implements Listener {

    @EventHandler
    public void applyHUD(PlayerJoinEvent e) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, "huds layout " + e.getPlayer().getName() + " add slot_hud-layout");
        Bukkit.dispatchCommand(console, "huds layout " + e.getPlayer().getName() + " remove bar_hud-layout");
        Bukkit.dispatchCommand(console, "huds layout " + e.getPlayer().getName() + " add left_ui");
    }
}
