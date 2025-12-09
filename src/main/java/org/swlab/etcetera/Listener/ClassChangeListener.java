package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.events.QuestFinishEvent;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.players.PlayerAccountImplementation;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.NameTagUtil;


public class ClassChangeListener implements Listener{
    @EventHandler
    public void onLevelUp(PlayerChangeClassEvent e){
        Player player = e.getPlayer();
        PlayerClass newClass = e.getNewClass();


        Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), () -> {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            if(mmoCoreAPI.getPlayerData(player).getLevel() != 1){
                return;
            }
            ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + newClass.getName() + "0");
            ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
            player.getInventory().addItem(basicArmor);
            player.getInventory().addItem(basicWeapon);
            mmoCoreAPI.getPlayerData(player).setClassPoints(999);
            NameTagUtil.setPlayerNameTag(player);
        }, 20L);
    }
    @EventHandler
    public void onProfileCreate(QuestFinishEvent e){
        int id = e.getQuest().getId();
        if(id == 1){
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
        if(id >= 30000 && id < 40000){
            Player player = e.getPlayer();
            Quest quest = QuestsAPI.getAPI().getQuestsManager().getQuest(id);
            PlayerAccountImplementation account = BeautyQuests.getInstance().getPlayersManager().getAccount(player);
//            if(!account.hasQuestDatas(quest)){
                player.sendMessage(ColorManager.format("§6 [편의성] #93FFA3 5초 뒤, 자동으로 서브퀘스트 §f"+quest.getName()+"#93FFA3 를 수령합니다. 수령 전 채널을 옮기거나 하는 경우 직접 수령이 필요합니다."));
                Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bq start "+ player.getName()+" "+id);

                    }
                }, 100L);
//                CommandUtil.runCommandAsOP(player, );
//            }
//            System.out.println("true = " + true);
        }

    }
}
