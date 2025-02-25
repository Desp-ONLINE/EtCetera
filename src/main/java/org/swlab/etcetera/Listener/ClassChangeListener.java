package org.swlab.etcetera.Listener;

import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.skytasul.quests.BeautyQuests;
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


public class ClassChangeListener implements Listener{
    @EventHandler
    public void onLevelUp(PlayerChangeClassEvent e){
        Player player = e.getPlayer();
        PlayerClass newClass = e.getNewClass();
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + newClass.getName() + "0");
        ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
        player.getInventory().addItem(basicArmor);
        player.getInventory().addItem(basicWeapon);
        mmoCoreAPI.getPlayerData(player).setClassPoints(999);
    }
    @EventHandler
    public void onProfileCreate(QuestFinishEvent e){
        if(e.getQuest().getId() == 1){
            Player player = e.getPlayer();
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerClass profess = mmoCoreAPI.getPlayerData(player).getProfess();
            String className = profess.getName();
            ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + className + "0");
            ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
            player.getInventory().addItem(basicArmor);
            player.getInventory().addItem(basicWeapon);
            mmoCoreAPI.getPlayerData(player).setClassPoints(999);
        }
    }
}
