package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import de.kinglol12345.GUIPlus.events.GUIClickEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;
import org.swlab.etcetera.Util.NicknameboardUtil;

import java.text.NumberFormat;
import java.util.Random;

public class GoldItemListener implements Listener {

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
        Random random = new Random();
        int goldRange = random.nextInt(1, 101);
        int goldReward = 0;
        if(goldRange <= 65){
            goldReward = random.nextInt(100000, 200001);
        } else if (goldRange <= 80){
            goldReward = random.nextInt(210000, 350001);
        } else if (goldRange <= 92){
            goldReward = random.nextInt(360000, 700001);
        } else if (goldRange <= 97) {
            goldReward = random.nextInt(710000, 900001);
        } else if (goldRange <= 99) {
            goldReward = random.nextInt(910000, 1100001);
        } else{
            goldReward = random.nextInt(1110000, 1500001);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give "+player.getName()+" "+goldReward);
        String format = ColorManager.format("#FFD900 [미다스의 손] §f축하합니다! §6" + NumberFormat.getIntegerInstance().format(goldReward) + " 골드 §f를 획득했습니다!");
        player.sendMessage(format);
        item.setAmount(item.getAmount() - 1);
    }


}
