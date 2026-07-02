package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.HiddenMaterialConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 히든 재료 교환 GUI (3줄). 파스텔 톤 색상 + 재료를 가운데 정렬로 균등 배치한다.
 * 1단계 : 바꿀(소모할) 재료 선택 → 2단계 : 어떤 재료로 바꿀지 선택.
 */
public class HiddenExchangeGui implements InventoryHolder {

    public enum Stage {
        SELECT_SOURCE,
        SELECT_TARGET
    }

    private static final int ROWS = 3;
    private static final int SIZE = ROWS * 9;

    // 소모 재료(원초의핵)를 보여줄 고정 슬롯 (맨 아래 줄 가운데)
    private static final int COST_SLOT = 22;

    // 파스텔 톤 색상
    private static final String PASTEL_MINT = "#B5EAD7";   // 1단계 강조
    private static final String PASTEL_PINK = "#FFB7C5";   // 2단계 강조
    private static final String PASTEL_GOLD = "#FDE7A9";   // 소모 재료

    private final Stage stage;
    private final String sourceId;
    private final Map<Integer, String> slotToMaterial = new HashMap<>();
    private Inventory inventory;

    private HiddenExchangeGui(Stage stage, String sourceId) {
        this.stage = stage;
        this.sourceId = sourceId;
    }

    public static void openSourceSelect(Player player) {
        new HiddenExchangeGui(Stage.SELECT_SOURCE, null).open(player);
    }

    public static void openTargetSelect(Player player, String sourceId) {
        new HiddenExchangeGui(Stage.SELECT_TARGET, sourceId).open(player);
    }

    private void open(Player player) {
        player.openInventory(getInventory());
    }

    public Stage getStage() {
        return stage;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getMaterial(int slot) {
        return slotToMaterial.get(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }

        String title = stage == Stage.SELECT_SOURCE
                ? ColorManager.format(PASTEL_MINT + "히든 재료 교환 §7» §f바꿀 재료 선택")
                : ColorManager.format(PASTEL_PINK + "히든 재료 교환 §7» §f무엇으로 바꿀까요?");

        inventory = Bukkit.createInventory(this, SIZE, title);

        Material fillerType = stage == Stage.SELECT_SOURCE
                ? Material.LIGHT_BLUE_STAINED_GLASS_PANE
                : Material.PINK_STAINED_GLASS_PANE;
        ItemStack filler = createFiller(fillerType);
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }

        List<String> materials = HiddenMaterialConfig.HIDDEN_MATERIAL_IDS;
        List<Integer> slots = centeredSlots(materials.size());

        for (int i = 0; i < materials.size() && i < slots.size(); i++) {
            String id = materials.get(i);
            ItemStack item = buildMaterialItem(id);
            if (item == null) {
                continue;
            }
            int slot = slots.get(i);
            inventory.setItem(slot, item);
            slotToMaterial.put(slot, id);
        }

        // 소모 재료(원초의핵)를 아이템으로 표시
        ItemStack costItem = buildCostItem();
        if (costItem != null) {
            inventory.setItem(COST_SLOT, costItem);
        }

        return inventory;
    }

    private ItemStack buildCostItem() {
        ItemStack item = MMOItems.plugin.getItem(HiddenMaterialConfig.COST_ITEM_TYPE, HiddenMaterialConfig.COST_ITEM_ID);
        if (item == null) {
            return null;
        }
        item.setAmount(HiddenMaterialConfig.COST_AMOUNT);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() && meta.getLore() != null
                    ? new ArrayList<>(meta.getLore())
                    : new ArrayList<>();
            lore.add("");
            lore.add(ColorManager.format(PASTEL_GOLD + "▶ §f교환에 필요한 소모 재료 §f"
                    + HiddenMaterialConfig.COST_AMOUNT + "개"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMaterialItem(String id) {
        ItemStack item = MMOItems.plugin.getItem(HiddenMaterialConfig.HIDDEN_MATERIAL_TYPE, id);
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() && meta.getLore() != null
                    ? new ArrayList<>(meta.getLore())
                    : new ArrayList<>();
            lore.add("");
            if (stage == Stage.SELECT_SOURCE) {
                lore.add(ColorManager.format(PASTEL_MINT + "▶ §f클릭하여 이 재료를 교환합니다."));
            } else {
                lore.add(ColorManager.format(PASTEL_PINK + "▶ §f클릭하여 이 재료로 교환합니다."));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        item.setAmount(1);
        return item;
    }

    /**
     * MMOItems 아이템의 표시 이름을 반환한다. 이름이 없으면 ID 를 그대로 반환한다.
     */
    public static String itemName(String type, String id) {
        ItemStack item = MMOItems.plugin.getItem(type, id);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return id;
    }

    /**
     * 히든 재료(MISCELLANEOUS)의 표시 이름을 반환한다.
     */
    public static String materialName(String id) {
        return itemName(HiddenMaterialConfig.HIDDEN_MATERIAL_TYPE, id);
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

    /**
     * 재료 개수만큼, 3줄 인벤토리 안에서 가운데 정렬로 균등 배치할 슬롯을 계산한다.
     * 재료가 적으면 가운데 줄부터 채워 간격이 일정하게 보이도록 한다.
     */
    private List<Integer> centeredSlots(int count) {
        List<Integer> result = new ArrayList<>();
        if (count <= 0) {
            return result;
        }
        int rowsUsed = Math.min(ROWS, (int) Math.ceil(count / 9.0));
        int[] physicalRows;
        if (rowsUsed == 1) {
            physicalRows = new int[]{1};          // 가운데 줄
        } else if (rowsUsed == 2) {
            physicalRows = new int[]{0, 1};       // 위 + 가운데
        } else {
            physicalRows = new int[]{0, 1, 2};    // 전체
        }

        int base = count / rowsUsed;
        int extra = count % rowsUsed;
        for (int r = 0; r < rowsUsed; r++) {
            int inThisRow = base + (r < extra ? 1 : 0);
            int rowStart = physicalRows[r] * 9;
            int pad = (9 - inThisRow) / 2;
            for (int c = 0; c < inThisRow; c++) {
                result.add(rowStart + pad + c);
            }
        }
        return result;
    }
}
