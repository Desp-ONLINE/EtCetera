package org.swlab.etcetera.Listener;

import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;

public class ConsumableListener implements Listener{

    @EventHandler
    public void onItemConsume(ConsumableConsumedEvent e){
        if(e.getMMOItem().getId().equals("기타_루비")){
            if(EtCetera.getChannelType().equals("dungeon")){
                e.setCancelled(true);
                e.getPlayer().sendMessage("§c 로비에서만 사용 가능합니다.");
            }
        }
    }
}