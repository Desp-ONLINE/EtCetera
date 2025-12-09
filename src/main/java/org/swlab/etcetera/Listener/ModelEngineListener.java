package org.swlab.etcetera.Listener;

import com.ticxo.modelengine.api.events.RemoveModelEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModelEngineListener implements Listener {
    @EventHandler
    public void onModelRemove(RemoveModelEvent e){
        e.setCancelled(true);
//        System.out.println(" =ModelRemoveEVventCanceled ");
    }


}
