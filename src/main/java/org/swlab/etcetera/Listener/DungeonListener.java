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

    private static boolean isFirstClearKanaloa = true;
    @EventHandler
    public void onDungeonFail(DungeonFailedEvent e) {
        e.setCancelledAmount(true);
    }

    @EventHandler
    public void onDungeonSuccess(DungeonClearEvent e){
        if(e.getDungeonRoom().getConnected().getId().equals(9) && isFirstClearKanaloa){
            List<String> names = new ArrayList<>();
            ItemStack item = MMOItems.plugin.getItem("CONSUMABLE", "히든_공허의모자");
            List<ItemStack> itemStacks = new ArrayList<>();
            itemStacks.add(item);
            for (PlayerData member : e.getParty().getMembers()) {
                String name = member.getPlayer().getName();
                names.add(name);

                Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "카날로아 최초 격파를 축하합니다!", 0, itemStacks);
                MMOMail.getInstance().getMailAPI().sendMail(member.getPlayer().getName(), mail);
            }
            String playerNames = String.join(", ", names);
            String line = ColorManager.format("#96003C§n                                                  ");
            String announce = ColorManager.format("#96003C파#8D0A3E티 #7C1D43(§f"+playerNames+"#742745) #623B4A에#5A444C서 #514B52최#504856초#50465A로 #4F4061카#4F3D65날#4F3A69로#4E376D아#4E3571를 #4D2F79격#4D2C7D파#4C2980했#4C2684습#4C2488니#4B218C다#4B1E90!");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, line);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, announce);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, line);
            Bukkit.broadcastMessage(line);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(announce);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(line);
            isFirstClearKanaloa = false;
        }

    }
}
