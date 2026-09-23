package org.swlab.etcetera.Convinience;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Repositories.SkillSequenceRepository;
import org.swlab.etcetera.Util.MergedWeaponUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 자동 연계 시스템 등록 GUI (3줄).
 * 1줄: 등록된 무기(시전 순서 1~9). 좌클릭 제거 / 우클릭 한 칸 앞으로.
 * 3줄: 사용법 · 시전 · 전체 초기화 버튼.
 * 아래(플레이어) 인벤토리의 합성무기를 클릭하면 맨 뒤에 등록된다.
 */
public class SkillSequenceGui implements InventoryHolder {

    private static final int SIZE = 27;
    public static final int SEQUENCE_SLOT_START = 0;
    public static final int SEQUENCE_SLOT_END = 8;
    public static final int SLOT_INFO = 18;
    public static final int SLOT_CAST = 22;
    public static final int SLOT_CLEAR = 26;

    private final Player player;
    private Inventory inventory;

    private SkillSequenceGui(Player player) {
        this.player = player;
    }

    public static void open(Player player) {
        SkillSequenceGui gui = new SkillSequenceGui(player);
        player.openInventory(gui.getInventory());
    }

    public Player getPlayer() {
        return player;
    }

    /** 연계 순서 슬롯(0~8)이면 그 인덱스, 아니면 -1. */
    public static int sequenceIndexOf(int rawSlot) {
        return rawSlot >= SEQUENCE_SLOT_START && rawSlot <= SEQUENCE_SLOT_END ? rawSlot - SEQUENCE_SLOT_START : -1;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, SIZE, "§a자동 연계 시스템 §7» §f무기 등록");
            render();
        }
        return inventory;
    }

    /** 현재 등록 상태로 다시 그린다. */
    public void render() {
        ItemStack filler = named(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }

        List<String> sequence = SkillSequenceRepository.getInstance().getSequence(player);
        for (int i = 0; i <= SEQUENCE_SLOT_END - SEQUENCE_SLOT_START; i++) {
            int slot = SEQUENCE_SLOT_START + i;
            if (i < sequence.size()) {
                inventory.setItem(slot, buildSequenceItem(sequence.get(i), i));
            } else {
                inventory.setItem(slot, buildEmptySlot(i));
            }
        }

        inventory.setItem(SLOT_INFO, buildInfoItem());
        inventory.setItem(SLOT_CAST, buildCastItem());
        inventory.setItem(SLOT_CLEAR, buildClearItem());
    }

    private ItemStack buildSequenceItem(String key, int index) {
        ItemStack weapon = MergedWeaponUtil.findInInventory(player, key);
        ItemStack display;
        List<String> lore = new ArrayList<>();
        if (weapon != null) {
            display = weapon.clone();
            display.setAmount(1);
            ItemMeta meta = display.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore() != null) {
                lore.addAll(meta.getLore());
            }
        } else {
            display = new ItemStack(Material.BARRIER);
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(MergedWeaponUtil.displayNameOfKey(key) + " §7(인벤토리에 없음)");
                display.setItemMeta(meta);
            }
            lore.add("§c인벤토리에 없어 시전 시 건너뜁니다.");
        }
        lore.add("");
        lore.add("§e▶ 시전 순서 : §f" + (index + 1) + "번째");
        lore.add("§7좌클릭 : 등록 해제");
        if (index > 0) {
            lore.add("§7우클릭 : 한 칸 앞으로");
        }
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        return display;
    }

    private ItemStack buildEmptySlot(int index) {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§7" + (index + 1) + "번째 : 비어있음");
            List<String> lore = new ArrayList<>();
            lore.add("§f아래 인벤토리의 §e합성무기§f를 클릭해 등록합니다.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildInfoItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a자동 연계 시스템 사용법");
            List<String> lore = new ArrayList<>();
            lore.add("§f자동 연계 아이템을 §e우클릭§f하면 등록한 순서대로 인벤토리에 있는 합성무기의 스킬을 자동 시전합니다.");
            lore.add("§f");
            lore.add("§e1번째 무기§f의 스킬을 직접 사용해도 나머지가 자동 연계됩니다.");
            lore.add("");
            lore.add("§7- 아래 인벤토리의 합성무기 클릭 : 등록");
            lore.add("§7- 위 칸 좌클릭 : 해제 / 우클릭 : 앞으로 한 칸 이동");
            lore.add("§7- 인벤토리에 없는 무기는 건너뜁니다.");
            lore.add("§7- 강화해도 같은 무기로 인식합니다.");
            lore.add("");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildCastItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a자동 연계 시전");
            List<String> lore = new ArrayList<>();
            lore.add("§f클릭하면 창을 닫고 바로 시전합니다.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildClearItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c전체 초기화");
            List<String> lore = new ArrayList<>();
            lore.add("§f등록된 무기를 모두 해제합니다.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack named(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
