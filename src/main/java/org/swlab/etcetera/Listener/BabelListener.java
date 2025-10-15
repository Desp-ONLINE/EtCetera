package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.util.Vector;
import org.desp.babelTower.event.BabelClearEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;


public class BabelListener implements Listener {
    @EventHandler
    public void onBabelClear(BabelClearEvent e){
        Player player = e.getPlayer();
        int floor = e.getFloor();

        String emptyLine = ColorManager.format("#FF3737§n                                                                              §f");
        String emptyMessage = "";
        String broadcastMessage = ColorManager.format("§f   "+player.getName()+" #FFB1B1님께서 바벨탑 §c"+floor+"#FFB1B1층을 클리어 하셨습니다!");
        if(floor >70){
            Bukkit.broadcastMessage(emptyLine);
            Bukkit.broadcastMessage(emptyMessage);
            Bukkit.broadcastMessage(broadcastMessage);
            Bukkit.broadcastMessage(emptyLine);

            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyLine);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyMessage);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, broadcastMessage);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyLine);
        }
    }
}
