package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.EconomyManager;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.dople.dataSync.inventory.InventorySyncListener;
import org.swlab.etcetera.EtCetera;

import java.text.NumberFormat;
import java.util.Random;

public class GoldItemListener implements Listener {

    private static Random random = new Random();


    @EventHandler
    public void onMidasHand(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(!(e.getHand() == EquipmentSlot.HAND)) {
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!MMOItems.getID(item).equals("기타_미다스의손")){
            return;
        }
        if(!EtCetera.getChannelType().equals("lobby")){
            player.sendMessage("§c 로비에서만 사용하실 수 있습니다.");
            return;
        }
        if(InventorySyncListener.isDataLoading(player)){
            player.sendMessage("§c 데이터가 로드중입니다.");
            return;
        }
        int goldRange = random.nextInt(1, 101);
        int goldReward = 0;
        if(goldRange <= 65){
            goldReward = random.nextInt(100000, 200001);
            player.playSound(player, "uisounds:purchase1", 1, 1);

        } else if (goldRange <= 80){
            goldReward = random.nextInt(210000, 350001);
            player.playSound(player, "uisounds:purchase1", 1, 1);

        } else if (goldRange <= 92){
            goldReward = random.nextInt(360000, 700001);
            player.playSound(player, "uisounds:purchase1", 1, 1);

        } else if (goldRange <= 97) {
            goldReward = random.nextInt(710000, 900001);
            player.playSound(player, "uisounds:purchase1", 1, 1);

        } else if (goldRange <= 99) {
            goldReward = random.nextInt(910000, 1100001);
            player.playSound(player, "uisounds:congratulations", 1, 1);

        } else{
            goldReward = random.nextInt(1110000, 1500001);
            player.playSound(player, "uisounds:congratulations", 1, 1);

        }
        EconomyManager.addMoney(player, goldReward);
        String format = ColorManager.format("#FFD900 [미다스의 손] §f축하합니다! §6" + NumberFormat.getIntegerInstance().format(goldReward) + " 골드 §f를 획득했습니다!");
        player.sendMessage(format);
        item.setAmount(item.getAmount() - 1);
    }


}
