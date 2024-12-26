package org.swlab.etcetera.Listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.swlab.etcetera.EtCetera;

public class MobListener implements Listener {
    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getPluginInstance());
        if(e.getEntity().getKiller() == null){
            return;
        }
        PlayerData playerData = mmoCoreAPI.getPlayerData(e.getEntity().getKiller());

        int playerLevel = playerData.getLevel();
        MythicBukkit inst = MythicBukkit.inst();
        MobExecutor mobManager = inst.getMobManager();
        ActiveMob mythicMobInstance = mobManager.getMythicMobInstance(e.getEntity());
        if(mythicMobInstance == null){
            return;
        }
        int monsterLevel = (int) mythicMobInstance.getLevel();


        System.out.println(playerLevel);
        System.out.println(monsterLevel);

        playerData.giveExperience(1, EXPSource.SOURCE);
        // 레벨 차가 6 이상이면 경험치를 지급하지 않음 !
    }
}
