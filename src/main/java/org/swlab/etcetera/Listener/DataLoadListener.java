package org.swlab.etcetera.Listener;

import com.binggre.mmochest.objects.Chest;
import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.profiles.event.PendingProfileEvent;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.quests.Quest;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import lombok.Getter;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerDataLoadEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class DataLoadListener implements Listener {

    public HashMap<Player, Boolean> isDataLoaded = new HashMap<Player, Boolean>();

    @Getter
    public static DataLoadListener instance;

    public DataLoadListener() {
        instance = this;
    }

    public void putPlayerData(Player player) {
        isDataLoaded.put(player, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        isDataLoaded.put(player, false);

//        ProfileListImpl playerData = MMOProfiles.plugin.getPlayerData(player.getUniqueId());
//        if (!playerData.getProfiles().isEmpty()) {
//            playerData.applyProfile((PlayerProfileImpl)playerData.getProfiles().get(0));
//        }

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

    public int getPlayerLevel(Player player) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        return playerData.getLevel();
    }

    public boolean validCheck(Player player) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerData playerData = mmoCoreAPI.getPlayerData(player);
        if(playerData.getProfess().getName().equalsIgnoreCase("HUMAN")){
            return false;
        }
        return !isDataLoaded.get(player);
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (e.getMessage().equals("/직업") || e.getMessage().equals("/wlrdjq") || e.getMessage().equals("/채널") || e.getMessage().equals("/cosjf")) {
            return;
        }

        if (e.getMessage().equals("/창고") || e.getMessage().equals("/ckdrh") || e.getMessage().equals("/cr") || e.getMessage().equals("/ㅊㄱ")) {
            if (getPlayerLevel(player) <= 1) {
                e.setCancelled(true);
                player.sendMessage("§c 1레벨인 경우 창고를 이용하실 수 없습니다.");
                return;
            }
        }

        if (validCheck(player)) {
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업) (/채널)");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("직업 선택")) {
            return;
        }
        if (e.getView().getTitle().equals("채널 선택")) {
            return;
        }
//        if(e.getClickedInventory().getHolder() instanceof Chest){
//            if(getPlayerLevel(player) <= 1){
//                e.setCancelled(true);
//                player.sendMessage("§c 1레벨인 경우 창고를 이용하실 수 없습니다.");
//                return;
//            }
//        }
        if (validCheck(player)) {
            if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                e.setCancelled(true);
                player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업) (/채널)");
            }
        }
    }

    @EventHandler
    public void onCommand(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

        if (e.getView().getTitle().equals("직업 선택")) {
            return;
        }
        if (e.getView().getTitle().equals("채널 선택")) {
            return;
        }
        if (e.getView().getTitle().equals("창고")) {
            if (getPlayerLevel(player) <= 1) {
                e.setCancelled(true);
                player.sendMessage("§c 1레벨인 경우 창고를 이용하실 수 없습니다.");
                return;
            }
        }

        if (validCheck(player)) {
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업)");

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (validCheck(player)) {
            player.sendMessage("§c 데이터가 로드 중이거나 로드에 실패했거나, 직업이 없습니다. 직업을 선택하시거나, 채널을 이동 해주세요. (/직업)");
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        isDataLoaded.remove(event.getPlayer());
    }


}
