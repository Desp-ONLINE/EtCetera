package org.swlab.etcetera.Listener;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.quests.Quest;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.ArrayList;

public class ChestExpansionListener implements Listener {

    private String CASH_CHEST_EXPAND_ITEM_ID = "기타_캐시창고확장권";
    private String VOTE_CHEST_EXPAND_ITEM_ID = "기타_추천창고확장권";

    @EventHandler
    public void onRightClickExpansionTicket(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
        String id = MMOItems.getID(itemInMainHand);

        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (id.equals(CASH_CHEST_EXPAND_ITEM_ID)) {
                for (int i = 4; i < 9; i++) {
                    if (!player.hasPermission("mmochest." + i)) {
                        ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                        Bukkit.dispatchCommand(consoleSender, "lp user " + player.getName() + " permission set " + "mmochest." + i);
                        player.sendMessage("§a  " + i + "번 창고 확장이 완료되었습니다.");
                        if (itemInMainHand.getAmount() > 1) {
                            itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
                        } else {
                            itemInMainHand.setAmount(0);
                        }
                        return;
                    }
                }
                player.sendMessage("§c 이미 모든 창고를 확장했습니다!");
                return;

            }
            if (id.equals(VOTE_CHEST_EXPAND_ITEM_ID)) {
                if(!player.hasPermission("mmochest.3")){
                    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
                    Bukkit.dispatchCommand(consoleSender, "lp user " + player.getName() + " permission set " + "mmochest." + 3);
                    player.sendMessage("§a   3번 창고 확장이 완료되었습니다.");
                    if (itemInMainHand.getAmount() > 1) {
                        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
                    } else {
                        itemInMainHand.setAmount(0);
                    }
                    return;
                }
                player.sendMessage("§c 이미 3번 창고를 확장하셨습니다.");
                return;
            }
        }
    }
}
