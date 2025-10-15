package org.swlab.etcetera.Listener;

import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.dople.petCollection.PetCollectionAPI;

import java.util.ArrayList;
import java.util.Arrays;

public class PetListener implements Listener {
    @EventHandler
    public void onPetFeed(PlayerInteractEvent e) {

        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ArrayList<String> rankB = new ArrayList<>(Arrays.asList("am_pet_gemling", "am_pet_beepu", "am_pet_skel", "cubee-armadillo","cubee-icy_wisp" ));
        ArrayList<String> rankA = new ArrayList<>(Arrays.asList("cubee-kitsu", "cubee-kodama", "cubee-soul_lantern", "cubee-tortoise", "cubee-hex_wizard"));
        ArrayList<String> rankS = new ArrayList<>(Arrays.asList("cubee-oni_mask", "cubee-tengu", "cubee-willow_wisp", "cubee-yokai_neko", "cubee-dino", "cubee-dragon_junior", "cubee-hedgehog", "cubee-protector_golem", "mini_bingle", "mini_dople","mini_sorim"));
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = e.getPlayer();
            ItemStack feedItem = player.getInventory().getItemInMainHand();
            String id = MMOItems.getID(feedItem);

            Pet activePet = null;
            String petID = null;

            switch (id) {

                case "사료_비독성이끼":
//                    if(!player.getName().equals("dople_L")){
//                        player.sendMessage("§c 점검중입니다.");
//                        return;
//                    }
                    activePet = MCPetsAPI.getActivePet(player.getUniqueId());
                    if (activePet == null) {
                        player.sendMessage("§c 펫을 소환하고 사용해주세요.");
                        return;
                    }
                    petID = activePet.getId();
                    if (!rankB.contains(petID)) {
                        player.sendMessage("§c 해당 등급 펫이 먹을 수 있는 사료가 아닙니다!");
                        return;
                    }
                    player.getInventory().getItemInMainHand().setAmount(feedItem.getAmount() - 1);
                    PetCollectionAPI.getPlayerRepository().addPetExp(player, petID, 1000);
                    return;
                case "사료_가공육":
//                    if(!player.getName().equals("dople_L")){
//                        player.sendMessage("§c 점검중입니다.");
//                        return;
//                    }
                    activePet = MCPetsAPI.getActivePet(player.getUniqueId());
                    if (activePet == null) {
                        player.sendMessage("§c 펫을 소환하고 사용해주세요.");
                        return;
                    }
                    petID = activePet.getId();
                    if (!rankA.contains(petID)) {
                        player.sendMessage("§c 해당 등급 펫이 먹을 수 있는 사료가 아닙니다!");
                        return;
                    }
                    player.getInventory().getItemInMainHand().setAmount(feedItem.getAmount() - 1);
                    PetCollectionAPI.getPlayerRepository().addPetExp(player, petID, 1000);
                    return;
                case "사료_황금달걀":
//                    if(!player.getName().equals("dople_L")){
//                        player.sendMessage("§c 점검중입니다.");
//                        return;
//                    }
                    activePet = MCPetsAPI.getActivePet(player.getUniqueId());
                    if (activePet == null) {
                        player.sendMessage("§c 펫을 소환하고 사용해주세요.");
                        return;
                    }
                    petID = activePet.getId();
                    if (!rankS.contains(petID)) {
                        player.sendMessage("§c 해당 등급 펫이 먹을 수 있는 사료가 아닙니다!");
                        return;
                    }
                    player.getInventory().getItemInMainHand().setAmount(feedItem.getAmount() - 1);
                    PetCollectionAPI.getPlayerRepository().addPetExp(player, petID, 1000);
                    return;
                default:
                    return;
            }
        }

    }
}