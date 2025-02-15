package org.swlab.etcetera.Listener;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import org.swlab.etcetera.EtCetera;

import java.util.HashMap;
import java.util.UUID;


public class LevelUpListener implements Listener{
    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent e){
        Player player = e.getPlayer();
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(player).giveAttributePoints(1);
        return;
    }
}
