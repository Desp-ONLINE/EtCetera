package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.EconomyManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.skills.variables.Variable;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.LivingEntity;
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
import java.util.UUID;

public class MimicListener implements Listener {


    @EventHandler
    public void onMimicDeath(MythicMobDeathEvent e){
//        String internalName = e.getMobType().getInternalName();
//        if(internalName.contains("미믹")){
//            String village = internalName.replace("미믹_","");
//            LivingEntity killer = e.getKiller();
//            if(killer instanceof Player player){
//                UUID uniqueId = player.getUniqueId();
//
//            }
//        }
    }

//    @EventHandler
//    public void onMimicDamaged(MythicDamageEvent e){
//        SkillCaster caster = e.getCaster();
//        if(caster.getEntity().isPlayer()){
//            caster.getEntity()
//        }
//        AbstractEntity target = e.getTarget();
//        MythicBukkit.inst().getMobManager().getActiveMob(target.getUniqueId()).ifPresent(mob -> {
//            Variable mobowner = mob.getVariables().get("mobowner");
//            System.out.println("mobowner = " + mobowner.get());
//        });
//    }

}
