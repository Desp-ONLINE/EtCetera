package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.swlab.etcetera.Convinience.ClassSelectGui;
import org.swlab.etcetera.Util.CommandUtil;

/**
 * 직업 선택 GUI 클릭 처리.
 */
public class ClassSelectListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ClassSelectGui gui)) {
            return;
        }
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        // 하단(플레이어) 인벤토리 클릭은 무시
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getInventory())) {
            return;
        }
        if (!e.isLeftClick() || e.isShiftClick()) {
            return;
        }

        int slot = e.getRawSlot();
        ClassSelectGui.ClassEntry entry = gui.getClassEntry(slot);
        if (entry == null && slot != ClassSelectGui.GUIDE_SLOT) {
            return;
        }
        if (!gui.tryClick()) {
            return;
        }

        if (slot == ClassSelectGui.GUIDE_SLOT) {
            CommandUtil.runCommandAsOP(player, "공략");
            return;
        }

        if (entry.getPermission() != null && !player.hasPermission(entry.getPermission())) {
            player.sendMessage("§c히든 퀘스트를 클리어 하셔야 전직이 가능합니다! 디스코드 공략 글에서 확인하실 수 있습니다.");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mmocore admin class " + player.getName() + " " + entry.getClassName());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof ClassSelectGui) {
            e.setCancelled(true);
        }
    }
}
