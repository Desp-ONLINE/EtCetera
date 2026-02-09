package org.swlab.etcetera.Listener;

import fr.phoenixdevt.profiles.event.PendingProfileEvent;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.quests.Quest;
import lombok.Getter;
import net.Indyuce.mmocore.api.event.PlayerDataLoadEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class DataLoadListener implements Listener {

    public HashMap<Player, Boolean> isDataLoaded = new HashMap<Player, Boolean>();

    @Getter
    public static DataLoadListener instance;

    public DataLoadListener(){
        instance = this;
    }

    public void putPlayerData(Player player) {
        isDataLoaded.put(player, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        isDataLoaded.put(player, false);

    }

    @EventHandler
    public void onPlayerJoin(ProfileSelectEvent event) {
        Player player = event.getPlayer();
        isDataLoaded.put(player, true);
    }
    @EventHandler
    public void onPlayerJoin(ProfileCreateEvent event) {
        Player player = event.getPlayer();
        isDataLoaded.put(player, true);
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        Player player = e.getPlayer();
        if(e.getMessage().equals("/직업") || e.getMessage().equals("/wlrdjq") || e.getMessage().equals("/채널") || e.getMessage().equals("/cosjf")){
            return;
        }
        if(!isDataLoaded.get(player)){
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업) (/채널)");
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onCommand(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();

        if(!e.getView().getTitle().equals("직업 선택")){
            return;
        }
        if(!e.getView().getTitle().equals("채널 선택")){
            return;
        }

        if(!isDataLoaded.get(player)){
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업) (/채널)");


            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onCommand(InventoryOpenEvent e){
        Player player = (Player) e.getPlayer();

        if(e.getView().getTitle().equals("직업 선택")){
            return;
        }
        if(e.getView().getTitle().equals("채널 선택")){
            return;
        }

        if(!isDataLoaded.get(player)){
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업)");

            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onCommand(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(!isDataLoaded.get(player)){
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업)");
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        isDataLoaded.remove(event.getPlayer());
    }


}
