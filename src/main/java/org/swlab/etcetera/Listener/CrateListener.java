package org.swlab.etcetera.Listener;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.quests.Quest;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;

public class CrateListener implements Listener {

    public static ArrayList<Player> canOpen = new ArrayList<>();

    public static long openDelay = 60L;

    @EventHandler
    public void onCrateOpen(CrateOpenEvent e){
        Player player = e.getPlayer();
        QuestsAPI api = QuestsAPI.getAPI();
        Quest quest = api.getQuestsManager().getQuest(10);
        if(!BeautyQuests.getInstance().getPlayersManager().getAccount(player).hasQuestDatas(quest)){
            player.sendMessage("§c  9번 메인 퀘스트를 먼저 클리어하세요!");
            e.setCancelled(true);
            return;
        }
//        System.out.println("canOpen.contains(p) = " + canOpen.contains(player));
        if(canOpen.contains(player)){
            e.setCancelled(true);
            player.sendMessage("§c  3초 간격으로 오픈할 수 있습니다.");
            return;
        }
//        System.out.println("e.getCrate().getId() = " + e.getCrate().getId());
        if(e.getCrate().getId().equals("common")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠_일반"))){
                e.setCancelled(true);
                player.sendMessage("§c  일반 등급 판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);

        }
        else if(e.getCrate().getId().equals("rare")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠_레어"))){
                e.setCancelled(true);
                player.sendMessage("§c  레어 등급 판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);

        }
        else if(e.getCrate().getId().equals("epic")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠_에픽"))){
                e.setCancelled(true);
                player.sendMessage("§c  에픽 등급 판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);

        }
        else if(e.getCrate().getId().equals("unique")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠_유니크"))){
                e.setCancelled(true);
                player.sendMessage("§c  유니크 등급 판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);

        }
        else if(e.getCrate().getId().equals("legend")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠_레전더리"))){
                e.setCancelled(true);
                player.sendMessage("§c  레전더리 등급 판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);

        }
        else if(e.getCrate().getId().equals("legendary")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_판도라의열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  판도라의 열쇠를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }
        else if(e.getCrate().getId().equals("elvenheim")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_엘븐하임열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  네이처 키를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }else if(e.getCrate().getId().equals("kalima")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_칼리마열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  샌드스톤 키를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }else if(e.getCrate().getId().equals("inferium")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_인페리움열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  라바플로우 키를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }
        else if(e.getCrate().getId().equals("epsilon")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_엡실론열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  포가튼 키를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }else if(e.getCrate().getId().equals("arctica")){
            if(!(MMOItems.getID(player.getInventory().getItemInMainHand()).equals("기타_아르크티카열쇠"))){
                e.setCancelled(true);
                player.sendMessage("§c  프로스트 키를 손에 들고 시도하세요.");
                return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount-1);
            canOpen.add(player);
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    canOpen.remove(player);
                }
            }, openDelay);
        }
        else{
            e.setCancelled(true);
        }
    }
}
