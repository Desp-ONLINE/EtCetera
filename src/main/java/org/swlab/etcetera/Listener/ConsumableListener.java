package org.swlab.etcetera.Listener;

import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.swlab.etcetera.EtCetera;

public class ConsumableListener implements Listener{

    @EventHandler
    public void onItemConsume(ConsumableConsumedEvent e){
        if(e.getPlayer().hasPotionEffect(PotionEffectType.SATURATION)){
            e.getPlayer().sendMessage("§c 현재 해당 아이템을 사용할 수 없습니다.");
            e.setCancelled(true);
            return;
        }
        if(e.getMMOItem().getId().equals("기타_루비")){
            if(EtCetera.getChannelType().equals("dungeon")){
                e.setCancelled(true);
                e.getPlayer().sendMessage("§c 로비에서만 사용 가능합니다.");
            }
        }
    }
}