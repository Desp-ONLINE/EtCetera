package org.swlab.etcetera.Convinience;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * /템 <ID> 검색 결과 GUI (6줄). 타입 상관없이 ID 에 검색어가 포함된 모든 MMOItem 을 보여준다.
 * 상단 5줄 = 검색 결과, 하단 1줄 = 페이지 이동.
 */
public class ItemSearchGui implements InventoryHolder {

    public static final int PREV_SLOT = 45;
    public static final int INFO_SLOT = 49;
    public static final int NEXT_SLOT = 53;

    private static final int SIZE = 54;
    private static final int PAGE_SIZE = 45;

    /** 검색 결과 한 건 (타입 + ID). */
    public record Entry(Type type, String id) {
    }

    private final String keyword;
    private final List<Entry> entries;
    private final Inventory inventory;
    private int page;

    private ItemSearchGui(String keyword, List<Entry> entries) {
        this.keyword = keyword;
        this.entries = entries;
        this.inventory = Bukkit.createInventory(this, SIZE, "아이템 검색: " + keyword);
        render();
    }

    /** ID 에 keyword 가 포함된(대소문자 무시) 모든 템플릿을 찾는다. */
    public static List<Entry> search(String keyword) {
        String upper = keyword.toUpperCase(Locale.ROOT);
        List<Entry> result = new ArrayList<>();
        for (Type type : MMOItems.plugin.getTypes().getAll()) {
            for (MMOItemTemplate template : MMOItems.plugin.getTemplates().getTemplates(type)) {
                if (template.getId().toUpperCase(Locale.ROOT).contains(upper)) {
                    result.add(new Entry(type, template.getId()));
                }
            }
        }
        result.sort(Comparator.comparing(Entry::id).thenComparing(entry -> entry.type().getId()));
        return result;
    }

    public static void open(Player player, String keyword, List<Entry> entries) {
        player.openInventory(new ItemSearchGui(keyword, entries).getInventory());
    }

    /** 해당 슬롯의 검색 결과. 아이템 슬롯이 아니면 null. */
    public Entry getEntry(int slot) {
        if (slot < 0 || slot >= PAGE_SIZE) {
            return null;
        }
        int index = page * PAGE_SIZE + slot;
        return index < entries.size() ? entries.get(index) : null;
    }

    public void prevPage() {
        if (page > 0) {
            page--;
            render();
        }
    }

    public void nextPage() {
        if (page < maxPage()) {
            page++;
            render();
        }
    }

    private int maxPage() {
        return Math.max(0, (entries.size() - 1) / PAGE_SIZE);
    }

    private void render() {
        inventory.clear();

        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            Entry entry = getEntry(slot);
            if (entry == null) {
                break;
            }
            inventory.setItem(slot, buildIcon(entry));
        }

        if (page > 0) {
            inventory.setItem(PREV_SLOT, createButton(Material.ARROW, "§a이전 페이지"));
        }
        if (page < maxPage()) {
            inventory.setItem(NEXT_SLOT, createButton(Material.ARROW, "§a다음 페이지"));
        }
        inventory.setItem(INFO_SLOT, createButton(Material.PAPER,
                "§f" + (page + 1) + " / " + (maxPage() + 1) + " 페이지",
                "§7검색어: §e" + keyword,
                "§7결과: §e" + entries.size() + "개",
                "",
                "§7좌클릭: §f1개 지급",
                "§7쉬프트+좌클릭: §f64개 지급"));
    }

    private ItemStack buildIcon(Entry entry) {
        ItemStack item = MMOItems.plugin.getItem(entry.type(), entry.id());
        if (item == null || item.getType() == Material.AIR) {
            item = new ItemStack(Material.BARRIER);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (!meta.hasDisplayName()) {
            meta.setDisplayName("§f" + entry.id());
        }
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("");
        lore.add("§8" + entry.type().getId() + " : " + entry.id());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material material, String name, String... lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(List.of(lore));
            }
            button.setItemMeta(meta);
        }
        return button;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
