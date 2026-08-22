package org.swlab.etcetera.Listener;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FurnitureProtectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnitureBreak(FurnitureBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

}
