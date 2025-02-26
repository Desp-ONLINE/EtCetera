package org.swlab.etcetera.Listener;

import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class LevelUpListener implements Listener {

    @EventHandler
    public void onLevelup(PlayerLevelUpEvent e){
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).giveAttributePoints(1);
    }
}
