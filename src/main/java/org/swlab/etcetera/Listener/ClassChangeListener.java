package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;


public class ClassChangeListener implements Listener {

    @EventHandler
    public void onLevelUp(PlayerChangeClassEvent e) {
        Player player = e.getPlayer();
        PlayerClass newClass = e.getNewClass();


        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        // 이벤트는 직업 변경 적용 전에 발생하며, 처음 플레이하는 직업은 저장된 정보가 없어 null 반환
        SavedClassInformation classInfo = mmoCoreAPI.getPlayerData(player).getClassInfo(newClass);
        int level = classInfo == null ? 1 : classInfo.getLevel();
        if (level == 1) {
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    CommandUtil.runCommandAsOP(player, "기본템 1");
                    mmoCoreAPI.getPlayerData(player).setClassPoints(999);
                }
            }, 20L);
        }


    }

    /** 퀘스트 완료 공통 처리. IDEQuestListener 가 호출한다. */
    public static void handleQuestFinish(Player player, int id, String questName) {
        if (id == 1) {
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            PlayerClass profess = mmoCoreAPI.getPlayerData(player).getProfess();
            String className = profess.getName();
            ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + className + "0");
            ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
            player.getInventory().addItem(basicArmor);
            player.getInventory().addItem(basicWeapon);
            mmoCoreAPI.getPlayerData(player).setClassPoints(999);
        }
        // 전직 퀘스트 클리어 시 해당 차수 기본템 자동 지급 (90000~: 2차, 90010~: 3차, 90020~: 4차, 90030~: 각성)
        if (id >= 90000 && id < 90040) {
            String tier = switch ((id - 90000) / 10) {
                case 0 -> "2";
                case 1 -> "3";
                case 2 -> "4";
                default -> "각성";
            };
            player.sendMessage(ColorManager.format("§6 [전직] #93FFA3 전직을 축하합니다! 잠시 후 기본템이 자동 지급됩니다. 분실 시 §f/기본템 " + tier + "#93FFA3 명령어로 언제든지 다시 받을 수 있습니다."));
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    CommandUtil.runCommandAsOP(player, "기본템 " + tier);
                }
            }, 20L);
        }
        if (id >= 30000 && id < 40000) {
            player.sendMessage(ColorManager.format("§6 [편의성] #93FFA3 5초 뒤, 자동으로 서브퀘스트 §f" + questName + "#93FFA3 를 수령합니다. 수령 전 채널을 옮기거나 하는 경우 직접 수령이 필요합니다."));
            // 다음 서브퀘스트를 수령시킨다.
            String startCommand = "questadmin start " + player.getName() + " " + id;
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), startCommand);
                }
            }, 100L);
        }

    }
}
