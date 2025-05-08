package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOItemsBukkit;
import net.Indyuce.mmoitems.api.MMOItemsAPI;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.ItemManager;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.EtCetera;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class SkillCooldownNotice {

    public static void scheduleStart() {


        Bukkit.getScheduler().runTaskTimerAsynchronously(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {

                    if(EtCetera.getChannelType().equals("lobby")){
                        if(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("fishing")){
                            return;
                        }
                    }
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    String id = MMOItems.getID(itemInMainHand);
                    Type type = MMOItems.getType(itemInMainHand);
                    if(type == null || itemInMainHand.equals( Material.AIR)){
                        return;
                    }
                    if(!type.getId().equals("SWORD")){
                        return;
                    }
                    StatData data = Objects.requireNonNull(MMOItems.plugin.getMMOItem(type, id)).getData(ItemStats.ABILITIES);
                    AbilityListData data2 = (AbilityListData) data;


                    StringBuilder stringBuilder = new StringBuilder();
                    if(data2 == null){
                        return;
                    }
                    Iterator<AbilityData> iterator = data2.getAbilities().iterator();

                    while (iterator.hasNext()) {
                        AbilityData ability = iterator.next();
                        String originTypeName = ability.getTrigger().getName();
                        String key = "";
                        switch (originTypeName) {
                            case "Left Click":
                                key = "좌클릭";
                                break;
                            case "Right Click":
                                key = "우클릭";
                                break;
                            case "Shift Right Click":
                                key = "쉬프트+우클릭";
                                break;
                            case "Shift Left Click":
                                key = "쉬프트+좌클릭";
                                break;
                            case "Shift Drop Item":
                                key = "쉬프트+버리기";
                                break;
                        }
                        String name = ability.getAbility().getName();
                        String replacedName = name.replace(" ", "_").toLowerCase();
                        String cooldown = PlaceholderAPI.setPlaceholders(player, "%mythiclib_cooldown_skill_" + replacedName + "%");
                        stringBuilder.append("§6"+key + "§f: §a" + cooldown + "§f초");
                        if(iterator.hasNext()){
                            stringBuilder.append(" | ");
                        }
                        player.sendActionBar(itemInMainHand.getItemMeta().getDisplayName()+ " §f:: " + stringBuilder.toString());
                    }
                });
            }
        },4L,4L);
    }

}

