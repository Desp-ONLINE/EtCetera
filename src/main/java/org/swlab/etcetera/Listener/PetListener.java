package org.swlab.etcetera.Listener;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PetListener implements Listener {
    @EventHandler
    public void onPetKill(PlayerInteractAtEntityEvent e){
        if(e.getPlayer().getName().equals("dople_L")){
            Pet activePet = MCPetsAPI.getActivePet(e.getPlayer().getUniqueId());
            System.out.println("activePet.getMythicMobName() = " + activePet.getMythicMobName());            
        }
    }
}