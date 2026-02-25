package org.swlab.etcetera.Listener;

import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.MMOInventoryAPI;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.inventory.slot.CustomSlot;
import net.Indyuce.inventory.inventory.slot.SlotType;
import net.Indyuce.inventory.player.CustomInventoryData;
import net.Indyuce.inventory.player.InventoryItem;
import net.Indyuce.inventory.player.PlayerData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.Collection;
import java.util.Set;

public class EquipListener implements Listener {
    @EventHandler
    public void onEquipChestplate(ItemEquipEvent e) {


        Player player = e.getPlayer();

        ItemStack item = e.getItem();
        String mmoItemID = MMOItems.getID(item);
        String mmoItemType = MMOItems.getTypeName(item);
        if (!validSameTypeItem(player,mmoItemType, mmoItemID)) {
            player.sendMessage("§c 동일한 종류의 장비를 장착하실 수 없습니다.");
            e.setCancelled(true);
            return;
        }
        if (e.getSlot() == null) {
            e.setCancelled(true);
            player.sendMessage("§c 쉬프트+클릭을 통한 탈착은 금지되어 있습니다.");
            return;

        }


        if (e.getSlot().getType().equals(SlotType.BOOTS) || e.getSlot().getType().equals(SlotType.LEGGINGS)) {
            return;
        }
        player.playSound(player, "uisounds:itemequip", 1.0F, 1.0F);
        if (e.getSlot().getType().equals(SlotType.CHESTPLATE)) {
            if (item == null) {
                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                player.getInventory().setBoots(new ItemStack(Material.AIR));
                return;
            }
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (meta == null) {
                return;
            }

            int customModelData = meta.getCustomModelData();
            Color color = meta.getColor();
            if (player.getInventory().getBoots() == null) {
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
                bootsMeta.setColor(color);
                bootsMeta.setCustomModelData(customModelData);
                bootsMeta.setUnbreakable(true);
                boots.setItemMeta(bootsMeta);
                player.getInventory().setBoots(boots);
            }
            if (player.getInventory().getLeggings() == null) {
                ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
                leggingsMeta.setColor(color);
                leggingsMeta.setCustomModelData(customModelData);
                leggingsMeta.setUnbreakable(true);
                leggings.setItemMeta(leggingsMeta);
                player.getInventory().setLeggings(leggings);
            }
        }

        if (e.getInventory().getId().equals("ascend_inventory")) {
            String id = MMOItems.getID(item);
            String slotID = e.getSlot().getId();
            System.out.println(slotID);
            if (slotID.equals("apocalypse2") && id.contains("성배")) {
                e.setCancelled(true);
                player.sendMessage("§c 이 슬롯에는 아포칼립스 성배를 장착하실 수 없습니다.");
                return;
            }
            if (slotID.equals("apocalypse3") && (id.contains("성배") || id.contains("로브"))) {
                e.setCancelled(true);
                player.sendMessage("§c 이 슬롯에는 아포칼립스 성배 또는 아포칼립스 로브를 장착하실 수 없습니다.");
                return;
            }
        }

    }

    public boolean validSameTypeItem(Player player,String mmoItemType, String mmoItemID) {
        PlayerData playerData = MMOInventory.plugin.getDataManager().get(player);

        Collection<CustomInventoryData> allCustom = playerData.getAllCustom();

        ItemStack toEquipItem = MMOItems.plugin.getItem(mmoItemType, mmoItemID);

        if(toEquipItem == null){
            return true;
        }


        for (CustomInventoryData customInventoryData : allCustom) {
            for (InventoryItem inventoryItem : customInventoryData.getItems()) {
                ItemStack equippedItem = inventoryItem.getItemStack();

                if (toEquipItem.getType().equals(equippedItem.getType()) && toEquipItem.getItemMeta().getCustomModelData() == equippedItem.getItemMeta().getCustomModelData()) {
                    return false;
                }
            }

        }
        return true;
    }

//    @EventHandler
//    public void disableUnequipLeggingsAndBoots(InventoryClickEvent e) {
//        Player player = (Player) e.getWhoClicked();
//        if (e.getView().getType().equals(InventoryType.CRAFTING)) {
//            if (e.getSlot() >= 36 && e.getSlot() <= 39) {
//                e.setCancelled(true);
//                player.sendMessage("§c 장비창을 통한 장/탈착만 가능합니다.");
//                CommandUtil.runCommandAsOP(player, "mmoinventory");
//            }
//        }
//
//
//        if (e.getView().getType() == InventoryType.CRAFTING) {
//            if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
//                ItemStack currentItem = e.getCurrentItem();
//                if (currentItem != null && isArmor(currentItem.getType())) {
//                    e.setCancelled(true);
//                    player.sendMessage("§c SHIFT+클릭을 통한 장착은 불가능합니다.");
//                }
//            }
//        }
//    }


}
