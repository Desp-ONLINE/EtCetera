package org.swlab.etcetera.Listener;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;

public class LevelUpListener implements Listener {

    @EventHandler
    public void onLevelup(PlayerLevelUpEvent e){
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).giveAttributePoints(1);
        if(e.getNewLevel() == 100){
            Bukkit.broadcast(Component.text("§e! "+e.getPlayer().getName()+"§f님께서 §c100 레벨§f을 달성하셨습니다!"));
            e.getPlayer().sendMessage("§e /메일함 §f을 확인해보세요.");
        }
    }
}
