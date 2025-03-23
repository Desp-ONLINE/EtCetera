package org.swlab.etcetera.Listener;

import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.slot.SlotType;
import org.bukkit.Color;
import org.bukkit.Material;
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
import org.swlab.etcetera.Util.CommandUtil;

public class EquipListener implements Listener {
    @EventHandler
    public void onEquipChestplate(ItemEquipEvent e) {
        if (e.getSlot() == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c 쉬프트+클릭을 통한 탈착은 금지되어 있습니다.");
            return;

        }
        if (e.getSlot().getType().equals(SlotType.BOOTS) || e.getSlot().getType().equals(SlotType.LEGGINGS)) {
            return;
        }
        if (e.getSlot().getType().equals(SlotType.CHESTPLATE)) {
            ItemStack item = e.getItem();
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (meta == null) {
                e.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
                return;
            }
            int customModelData = meta.getCustomModelData();
            Color color = meta.getColor();
            if (e.getPlayer().getInventory().getBoots() == null) {
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
                bootsMeta.setColor(color);
                bootsMeta.setCustomModelData(customModelData);
                bootsMeta.setUnbreakable(true);
                boots.setItemMeta(bootsMeta);
                e.getPlayer().getInventory().setBoots(boots);
            }
            if (e.getPlayer().getInventory().getLeggings() == null) {
                ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
                leggingsMeta.setColor(color);
                leggingsMeta.setCustomModelData(customModelData);
                leggingsMeta.setUnbreakable(true);
                leggings.setItemMeta(leggingsMeta);
                e.getPlayer().getInventory().setLeggings(leggings);
            }
        }
    }

    @EventHandler
    public void disableUnequipLeggingsAndBoots(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getType().equals(InventoryType.CRAFTING)) {
            if (e.getSlot() >= 36 && e.getSlot() <= 39) {
                e.setCancelled(true);
                player.sendMessage("§c 장비창을 통한 장/탈착만 가능합니다.");
                CommandUtil.runCommandAsOP(player, "mmoinventory");
            }
        }


        if (e.getView().getType() == InventoryType.CRAFTING) {
            if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
                ItemStack currentItem = e.getCurrentItem();
                if (currentItem != null && isArmor(currentItem.getType())) {
                    e.setCancelled(true);
                    player.sendMessage("§c SHIFT+클릭을 통한 장착은 불가능합니다.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        // 손에 아이템이 없거나, 왼손 사용 이벤트라면 무시
        if (item == null || event.getHand() == EquipmentSlot.OFF_HAND) return;

        // 우클릭으로 갑옷 장착 시도 감지
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (isArmor(item.getType())) {
                event.setCancelled(true);
                player.sendMessage("§c 장비창을 통한 장착만 가능합니다.");
                CommandUtil.runCommandAsOP(player, "mmoinventory");
            }
        }
    }

    // 갑옷인지 확인하는 함수
    private boolean isArmor(Material material) {
        return material.name().endsWith("_HELMET") ||
                material.name().endsWith("_CHESTPLATE") ||
                material.name().endsWith("_LEGGINGS") ||
                material.name().endsWith("_BOOTS");
    }
}
