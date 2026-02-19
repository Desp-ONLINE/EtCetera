package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.events.QuestFinishEvent;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.players.PlayerAccountImplementation;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.desp.upgrade.dto.UpgradeData;
import org.desp.upgrade.event.UpgradeDestroyEvent;
import org.desp.upgrade.event.UpgradeSuccessEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.NameTagUtil;

import java.util.ArrayList;
import java.util.Arrays;


public class UpgradeListener implements Listener{

    @EventHandler
    public void onUpgrade(UpgradeSuccessEvent e){

        Player player = e.getPlayer();

        String excaliberDominanceMessage = ColorManager.format("#3C53BA        "+player.getName()+" §f님께서 #33387C&n초#35488D&n월#38579F&n자#3A67B0&n의 #3A6AB3&n대#385DA5&n검#375198&n, #33387C&n엑#394B9D&n스#3E5DBE&n칼#4470DE&n리#4982FF&n버 §f의 주인이 되었습니다.");

        if(e.getUpgradeData().getAfterWeapon().equals("합성무기_초월자의대검0")){
            sendBroadcast(excaliberDominanceMessage);
        }
    }

    public void sendBroadcast(String msg){
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(msg);
        Bukkit.broadcastMessage("");

        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, msg);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
    }

    @EventHandler
    public void onDestroyed(UpgradeDestroyEvent e){
        Player player = e.getPlayer();
        UpgradeData upgradeData = e.getUpgradeData();
        String afterWeapon = upgradeData.getAfterWeapon();
        if(afterWeapon.startsWith("주간반지_")){
            String substring = afterWeapon.substring(0, afterWeapon.length() - 1);
            ItemStack ringWeek = MMOItems.plugin.getItem("RING_WEEK", substring);


            Mail mail = MMOMail.getInstance().getMailAPI().createMail("시스템", "연마 파괴에 대한 반지 보상입니다.", 0, new ArrayList<>(Arrays.asList(ringWeek)));
            MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);

        }
    }
}
