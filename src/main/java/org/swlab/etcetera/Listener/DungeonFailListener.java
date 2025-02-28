package org.swlab.etcetera.Listener;

import com.binggre.mmodungeon.api.DungeonFailedEvent;
import fr.skytasul.quests.api.events.QuestFinishEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;


public class DungeonFailListener implements Listener{
    @EventHandler
    public void onDungeonFail(DungeonFailedEvent e){
        e.setCancelledAmount(true);
    }
}
