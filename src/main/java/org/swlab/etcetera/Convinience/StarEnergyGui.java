package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.EconomyManager;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Repositories.StarEnergyRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 별의 기운 GUI.
 * MAIN      : 보유량 / 비용 표시, 무작위 교환 버튼, 초월석 3종(클릭 시 골드 추가 소모로 선택 교환), 레이드별 획득량 버튼
 * RAID_LIST : 레이드별 별의 기운 지급량 + 파티 인원별 1인당 획득량
 * CONFIRM   : 교환 최종 확인
 */
public class StarEnergyGui implements InventoryHolder {

    public enum Stage {
        MAIN,
        RAID_LIST,
        CONFIRM
    }

    // ===== 교환 설정 =====
    public static final int ENERGY_COST = 1000;
    public static final String CATALYST_TYPE = "MISCELLANEOUS";
    public static final String CATALYST_ID = "기타_어센드카탈리스트";
    public static final int CATALYST_COST = 3;
    public static final long SELECT_GOLD_COST = 30_000_000L;
    public static final String STONE_TYPE = "MISCELLANEOUS";
    public static final List<String> STONE_IDS = Arrays.asList("기타_청초월석", "기타_자초월석", "기타_흑초월석");
    // 무작위 교환 확률 (만분율, 합 10000)
    public static final int[] STONE_WEIGHTS = {3333, 3333, 3334};

    // ===== 색상 =====
    public static final String STAR = "#9ADCFF";
    public static final String STAR_DEEP = "#6FA8FF";
    public static final String GOLD = "#FFD966";
    public static final String SOFT = "#C9C9D6";
    public static final String RED = "#FF9AA2";
    public static final String GREEN = "#A8E6A1";

    // ===== MAIN 슬롯 =====
    public static final int SLOT_INFO = 4;
    public static final int SLOT_COST_ENERGY = 11;
    public static final int SLOT_COST_CATALYST = 13;
    public static final int SLOT_COST_GOLD = 15;
    public static final int[] SLOT_STONES = {29, 31, 33};
    public static final int SLOT_RANDOM = 22;
    public static final int SLOT_RAID_LIST = 39;
    public static final int SLOT_GUIDE = 41;

    // ===== RAID_LIST / CONFIRM 슬롯 =====
    public static final int RAIDS_PER_PAGE = 36;
    public static final int SLOT_PREV = 45;
    public static final int SLOT_BACK = 49;
    public static final int SLOT_NEXT = 53;
    public static final int SLOT_CONFIRM = 11;
    public static final int SLOT_CONFIRM_PREVIEW = 13;
    public static final int SLOT_CANCEL = 15;

    private static final NumberFormat NUMBER = NumberFormat.getIntegerInstance();

    private final Stage stage;
    private final int page;
    // CONFIRM 단계: 선택 교환이면 초월석 ID, 무작위 교환이면 null
    private final String selectedStone;
    private final Player viewer;
    private final Map<Integer, String> slotToStone = new HashMap<>();
    private Inventory inventory;

    private StarEnergyGui(Player viewer, Stage stage, int page, String selectedStone) {
        this.viewer = viewer;
        this.stage = stage;
        this.page = page;
        this.selectedStone = selectedStone;
    }

    public static void openMain(Player player) {
        player.openInventory(new StarEnergyGui(player, Stage.MAIN, 0, null).getInventory());
    }

    public static void openRaidList(Player player, int page) {
        player.openInventory(new StarEnergyGui(player, Stage.RAID_LIST, page, null).getInventory());
    }

    public static void openConfirm(Player player, String selectedStone) {
        player.openInventory(new StarEnergyGui(player, Stage.CONFIRM, 0, selectedStone).getInventory());
    }

    public Stage getStage() {
        return stage;
    }

    public int getPage() {
        return page;
    }

    public String getSelectedStone() {
        return selectedStone;
    }

    public String getStone(int slot) {
        return slotToStone.get(slot);
    }

    public static int getMaxPage() {
        int size = StarEnergyRepository.RAIDS.size();
        return Math.max(0, (size - 1) / RAIDS_PER_PAGE);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }
        switch (stage) {
            case RAID_LIST -> buildRaidList();
            case CONFIRM -> buildConfirm();
            default -> buildMain();
        }
        return inventory;
    }

    // ================= MAIN =================

    private void buildMain() {
        inventory = Bukkit.createInventory(this, 45, ColorManager.format(STAR_DEEP + "별의 기운 §7» §f초월석 교환"));
        fill(Material.BLACK_STAINED_GLASS_PANE);
        for (int slot : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 36, 37, 38, 40, 42, 43, 44}) {
            inventory.setItem(slot, filler(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        }

        Integer owned = StarEnergyRepository.getInstance().getCachedAmount(viewer);
        String ownedText = owned == null ? "§7불러오는 중..." : STAR + NUMBER.format(owned) + " §7/ " + NUMBER.format(StarEnergyRepository.MAX_AMOUNT);

        inventory.setItem(SLOT_INFO, simple(Material.NETHER_STAR, STAR + "별의 기운", true,
                "",
                "§f보유량 : " + ownedText,
                "",
                SOFT + "레이드를 클리어해 모은 별의 힘입니다.",
                SOFT + "초월석으로 교환할 수 있습니다.",
                "",
                SOFT + "최대 " + STAR + NUMBER.format(StarEnergyRepository.MAX_AMOUNT) + SOFT + "까지 보유할 수 있습니다.",
                RED + "초과하는 별의 기운은 소멸합니다."));

        // 비용 표시
        boolean energyEnough = owned != null && owned >= ENERGY_COST;
        inventory.setItem(SLOT_COST_ENERGY, simple(Material.NETHER_STAR, STAR + "필요 : 별의 기운 " + NUMBER.format(ENERGY_COST), false,
                "",
                "§f보유 : " + ownedText + " " + check(energyEnough),
                "",
                SOFT + "모든 교환에 공통으로 소모됩니다."));

        ItemStack catalyst = MMOItems.plugin.getItem(CATALYST_TYPE, CATALYST_ID);
        int catalystOwned = countMMOItem(viewer, CATALYST_TYPE, CATALYST_ID);
        if (catalyst != null) {
            catalyst.setAmount(CATALYST_COST);
            appendLore(catalyst,
                    "",
                    "§f필요 : " + CATALYST_COST + "개",
                    "§f보유 : " + (catalystOwned >= CATALYST_COST ? GREEN : RED) + catalystOwned + "개 " + check(catalystOwned >= CATALYST_COST),
                    "",
                    SOFT + "모든 교환에 공통으로 소모됩니다.");
            inventory.setItem(SLOT_COST_CATALYST, catalyst);
        }

        boolean goldEnough = viewer.isOnline() && EconomyManager.hasMoney(viewer, SELECT_GOLD_COST);
        inventory.setItem(SLOT_COST_GOLD, simple(Material.GOLD_INGOT, GOLD + "선택 교환 시 추가 : " + NUMBER.format(SELECT_GOLD_COST) + " 골드", false,
                "",
                "§f보유 골드 : " + GOLD + NUMBER.format((long) EconomyManager.getMoney(viewer)) + " " + check(goldEnough),
                "",
                SOFT + "초월석을 직접 선택할 때만 추가로 소모됩니다.",
                SOFT + "무작위 교환은 골드를 소모하지 않습니다."));

        // 무작위 교환
        List<String> randomLore = new ArrayList<>();
        randomLore.add("");
        randomLore.add("§f소모 : " + STAR + "별의 기운 " + NUMBER.format(ENERGY_COST) + " §7+ §f" + itemName(CATALYST_TYPE, CATALYST_ID) + " §f" + CATALYST_COST + "개");
        randomLore.add("");
        randomLore.add("§f아래 초월석 중 하나를 §d무작위§f로 획득합니다.");
        for (int i = 0; i < STONE_IDS.size(); i++) {
            randomLore.add("  §7- §f" + itemName(STONE_TYPE, STONE_IDS.get(i)));
        }
        randomLore.add("");
        randomLore.add(STAR + "클릭하여 무작위로 교환할 수 있습니다.");
        inventory.setItem(SLOT_RANDOM, simple(Material.ENDER_EYE, STAR + "무작위 교환", true, randomLore.toArray(new String[0])));

        // 선택 교환 (초월석 3종)
        for (int i = 0; i < STONE_IDS.size() && i < SLOT_STONES.length; i++) {
            String id = STONE_IDS.get(i);
            ItemStack stone = MMOItems.plugin.getItem(STONE_TYPE, id);
            if (stone == null) {
                continue;
            }
            stone.setAmount(1);
            appendLore(stone,
                    "",
                    GOLD + "[ 선택 교환 ]",
                    "§f소모 : " + STAR + "별의 기운 " + NUMBER.format(ENERGY_COST) + " §7+ §f" + itemName(CATALYST_TYPE, CATALYST_ID) + " §f" + CATALYST_COST + "개 §7+ " + GOLD + NUMBER.format(SELECT_GOLD_COST) + " 골드",
                    "",
                    GOLD + "클릭하여 이 초월석으로 교환할 수 있습니다.");
            inventory.setItem(SLOT_STONES[i], stone);
            slotToStone.put(SLOT_STONES[i], id);
        }

        inventory.setItem(SLOT_RAID_LIST, simple(Material.WRITABLE_BOOK, STAR + "레이드별 별의 기운 확인", false,
                "",
                SOFT + "레이드별로 얻을 수 있는 별의 기운을 확인합니다.",
                "",
                STAR + "클릭하여 확인할 수 있습니다."));

        inventory.setItem(SLOT_GUIDE, simple(Material.KNOWLEDGE_BOOK, GOLD + "별의 기운은 어떻게 얻나요?", false, guideLore()));
    }

    public static String[] guideLore() {
        List<String> lines = new ArrayList<>();
        lines.add("");
        if (!StarEnergyRepository.isEarnable()) {
            lines.add(RED + StarEnergyRepository.START_DATE.getMonthValue() + "월 " + StarEnergyRepository.START_DATE.getDayOfMonth() + "일부터 획득할 수 있습니다.");
            lines.add("");
        }
        lines.addAll(Arrays.asList(
                "§f레이드를 클리어하고 §e보상방에 입장§f하면 정해진 별의 기운을 받습니다.",
                "",
                GOLD + "파티원과 나누어 받습니다.",
                SOFT + "보상방에 함께 입장한 §f파티원 수" + SOFT + "만큼 똑같이 나누어 지급됩니다.",
                "",
                "§7예) 별의 기운 " + STAR + "300§7인 레이드를 클리어한 경우",
                "  §7- §f1인 파티 §7: " + STAR + "300",
                "  §7- §f2인 파티 §7: 각자 " + STAR + "150",
                "  §7- §f3인 파티 §7: 각자 " + STAR + "100",
                "  §7- §f4인 파티 §7: 각자 " + STAR + "75",
                "",
                SOFT + "나누어 떨어지지 않는 나머지는 버려집니다. §7(예: 100을 3인이 나누면 각자 33)",
                "",
                RED + "최대 " + NUMBER.format(StarEnergyRepository.MAX_AMOUNT) + "까지 보유할 수 있습니다.",
                RED + "초과하는 별의 기운은 소멸합니다."
        ));
        return lines.toArray(new String[0]);
    }

    // ================= RAID_LIST =================

    private void buildRaidList() {
        int maxPage = getMaxPage();
        inventory = Bukkit.createInventory(this, 54, ColorManager.format(STAR_DEEP + "별의 기운 §7» §f레이드별 획득량 §7(" + (page + 1) + "/" + (maxPage + 1) + ")"));
        for (int i = 36; i < 54; i++) {
            inventory.setItem(i, filler(Material.BLACK_STAINED_GLASS_PANE));
        }

        List<StarEnergyRepository.RaidEnergy> raids = StarEnergyRepository.RAIDS;
        int start = page * RAIDS_PER_PAGE;
        for (int i = 0; i < RAIDS_PER_PAGE && start + i < raids.size(); i++) {
            StarEnergyRepository.RaidEnergy raid = raids.get(start + i);
            int total = raid.amount();
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§f클리어 시 별의 기운 : " + STAR + NUMBER.format(total));
            lore.add("");
            lore.add(GOLD + "파티 인원별 1인당 획득량");
            for (int size = 1; size <= 4; size++) {
                lore.add("  §7" + size + "인 파티 : " + STAR + NUMBER.format(total / size));
            }
            lore.add("");
            lore.add(SOFT + "보상방에 함께 입장한 인원 수로 나누어 지급됩니다.");
            inventory.setItem(i, simple(Material.NETHER_STAR, "§6" + raid.name(), false, lore.toArray(new String[0])));
        }
        if (raids.isEmpty()) {
            inventory.setItem(13, simple(Material.BARRIER, RED + "등록된 레이드가 없습니다.", false));
        }

        inventory.setItem(47, simple(Material.KNOWLEDGE_BOOK, GOLD + "별의 기운 획득 안내", false, guideLore()));
        if (page > 0) {
            inventory.setItem(SLOT_PREV, simple(Material.ARROW, "§f이전 페이지", false));
        }
        if (page < maxPage) {
            inventory.setItem(SLOT_NEXT, simple(Material.ARROW, "§f다음 페이지", false));
        }
        inventory.setItem(SLOT_BACK, simple(Material.OAK_DOOR, "§f돌아가기", false));
    }

    // ================= CONFIRM =================

    private void buildConfirm() {
        boolean select = selectedStone != null;
        inventory = Bukkit.createInventory(this, 27, ColorManager.format(STAR_DEEP + "별의 기운 §7» §f" + (select ? "선택 교환 확인" : "무작위 교환 확인")));
        fill(Material.BLACK_STAINED_GLASS_PANE);

        List<String> costLore = new ArrayList<>();
        costLore.add("");
        costLore.add("§f소모 재료");
        costLore.add("  §7- " + STAR + "별의 기운 " + NUMBER.format(ENERGY_COST));
        costLore.add("  §7- §f" + itemName(CATALYST_TYPE, CATALYST_ID) + " §f" + CATALYST_COST + "개");
        if (select) {
            costLore.add("  §7- " + GOLD + NUMBER.format(SELECT_GOLD_COST) + " 골드");
        }
        costLore.add("");

        if (select) {
            ItemStack preview = MMOItems.plugin.getItem(STONE_TYPE, selectedStone);
            if (preview != null) {
                preview.setAmount(1);
                inventory.setItem(SLOT_CONFIRM_PREVIEW, preview);
            }
        } else {
            List<String> lore = new ArrayList<>();
            lore.add("");
            for (int i = 0; i < STONE_IDS.size(); i++) {
                lore.add("§7- §f" + itemName(STONE_TYPE, STONE_IDS.get(i)));
            }
            inventory.setItem(SLOT_CONFIRM_PREVIEW, simple(Material.ENDER_EYE, STAR + "무작위 초월석 1개", true, lore.toArray(new String[0])));
        }

        List<String> confirmLore = new ArrayList<>(costLore);
        confirmLore.add(GREEN + "클릭하여 교환할 수 있습니다.");
        inventory.setItem(SLOT_CONFIRM, simple(Material.LIME_STAINED_GLASS_PANE, GREEN + "교환하기", false, confirmLore.toArray(new String[0])));
        inventory.setItem(SLOT_CANCEL, simple(Material.RED_STAINED_GLASS_PANE, RED + "취소", false, "", SOFT + "클릭하여 이전 화면으로 돌아갈 수 있습니다."));
    }

    // ================= util =================

    private static String check(boolean ok) {
        return ok ? GREEN + "✔" : RED + "(부족)";
    }

    private void fill(Material material) {
        ItemStack filler = filler(material);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }
    }

    private static ItemStack filler(Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        return filler;
    }

    private static ItemStack simple(Material material, String name, boolean glow, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorManager.format(name));
            List<String> lines = new ArrayList<>();
            for (String line : lore) {
                lines.add(ColorManager.format(indent(line)));
            }
            meta.setLore(lines);
            meta.addItemFlags(ItemFlag.values());
            if (glow) {
                meta.setEnchantmentGlintOverride(true);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    // 설명 로어는 모두 스페이스 2칸 들여쓰기 (빈 줄 제외)
    private static String indent(String line) {
        return line.isEmpty() ? line : "  " + line;
    }

    private static void appendLore(ItemStack item, String... lines) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        for (String line : lines) {
            lore.add(ColorManager.format(indent(line)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String itemName(String type, String id) {
        return HiddenExchangeGui.itemName(type, id);
    }

    public static boolean matches(ItemStack item, String type, String id) {
        String itemId = MMOItems.getID(item);
        if (itemId == null || !itemId.equals(id)) {
            return false;
        }
        String typeName = MMOItems.getTypeName(item);
        return typeName != null && typeName.equals(type);
    }

    public static int countMMOItem(Player player, String type, String id) {
        int total = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR) {
                continue;
            }
            if (matches(content, type, id)) {
                total += content.getAmount();
            }
        }
        return total;
    }
}
