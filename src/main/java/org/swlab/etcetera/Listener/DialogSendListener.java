package org.swlab.etcetera.Listener;

import fr.skytasul.quests.api.events.DialogSendEvent;
import fr.skytasul.quests.api.events.DialogSendMessageEvent;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicMobLootDropEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class DialogSendListener implements Listener {
    @EventHandler
    public void onQuestdialog(DialogSendMessageEvent e) {

        Player player = e.getPlayer();
        String name1 = e.getDialog().getNpc().getNpc().getName();
        String text = e.getMessage().text;
        if(player.getName().equals("dople_L")){
            if(e.getMessage().sender.name().equals("NPC")){
                player.sendTitle("§6"+name1, text);
            }
            else if (e.getMessage().sender.name().equals("PLAYER")){
                player.sendTitle("§e"+player.getName(), text);
            }
        }

    }


}
