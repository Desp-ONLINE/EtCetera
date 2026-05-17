package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastComponentVelocityListener;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;

public class LevelUpListener implements Listener {

    @EventHandler
    public void onLevelup(PlayerLevelUpEvent e){
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());


        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());

        Player player = e.getPlayer();

        String name = player.getName();
        mmoCoreAPI.getPlayerData(e.getPlayer()).giveAttributePoints(e.getNewLevel() - e.getOldLevel());
        if(e.getNewLevel() == 100){
            String message = "§e "+e.getPlayer().getName()+"§f 님께서 §c레벨 100§f을 달성하셨습니다! 축하해주세요!!";
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "§f");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(message);
            Bukkit.broadcastMessage("");
            e.getPlayer().sendMessage("§e /메일함 §f을 확인해보세요.");
        }
        else if(e.getNewLevel() == 200){
            String message = ColorManager.format("§e    #3789B8"+name+"§f님께서 레벨 #1D4F6D200 §f을 달성하셨습니다! #A9E0FF축하해주세요!!");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");

            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(message);
            Bukkit.broadcastMessage("");
//            e.getPlayer().sendMessage("§e /메일함 §f을 확인해보세요.");
        }
    }
}
