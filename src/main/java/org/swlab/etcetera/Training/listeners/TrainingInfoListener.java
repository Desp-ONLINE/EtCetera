package org.swlab.etcetera.Training.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.swlab.etcetera.Training.gui.TrainingInfoGui;

/**
 * 관리자용 훈련 정보 GUI 조작 처리. 열람 전용이라 페이지 이동 외 모든 클릭을 막는다.
 */
public class TrainingInfoListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof TrainingInfoGui gui)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getInventory())) {
            return;
        }

        int slot = event.getSlot();
        if (slot == TrainingInfoGui.PREV_SLOT && gui.getPage() > 0) {
            TrainingInfoGui.open(player, gui.getPage() - 1);
        } else if (slot == TrainingInfoGui.NEXT_SLOT && gui.getPage() < gui.getMaxPage()) {
            TrainingInfoGui.open(player, gui.getPage() + 1);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof TrainingInfoGui) {
            event.setCancelled(true);
        }
    }
}
