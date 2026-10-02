package org.swlab.etcetera.DungeonChannel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 채널별 사냥터 텔레포트 GUI. 기존 GUI 플러그인의 "텔레포트1~8" 메뉴를 옮기고
 * Area 플러그인 기준으로 각 사냥터 안의 인원 수를 표시한다. 클릭 시 /텔레포트 <던전> <채널> 실행.
 */
public class DungeonTeleportGui implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int[] FILLER_SLOTS = {0, 4, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35, 36, 40, 44, 45, 49, 53};

    private final int channel;
    private final Map<Integer, DungeonCatalog.DungeonEntry> slotToEntry = new HashMap<>();
    private Inventory inventory;
    private long lastClick;

    private DungeonTeleportGui(int channel) {
        this.channel = channel;
        for (DungeonCatalog.DungeonEntry entry : DungeonCatalog.getEntries()) {
            slotToEntry.put(entry.slot(), entry);
        }
    }

    public static void open(Player player, int channel) {
        player.openInventory(new DungeonTeleportGui(channel).getInventory());
    }

    public int getChannel() {
        return channel;
    }

    public DungeonCatalog.DungeonEntry getEntry(int slot) {
        return slotToEntry.get(slot);
    }

    /** 이 채널에서 막혀 있는 사냥터인지 (config: dungeonChannel.disabledDungeons.<채널>) */
    public boolean isDisabled(DungeonCatalog.DungeonEntry entry) {
        List<Integer> disabled = EtCetera.getInstance().getConfig().getIntegerList("dungeonChannel.disabledDungeons." + channel);
        return disabled.contains(entry.number());
    }

    /** 연타 방지 (200ms). 클릭을 처리해도 되면 true. */
    public boolean tryClick() {
        long now = System.currentTimeMillis();
        if (now - lastClick < 200) {
            return false;
        }
        lastClick = now;
        return true;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }
        inventory = Bukkit.createInventory(this, SIZE, "§f던전 " + channel + "채널 텔레포트");
        ItemStack filler = DungeonChannelGui.createFiller();
        for (int slot : FILLER_SLOTS) {
            inventory.setItem(slot, filler);
        }
        render();
        return inventory;
    }

    /** 사냥터 아이콘을 최신 인원 수로 다시 그린다. */
    public void render() {
        if (inventory == null) {
            return;
        }
        DungeonChannelManager manager = DungeonChannelManager.getInstance();
        ChannelStatus status = manager == null ? ChannelStatus.offline(channel) : manager.getStatus(channel);
        for (DungeonCatalog.DungeonEntry entry : DungeonCatalog.getEntries()) {
            inventory.setItem(entry.slot(), buildDungeonItem(entry, status));
        }
    }

    private ItemStack buildDungeonItem(DungeonCatalog.DungeonEntry entry, ChannelStatus status) {
        ItemStack item = new ItemStack(entry.material());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(entry.displayName());

        List<String> lore = new ArrayList<>(entry.lore());
        lore.add("");
        if (isDisabled(entry)) {
            lore.add(" §c이 채널에서는 입장할 수 없는 사냥터입니다.");
        } else if (!status.online()) {
            lore.add(" §c● §f" + channel + "채널이 닫혀 있어 입장할 수 없습니다.");
        } else {
            lore.add(" §a● §f현재 사냥 중인 인원 §e" + status.areaCount(entry.areaName()) + "§f명");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
