package org.swlab.etcetera.Training.gui;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.objects.TrainingSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 훈련 정보 GUI (6줄).
 * 훈련을 마친 플레이어별로 /정보 스텟과 사용한 스킬(시전 아이템) 내역을 머리 아이템 로어로 보여준다.
 */
public class TrainingInfoGui implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int CONTENT_SIZE = 45;
    public static final int PREV_SLOT = 45;
    public static final int NEXT_SLOT = 53;
    private static final int PAGE_SLOT = 49;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd HH:mm:ss");

    private final int page;
    private final List<TrainingSnapshot> snapshots;
    private Inventory inventory;

    private TrainingInfoGui(int page, List<TrainingSnapshot> snapshots) {
        this.page = page;
        this.snapshots = snapshots;
    }

    public static void open(Player admin, int page) {
        List<TrainingSnapshot> snapshots = TrainingManager.getInstance().getSnapshotsNewestFirst();
        int maxPage = maxPage(snapshots.size());
        int fixedPage = Math.max(0, Math.min(page, maxPage));
        admin.openInventory(new TrainingInfoGui(fixedPage, snapshots).getInventory());
    }

    public int getPage() {
        return page;
    }

    public int getMaxPage() {
        return maxPage(snapshots.size());
    }

    private static int maxPage(int size) {
        return Math.max(0, (size - 1) / CONTENT_SIZE);
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }
        inventory = Bukkit.createInventory(this, SIZE, "§8[ 훈련 정보 ]");

        int start = page * CONTENT_SIZE;
        for (int i = 0; i < CONTENT_SIZE && start + i < snapshots.size(); i++) {
            inventory.setItem(i, buildSnapshotItem(snapshots.get(start + i)));
        }

        ItemStack filler = createFiller();
        for (int i = CONTENT_SIZE; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }
        if (page > 0) {
            inventory.setItem(PREV_SLOT, createButton(Material.ARROW, "#A5D8FF이전 페이지"));
        }
        if (page < getMaxPage()) {
            inventory.setItem(NEXT_SLOT, createButton(Material.ARROW, "#A5D8FF다음 페이지"));
        }
        inventory.setItem(PAGE_SLOT, createButton(Material.BOOK,
                "#FDFFB6페이지 %d / %d §7(기록 %d명)".formatted(page + 1, getMaxPage() + 1, snapshots.size())));
        return inventory;
    }

    private ItemStack buildSnapshotItem(TrainingSnapshot snapshot) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(snapshot.getUuid()));
        }
        meta.setDisplayName(ColorManager.format("#FDFFB6" + snapshot.getPlayerName() + " §7(" + snapshot.getJob() + ")"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ColorManager.format("§7  훈련 일시 : §f" + DATE_FORMAT.format(new Date(snapshot.getCapturedAt()))));
        lore.add(ColorManager.format("§7  누적 데미지 : #FF6347" + NumberUtil.applyComma(snapshot.getTotalDamage())
                + " §7(" + snapshot.getHitCount() + "타)"));
        lore.add(ColorManager.format("§7  평균 DPS : §f" + NumberUtil.applyComma(snapshot.getAverageDps())
                + " §7/ 최고 DPS : §f" + NumberUtil.applyComma(snapshot.getPeakDps())));
        TrainingSnapshot.WeaponInfo weapon = snapshot.getWeapon();
        if (weapon != null) {
            String detail = weapon.tier() != null ? " §7(" + weapon.tier() + "차 +" + weapon.enhance() + ")" : "";
            lore.add(ColorManager.format("§7  무기 : §f" + weapon.name() + detail));
        }
        lore.add("");
        lore.add(ColorManager.format("#FFA500  ━━ 스텟 (/정보) ━━"));
        for (Map.Entry<String, String> stat : snapshot.getStats().entrySet()) {
            lore.add(ColorManager.format("§7   " + stat.getKey() + " : §f" + stat.getValue()));
        }
        lore.add("");
        lore.add(ColorManager.format("#FFA500  ━━ 사용 스킬 아이템 ━━"));
        if (snapshot.getSkillUsages().isEmpty()) {
            lore.add(ColorManager.format("§8   기록 없음"));
        } else {
            double total = snapshot.getTotalDamage();
            int rank = 1;
            for (TrainingSnapshot.SkillUsage usage : snapshot.getSkillUsages()) {
                double share = total <= 0 ? 0 : usage.totalDamage() / total * 100;
                lore.add(ColorManager.format("§f   %d. %s §7- #FF6347%s §7(%.1f%% | %d타)"
                        .formatted(rank++, usage.itemName(), NumberUtil.applyComma(usage.totalDamage()),
                                share, usage.hits())));
            }
        }
        if (!snapshot.getIntervals().isEmpty()) {
            lore.add("");
            lore.add(ColorManager.format("#FFA500  ━━ 구간 기록 (10초) ━━"));
            for (TrainingSnapshot.Interval interval : snapshot.getIntervals()) {
                lore.add(ColorManager.format("§7   %d~%d초 : #FF6347%s §7(DPS %s)"
                        .formatted(interval.startSec(), interval.endSec(),
                                NumberUtil.applyComma(interval.damage()), NumberUtil.applyComma(interval.dps()))));
            }
        }
        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorManager.format(name));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFiller() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        return filler;
    }
}
