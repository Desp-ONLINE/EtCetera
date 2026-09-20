package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 직업 선택 GUI (6줄). 기존 GUI 플러그인의 "전직" 메뉴를 그대로 옮긴 것.
 * 제피르/루인드는 히든 퀘스트 클리어 권한이 있어야 전직할 수 있다.
 */
public class ClassSelectGui implements InventoryHolder {

    public static final int GUIDE_SLOT = 49;

    private static final int SIZE = 54;
    private static final String TITLE = "직업 선택";

    // 배경 유리판 배치 (x = 직업/안내 아이콘 자리)
    // G=회색 R=빨강 g=초록 O=주황 M=자홍 L=연두 P=보라 C=청록
    private static final String[] LAYOUT = {
            "GGRRgOMMM",
            "GxGxMxOxM",
            "GGRMxMOMM",
            "LLLRxRgPP",
            "LxCxRxgxP",
            "LLLCxggPP"
    };

    public enum ClassEntry {
        CRUSADER(10, "크루세이더", Material.IRON_SWORD, 43, null,
                "#585858크#434D6A루#2D417B세이더", "전장에 누구보다 빨리 돌격하는 전사", "로", null, null),
        INFERNO(12, "인페르노", Material.STONE_SWORD, 8304, null,
                "#FF4F06인#FB3504페#F81A02르#F40000노", "후방에서 먼 사정거리의 적을 타격하는 궁수", "로",
                "§a + §f보스가 불이 붙어 있다면 §b§n+15%§f의 데미지를 가합니다.", null),
        FAUST(14, "파우스트", Material.STONE_SWORD, 2464, null,
                "#BE4A57파#966D79우#6F8F9C스#47B2BE트", "빠른 공격과 콤보를 사용하는 격투가", "로", null, null),
        OBERON(16, "오베론", Material.IRON_SWORD, 247, null,
                "#76F2FF오#807ECF베#890A9E론", "넓은 범위를 타격하는 마법사", "으로", null, null),
        QUASAR(22, "퀘이사", Material.STONE_SWORD, 553009, null,
                "#E29FFF퀘이사", "별빛의 힘을 활용하는 버프형 창술사", "로", null, null),
        DREADNOUGHT(31, "드레드노트", Material.IRON_SWORD, 10028, null,
                "#FF7C7C드레드노트", "검고 붉은 분노를 사용하는 사수", "로", null, null),
        ZEPHYR(37, "제피르", Material.DIAMOND_SWORD, 10097, "class.zephyr",
                "#00FF84제#02ED84피#05DC84르", "매우 빠른 기동력을 가진 암살자", "로", null,
                "§7 ㄴ> 히든 퀘스트, §a§o제피르의 길 §7퀘스트를 클리어 해야 합니다!"),
        RUINED(39, "루인드", Material.IRON_SWORD, 141, "class.ruined",
                "#003B42루#274448인#4D4D4D드", "긴 쿨타임을 갖지만 폭딜을 갖는 대검사", "로", null,
                "§7 ㄴ> 히든 퀘스트, §3루인드 §7퀘스트를 클리어 해야 합니다!"),
        PAN(41, "판", Material.IRON_SWORD, 4590017, null,
                "#1AFF50판", "후방에서 석궁을 사용하는 궁사", "으로", null, null),
        FATAL(43, "페이탈", Material.DIAMOND_SWORD, 26752, null,
                "#3C148C페이탈", "암흑의 힘을 빌린 뇌격의 창술사", "로",
                "§a + §f다른 직업과 다르게, §8흉조§f 효과를 가진 적에게 흉조 효과 §c1§f마다 §b§n+5%§f의 데미지를 가합니다.", null);

        private final int slot;
        private final String className;
        private final Material material;
        private final int customModelData;
        private final String permission;
        private final String coloredName;
        private final String description;
        private final String particle;
        private final String extraLore;
        private final String hiddenLore;

        ClassEntry(int slot, String className, Material material, int customModelData, String permission,
                   String coloredName, String description, String particle, String extraLore, String hiddenLore) {
            this.slot = slot;
            this.className = className;
            this.material = material;
            this.customModelData = customModelData;
            this.permission = permission;
            this.coloredName = coloredName;
            this.description = description;
            this.particle = particle;
            this.extraLore = extraLore;
            this.hiddenLore = hiddenLore;
        }

        public String getClassName() {
            return className;
        }

        /** 전직에 필요한 권한. 제한이 없는 직업은 null. */
        public String getPermission() {
            return permission;
        }
    }

    private final Player player;
    private final Map<Integer, ClassEntry> slotToClass = new HashMap<>();
    private Inventory inventory;
    private long lastClick;

    private ClassSelectGui(Player player) {
        this.player = player;
    }

    public static void open(Player player) {
        player.openInventory(new ClassSelectGui(player).getInventory());
    }

    public ClassEntry getClassEntry(int slot) {
        return slotToClass.get(slot);
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

        for (int i = 0; i < SIZE; i++) {
            Material pane = paneOf(LAYOUT[i / 9].charAt(i % 9));
            if (pane != null) {
                inventory.setItem(i, createFiller(pane));
            }
        }

        for (ClassEntry entry : ClassEntry.values()) {
            inventory.setItem(entry.slot, buildClassItem(entry));
            slotToClass.put(entry.slot, entry);
        }

        inventory.setItem(GUIDE_SLOT, buildGuideItem());
        return inventory;
    }

    private ItemStack buildClassItem(ClassEntry entry) {
        ItemStack item = new ItemStack(entry.material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        String level = PlaceholderAPI.setPlaceholders(player, "%LevelPlaceholder_classLevel_" + entry.className + "%");
        meta.setDisplayName(ColorManager.format(entry.coloredName) + level);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ColorManager.format("§f э " + entry.description + ", " + entry.coloredName + "§f 입니다."));
        lore.add("");
        if (entry.extraLore != null) {
            lore.add(entry.extraLore);
            lore.add("");
        }
        lore.add(ColorManager.format("§f ᎘ §a클릭하여 " + entry.coloredName + "§a" + entry.particle + " 직업을 선택할 수 있습니다."));
        if (entry.hiddenLore != null) {
            lore.add(entry.hiddenLore);
        }
        meta.setLore(lore);
        meta.setCustomModelData(entry.customModelData);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildGuideItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f 직업 추천해주세요! §7§o(클릭)");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createFiller(Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        return filler;
    }

    private static Material paneOf(char c) {
        return switch (c) {
            case 'G' -> Material.GRAY_STAINED_GLASS_PANE;
            case 'R' -> Material.RED_STAINED_GLASS_PANE;
            case 'g' -> Material.GREEN_STAINED_GLASS_PANE;
            case 'O' -> Material.ORANGE_STAINED_GLASS_PANE;
            case 'M' -> Material.MAGENTA_STAINED_GLASS_PANE;
            case 'L' -> Material.LIME_STAINED_GLASS_PANE;
            case 'P' -> Material.PURPLE_STAINED_GLASS_PANE;
            case 'C' -> Material.CYAN_STAINED_GLASS_PANE;
            default -> null;
        };
    }
}
