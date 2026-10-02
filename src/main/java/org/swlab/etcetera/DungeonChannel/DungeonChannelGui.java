package org.swlab.etcetera.DungeonChannel;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.List;

/**
 * 던전 채널 선택 GUI. 기존 GUI 플러그인의 "던전" 메뉴를 옮기고 채널별 접속 가능 여부/접속자를 표시한다.
 * - MOVE: 던전 서버의 /던전. 클릭 시 해당 채널의 얕은숲 입구로 이동
 * - TELEPORT: 로비의 /텔레포트. 클릭 시 해당 채널의 사냥터 목록(DungeonTeleportGui)을 연다
 */
public class DungeonChannelGui implements InventoryHolder {

    public enum Mode {MOVE, TELEPORT}

    private static final int SIZE = 54;
    private static final String TITLE = "§x§1§2§3§5§2§4던전";
    // 채널 1~8 아이콘 위치와 리소스팩 모델 번호
    private static final int[] CHANNEL_SLOTS = {10, 12, 14, 16, 37, 39, 41, 43};
    private static final int[] CHANNEL_MODELS = {1, 11, 3, 4, 5, 6, 7, 8};
    private static final int NAMES_PER_LINE = 4;
    private static final int MAX_NAMES = 20;

    private final Mode mode;
    private Inventory inventory;
    private long lastClick;

    private DungeonChannelGui(Mode mode) {
        this.mode = mode;
    }

    public static void open(Player player, Mode mode) {
        player.openInventory(new DungeonChannelGui(mode).getInventory());
    }

    public Mode getMode() {
        return mode;
    }

    /** 슬롯에 해당하는 채널 번호. 채널 아이콘이 아니면 -1. */
    public int getChannel(int slot) {
        for (int i = 0; i < CHANNEL_SLOTS.length; i++) {
            if (CHANNEL_SLOTS[i] == slot) {
                return i + 1;
            }
        }
        return -1;
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
        inventory = Bukkit.createInventory(this, SIZE, TITLE);
        ItemStack filler = createFiller();
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }
        render();
        return inventory;
    }

    /** 채널 아이콘을 최신 상태로 다시 그린다. */
    public void render() {
        if (inventory == null) {
            return;
        }
        DungeonChannelManager manager = DungeonChannelManager.getInstance();
        for (int i = 0; i < CHANNEL_SLOTS.length; i++) {
            int channel = i + 1;
            ChannelStatus status = manager == null ? ChannelStatus.offline(channel) : manager.getStatus(channel);
            inventory.setItem(CHANNEL_SLOTS[i], buildChannelItem(status, CHANNEL_MODELS[i]));
        }
    }

    private ItemStack buildChannelItem(ChannelStatus status, int model) {
        int channel = status.channel();
        ItemStack item = new ItemStack(Material.YELLOW_DYE);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(statusDot(status.online()) + " §f던전 §3" + channel + " §f채널");

        List<String> lore = new ArrayList<>();
        lore.add("");
        if (status.online()) {
            lore.add(" §a● §f접속 가능");
            lore.add(" §f접속 인원 §e" + status.players().size() + "§f명");
            lore.addAll(playerLines(status.players()));
        } else {
            lore.add(" §c● §f접속 불가 §7(채널 닫힘)");
        }
        if (isCurrentChannel(channel)) {
            lore.add("");
            lore.add(" §e현재 접속 중인 채널입니다.");
        }
        lore.add("");
        if (mode == Mode.MOVE) {
            lore.add("§7> §f클릭 시, 던전 " + channel + "채널로 입장합니다.");
        } else {
            lore.add("§7> §f클릭 시, 던전 " + channel + "채널 사냥터 목록을 엽니다.");
        }
        meta.setLore(lore);
        meta.setCustomModelData(model);
        item.setItemMeta(meta);
        return item;
    }

    static String statusDot(boolean online) {
        return online ? "§a●" : "§c●";
    }

    static boolean isCurrentChannel(int channel) {
        return DungeonChannelManager.isDungeonServer() && EtCetera.getChannelNumber() == channel;
    }

    private static List<String> playerLines(List<String> players) {
        List<String> lines = new ArrayList<>();
        int shown = Math.min(players.size(), MAX_NAMES);
        for (int i = 0; i < shown; i += NAMES_PER_LINE) {
            List<String> chunk = players.subList(i, Math.min(i + NAMES_PER_LINE, shown));
            lines.add(" §7 " + String.join(", ", chunk));
        }
        if (players.size() > MAX_NAMES) {
            lines.add(" §7 외 §e" + (players.size() - MAX_NAMES) + "§7명");
        }
        return lines;
    }

    /** 리소스팩에서 투명하게 보이는 배경 유리판 (custom model data 2) */
    static ItemStack createFiller() {
        ItemStack filler = new ItemStack(Material.GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.setCustomModelData(2);
            filler.setItemMeta(meta);
        }
        return filler;
    }
}
