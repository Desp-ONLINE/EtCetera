package org.swlab.etcetera.Listener;

import com.binggre.mmodungeon.api.DungeonFailedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class DungeonFailListener implements Listener{
    @EventHandler
    public void onDungeonFail(DungeonFailedEvent e){
        e.setCancelledAmount(true);
    }
}
