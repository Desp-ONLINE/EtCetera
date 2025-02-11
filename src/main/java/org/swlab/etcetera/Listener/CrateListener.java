package org.swlab.etcetera.Listener;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateEvent;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class CrateListener implements Listener {

    public static ArrayList<Player> canOpen = new ArrayList<>();

    @EventHandler
    public void onCrateOpen(CrateOpenEvent e){
        Player player = e.getPlayer();
//        System.out.println("canOpen.contains(p) = " + canOpen.contains(player));
        if(canOpen.contains(player)){
            e.setCancelled(true);
            player.sendMessage("§c  5초 간격으로 오픈할 수 있습니다.");
            return;
        }
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
        }, 100L);
    }
}
