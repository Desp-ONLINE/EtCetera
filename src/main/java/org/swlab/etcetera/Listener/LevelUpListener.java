package org.swlab.etcetera.Listener;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.List;

public class LevelUpListener implements Listener {

    @EventHandler
    public void onLevelup(PlayerLevelUpEvent e){
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).giveAttributePoints(1);
        if(e.getNewLevel() == 100){
            Bukkit.broadcast(Component.text("§e! "+e.getPlayer().getName()+"§f님께서 §c100 레벨§f을 달성하셨습니다!"));
            e.getPlayer().sendMessage("§e /메일함 §f을 확인해보세요.");
        }
    }
}
