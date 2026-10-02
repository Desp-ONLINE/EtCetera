package org.swlab.etcetera.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.swlab.etcetera.DungeonChannel.DungeonCatalog;
import org.swlab.etcetera.DungeonChannel.DungeonChannelGui;
import org.swlab.etcetera.DungeonChannel.DungeonChannelManager;
import org.swlab.etcetera.DungeonChannel.DungeonTeleportGui;
import org.swlab.etcetera.Util.CommandUtil;

/**
 * 던전 채널 선택 / 채널별 사냥터 텔레포트 GUI 클릭 처리.
 */
public class DungeonChannelListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof DungeonChannelGui) && !(holder instanceof DungeonTeleportGui)) {
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

        if (holder instanceof DungeonChannelGui gui) {
            onChannelClick(player, gui, e.getRawSlot());
        } else {
            onTeleportClick(player, (DungeonTeleportGui) holder, e.getRawSlot());
        }
    }

    private void onChannelClick(Player player, DungeonChannelGui gui, int slot) {
        int channel = gui.getChannel(slot);
        if (channel < 0 || !gui.tryClick()) {
            return;
        }
        if (!isOnline(channel)) {
            player.sendMessage("§c 던전 " + channel + "채널은 현재 접속할 수 없습니다.");
            return;
        }
        if (gui.getMode() == DungeonChannelGui.Mode.TELEPORT) {
            DungeonTeleportGui.open(player, channel);
            return;
        }
        CommandUtil.runCommandAsOP(player, "채널 워프 dungeon" + channel + " 워프 이동 얕은숲_입구");
    }

    private void onTeleportClick(Player player, DungeonTeleportGui gui, int slot) {
        DungeonCatalog.DungeonEntry entry = gui.getEntry(slot);
        if (entry == null || !gui.tryClick()) {
            return;
        }
        if (gui.isDisabled(entry)) {
            player.sendMessage("§c 이 채널에서는 입장할 수 없는 사냥터입니다.");
            return;
        }
        if (!isOnline(gui.getChannel())) {
            player.sendMessage("§c 던전 " + gui.getChannel() + "채널은 현재 접속할 수 없습니다.");
            return;
        }
        // 레벨 제한, 순간이동 쿨타임은 TeleportCommand 에서 처리
        CommandUtil.runCommandAsOP(player, "텔레포트 " + entry.number() + " " + gui.getChannel());
    }

    private boolean isOnline(int channel) {
        DungeonChannelManager manager = DungeonChannelManager.getInstance();
        return manager != null && manager.getStatus(channel).online();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof DungeonChannelGui || holder instanceof DungeonTeleportGui) {
            e.setCancelled(true);
        }
    }
}
