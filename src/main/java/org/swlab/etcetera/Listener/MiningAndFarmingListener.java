package org.swlab.etcetera.Listener;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.experience.EXPSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.desp.mining.event.MiningEvent;
import org.khw3268480.farming.event.FarmingEvent;
import org.swlab.etcetera.EtCetera;


public class MiningAndFarmingListener implements Listener{
    @EventHandler
    public void onMining(MiningEvent e){
        Player player = e.getPlayer();

        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(player).giveExperience(20, EXPSource.OTHER);
    }
    @EventHandler
    public void onMining(FarmingEvent e){
        Player player = e.getPlayer();
        if(!e.isSuccess()){
            return;
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(player).giveExperience(40, EXPSource.OTHER);
    }

}
