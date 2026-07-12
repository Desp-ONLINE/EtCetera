package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
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
import org.swlab.etcetera.Util.JobWeaponUtil;
import org.swlab.etcetera.Util.JobWeaponUtil.JobWeapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 격변의 거울 GUI (3줄).
 * 1단계 : 내 인벤토리에서 변환할 직업 무기 선택
 * 2단계 : 같은 차수/강화 등급의 다른 직업 무기 중 변환할 무기 선택
 */
public class CataclysmMirrorGui implements InventoryHolder {

    public enum Stage {
        SELECT_WEAPON,
        SELECT_TARGET
    }

    public static final String MIRROR_TYPE = "MISCELLANEOUS";
    public static final String MIRROR_ID = "기타_격변의거울";

    private static final int SIZE = 27;
    private static final int INFO_SLOT = 4;
    private static final int CONTENT_ROW_START = 9;

    // 공통 파스텔 팔레트
    private static final String PASTEL_YELLOW = "#FDFFB6";
    private static final String PASTEL_BLUE = "#A5D8FF";
    private static final String SOFT_WHITE = "#EDEDED";
    private static final String SOFT_GRAY = "#C9C9C9";
    private static final String DIM_GRAY = "#9E9E9E";

    private final Stage stage;
    private final Player viewer;
    private final int sourceSlot;
    private final String sourceId;
    private final JobWeapon source;
    private final Map<Integer, String> slotToJob = new HashMap<>();
    private Inventory inventory;

    private CataclysmMirrorGui(Stage stage, Player viewer, int sourceSlot, String sourceId, JobWeapon source) {
        this.stage = stage;
        this.viewer = viewer;
        this.sourceSlot = sourceSlot;
        this.sourceId = sourceId;
        this.source = source;
    }

    public static void openWeaponSelect(Player player) {
        new CataclysmMirrorGui(Stage.SELECT_WEAPON, player, -1, null, null).open(player);
    }

    public static void openTargetSelect(Player player, int sourceSlot, String sourceId, JobWeapon source) {
        new CataclysmMirrorGui(Stage.SELECT_TARGET, player, sourceSlot, sourceId, source).open(player);
    }

    private void open(Player player) {
        player.openInventory(getInventory());
    }

    public Stage getStage() {
        return stage;
    }

    public int getSourceSlot() {
        return sourceSlot;
    }

    public String getSourceId() {
        return sourceId;
    }

    public JobWeapon getSource() {
        return source;
    }

    public String getJob(int slot) {
        return slotToJob.get(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }

        inventory = Bukkit.createInventory(this, SIZE, "§8[ 격변의 거울 ]");

        ItemStack filler = createFiller();
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }

        inventory.setItem(INFO_SLOT, stage == Stage.SELECT_WEAPON ? buildMirrorInfoItem() : buildSourceInfoItem());

        if (stage == Stage.SELECT_TARGET) {
            placeTargetWeapons();
        }
        return inventory;
    }

    /**
     * 1단계 정보 아이템 : 거울 사용 안내.
     */
    private ItemStack buildMirrorInfoItem() {
        ItemStack item = MMOItems.plugin.getItem(MIRROR_TYPE, MIRROR_ID);
        if (item == null) {
            item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorManager.format(PASTEL_BLUE + " 격변의 거울"));
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ColorManager.format(SOFT_GRAY + "   거울에 무기를 비추어 다른 직업의"));
            lore.add(ColorManager.format(SOFT_GRAY + "   무기로 변환합니다."));
            lore.add("");
            lore.add(ColorManager.format(SOFT_WHITE + "   유지 : " + PASTEL_YELLOW + "차수" + DIM_GRAY + " / " + PASTEL_YELLOW + "강화 등급"));
            lore.add("");
            lore.add(ColorManager.format(PASTEL_YELLOW + "   클릭 " + DIM_GRAY + " - 내 인벤토리의 직업 무기를 선택합니다."));
            meta.setLore(lore);
            applyFlags(meta);
            item.setItemMeta(meta);
        }
        item.setAmount(1);
        return item;
    }

    /**
     * 2단계 정보 아이템 : 선택한 무기 정보.
     */
    private ItemStack buildSourceInfoItem() {
        ItemStack item = MMOItems.plugin.getItem(JobWeaponUtil.WEAPON_TYPE, sourceId);
        if (item == null) {
            item = new ItemStack(Material.NETHERITE_SWORD);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ColorManager.format(SOFT_WHITE + "   차수 : " + PASTEL_YELLOW + source.tier() + "차"));
            lore.add(ColorManager.format(SOFT_WHITE + "   강화 : " + PASTEL_YELLOW + "+" + source.enhance()));
            lore.add("");
            lore.add(ColorManager.format(SOFT_GRAY + "   변환할 직업의 무기를 아래에서 선택하세요."));
            lore.add("");
            lore.add(ColorManager.format(SOFT_WHITE + "   소모 : " + PASTEL_YELLOW + "격변의 거울 1개"));
            meta.setLore(lore);
            applyFlags(meta);
            item.setItemMeta(meta);
        }
        item.setAmount(1);
        return item;
    }

    /**
     * 같은 차수/강화 등급의 다른 직업 무기들을 가운데 줄에 가운데 정렬로 배치한다.
     */
    private void placeTargetWeapons() {
        List<ItemStack> items = new ArrayList<>();
        List<String> jobs = new ArrayList<>();
        for (String job : JobWeaponUtil.JOB_NAMES) {
            if (job.equals(source.job())) {
                continue;
            }
            ItemStack item = buildTargetItem(job);
            if (item == null) {
                continue;
            }
            items.add(item);
            jobs.add(job);
        }

        int pad = (9 - items.size()) / 2;
        for (int i = 0; i < items.size() && i < 9; i++) {
            int slot = CONTENT_ROW_START + pad + i;
            inventory.setItem(slot, items.get(i));
            slotToJob.put(slot, jobs.get(i));
        }
    }

    private ItemStack buildTargetItem(String job) {
        String targetId = JobWeaponUtil.buildId(source.tier(), job, source.enhance());
        ItemStack item = MMOItems.plugin.getItem(JobWeaponUtil.WEAPON_TYPE, targetId);
        if (item == null) {
            return null;
        }
        boolean locked = JobWeaponUtil.requiresTier6Quest(source.tier())
                && !JobWeaponUtil.hasFinishedTier6Quest(viewer, job);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() && meta.getLore() != null
                    ? new ArrayList<>(meta.getLore())
                    : new ArrayList<>();
            lore.add("");
            if (locked) {
                lore.add(ColorManager.format("#FF8888   잠김 " + DIM_GRAY + " - 해당 직업의 6차 전직 퀘스트 클리어가 필요합니다."));
            } else {
                lore.add(ColorManager.format(PASTEL_YELLOW + "   클릭 " + DIM_GRAY + " - 이 무기로 변환합니다."));
            }
            meta.setLore(lore);
            applyFlags(meta);
            item.setItemMeta(meta);
        }
        item.setAmount(1);
        return item;
    }

    /**
     * MMOItems 아이템의 표시 이름을 반환한다. 이름이 없으면 ID 를 그대로 반환한다.
     */
    public static String weaponName(String id) {
        ItemStack item = MMOItems.plugin.getItem(JobWeaponUtil.WEAPON_TYPE, id);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return id;
    }

    private void applyFlags(ItemMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
    }

    private ItemStack createFiller() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        return filler;
    }
}
