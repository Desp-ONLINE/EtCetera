package org.swlab.etcetera.Placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Listener.LeapListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class CooldownPlaceholder extends PlaceholderExpansion {

    private final EtCetera etCetera;

    public CooldownPlaceholder(EtCetera etCetera) {
        this.etCetera = etCetera;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "CooldownPlaceholder";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dople";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        final String string = identifier.split("_")[0];
        if(Objects.equals(string, "cooldown")) {
            return getCooldownString(player);
        }
        if(Objects.equals(string, "leap")){
            if(!player.getName().equals("dople_L")){
                return "";
            }
            if(LeapListener.getInstance().isCooldown(player.getUniqueId())){
                return "0";
            }
            return "1";
        }
        return "1";
    }


    public String getCooldownString(Player player){
        if(EtCetera.getChannelType().equals("lobby")){
            if(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("fishing")){
                return "";
            }
        }
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        String id = MMOItems.getID(itemInMainHand);
        net.Indyuce.mmoitems.api.Type type = MMOItems.getType(itemInMainHand);
        if(type == null || itemInMainHand.equals( Material.AIR)){
            return "";
        }
        if(!type.getId().equals("SWORD")){
            return "";
        }
        StatData data = Objects.requireNonNull(MMOItems.plugin.getMMOItem(type, id)).getData(ItemStats.ABILITIES);
        AbilityListData data2 = (AbilityListData) data;


        StringBuilder stringBuilder = new StringBuilder();
        if(data2 == null){
            return "";
        }
        Iterator<AbilityData> iterator = data2.getAbilities().iterator();

        while (iterator.hasNext()) {
            AbilityData ability = iterator.next();
            String originTypeName = ability.getTrigger().getName();
            String key = "";
            switch (originTypeName) {
                case "Left Click":
                    key = "L";
                    break;
                case "Right Click":
                    key = "R";
                    break;
                case "Shift Right Click":
                    key = "S+R";
                    break;
                case "Shift Left Click":
                    key = "S+L";
                    break;
                case "Drop Item":
                    key = "Q";
                    break;
            }
            String name = ability.getAbility().getName();
            String replacedName = name.replace(" ", "_").toLowerCase();
            String cooldown = PlaceholderAPI.setPlaceholders(player, "%mythiclib_cooldown_skill_" + replacedName + "%");
            stringBuilder.append("<#FFC233>"+key + "<#FFFFFF>: <#10FF5D>" + cooldown + "<#FFFFFF>초");
            if(iterator.hasNext()){
                stringBuilder.append(" | ");
            }
        }
        return(stringBuilder.toString()).replace("§", "&");

    }
}
