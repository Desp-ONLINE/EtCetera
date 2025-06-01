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

}
