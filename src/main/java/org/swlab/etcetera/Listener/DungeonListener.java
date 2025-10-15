package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.api.DungeonClearEvent;
import com.binggre.mmodungeon.api.DungeonFailedEvent;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class DungeonListener implements Listener {

    private static boolean isFirstClearRaven = true;

    @EventHandler
    public void onDungeonFail(DungeonFailedEvent e) {



        e.setCancelledAmount(true);
    }
//
//    @EventHandler
//    public void onDungeonClear(DungeonClearEvent e) {
//        if(e.getDungeonRoom().getConnected().getId().equals(10) && isFirstClearRaven) {
//            String emptyMessage = "";
//
//            String message = ColorManager.format("   &#644E4E레&#675252기&#6A5656온&#6E5B5B의 &#746363군&#776767단&#7A6C6C장&#7E7070들&#817474을 &#877D7D모&#8A8181두 &#918989쓰&#948E8E러&#979292뜨&#9A9696렸&#9D9A9A습&#A19F9F니&#A4A3A3다&#A7A7A7.");
//            List<PlayerData> members = e.getParty().getMembers();
//            int size = members.size();
//            int i = 0;
//
//            String parties = "§7 처치 파티: ";
//            for (PlayerData member : members) {
//                i++;
//                if(i != size){
//                    parties += member.getPlayer().getName() + ", ";
//                } else {
//                    parties += member.getPlayer().getName();
//                }
//
//            }
//
//            Bukkit.broadcastMessage(emptyMessage);
//            Bukkit.broadcastMessage(message);
//            Bukkit.broadcastMessage(parties);
//            Bukkit.broadcastMessage(emptyMessage);
//
//
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyMessage);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, parties);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyMessage);
//
//            isFirstClearRaven = false;
//
//        }
//    }

}
