package org.swlab.etcetera.Listener;

import fr.nocsy.mcpets.api.MCPetsAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PetListener implements Listener {
    @EventHandler
    public void onPetKill(MythicMobDeathEvent e){
        LivingEntity killer = e.getKiller();
        ActiveMob mythicMobInstance = MythicBukkit.inst().getMobManager().getMythicMobInstance(killer);
//        MCPetsAPI.getAvailablePets()
    }
}