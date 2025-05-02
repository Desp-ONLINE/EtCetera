package org.swlab.etcetera.Listener;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class PetListener implements Listener {
    @EventHandler
    public void onPetFeed(PlayerInteractEvent e) {

        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ArrayList<String> rankB = new ArrayList<>(Arrays.asList("am_pet_gemling", "am_pet_beepu", "am_pet_skel"));
        ArrayList<String> rankA = new ArrayList<>(Arrays.asList("cubee-kitsu", "cubee-kodama", "cubee-soul_lantern"));
        ArrayList<String> rankS = new ArrayList<>(Arrays.asList("cubee-oni_mask", "cubee-tengu", "cubee-willow_wisp", "cubee-yokai_neko"));
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = e.getPlayer();
            ItemStack feedItem = player.getInventory().getItemInMainHand();
            String id = MMOItems.getID(feedItem);

            Pet activePet = null;
            String petID = null;
            switch (id) {
                case "사료_비독성이끼":
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
                    player.sendMessage("§a 사료를 먹은 펫이 기뻐합니다!");
                    activePet.getPetStats().addExperience(1000);
                    return;
                case "사료_가공육":
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
                    player.sendMessage("§a 사료를 먹은 펫이 기뻐합니다!");
                    activePet.getPetStats().addExperience(1000);
                    return;
                case "사료_황금달걀":
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
                    player.sendMessage("§a 사료를 먹은 펫이 기뻐합니다!");
                    activePet.getPetStats().addExperience(1000);
                    return;
                default:
                    return;
            }
        }

    }
}