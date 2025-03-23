package org.swlab.etcetera.Listener;

import fr.skytasul.quests.api.events.DialogSendMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
