package org.swlab.etcetera.Listener;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Convinience.ItemSearchGui;

/**
 * /템 검색 GUI 클릭 처리. 클릭한 아이템을 새로 생성해 지급한다.
 */
public class ItemSearchListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ItemSearchGui gui)) {
            return;
        }
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        // GUI 를 연 뒤 OP 가 해제된 경우 대비
        if (!player.isOp()) {
            player.closeInventory();
            return;
        }
        // 하단(플레이어) 인벤토리 클릭은 무시
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getInventory())) {
            return;
        }
        if (!e.isLeftClick()) {
            return;
        }

        int slot = e.getRawSlot();
        if (slot == ItemSearchGui.PREV_SLOT) {
            gui.prevPage();
            return;
        }
        if (slot == ItemSearchGui.NEXT_SLOT) {
            gui.nextPage();
            return;
        }

        ItemSearchGui.Entry entry = gui.getEntry(slot);
        if (entry == null) {
            return;
        }

        // 아이콘에는 식별용 로어가 붙어 있으므로 지급용은 새로 생성한다.
        ItemStack item = MMOItems.plugin.getItem(entry.type(), entry.id());
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§c아이템 생성에 실패했습니다: " + entry.type().getId() + " " + entry.id());
            return;
        }
        if (e.isShiftClick()) {
            item.setAmount(Math.min(64, item.getMaxStackSize()));
        }
        int amount = item.getAmount();

        // 인벤토리가 가득 차면 남은 수량은 발밑에 드롭
        for (ItemStack leftover : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
        player.sendMessage("§a[지급] §f" + entry.type().getId() + " " + entry.id() + " §7x" + amount);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof ItemSearchGui) {
            e.setCancelled(true);
        }
    }
}
