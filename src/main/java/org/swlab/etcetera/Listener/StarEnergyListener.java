package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.EconomyManager;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Convinience.StarEnergyGui;
import org.swlab.etcetera.Repositories.StarEnergyRepository;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.swlab.etcetera.Convinience.StarEnergyGui.*;

/**
 * 별의 기운 GUI 클릭 처리 + 초월석 교환.
 * 무작위 교환 : 별의 기운 1000 + 어센드카탈리스트 3개
 * 선택 교환   : 위 비용 + 3000만 골드
 */
public class StarEnergyListener implements Listener {

    private static final String PREFIX = STAR + "[ 별의 기운 ] §f";
    private static final NumberFormat NUMBER = NumberFormat.getIntegerInstance();

    // 연타로 인한 중복 교환 방지
    private final Set<UUID> processing = new HashSet<>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof StarEnergyGui gui)) {
            return;
        }
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getInventory())) {
            return;
        }
        int slot = e.getRawSlot();

        switch (gui.getStage()) {
            case MAIN -> {
                if (slot == SLOT_RANDOM) {
                    StarEnergyGui.openConfirm(player, null);
                } else if (slot == SLOT_RAID_LIST) {
                    StarEnergyGui.openRaidList(player, 0);
                } else if (gui.getStone(slot) != null) {
                    StarEnergyGui.openConfirm(player, gui.getStone(slot));
                }
            }
            case RAID_LIST -> {
                if (slot == SLOT_BACK) {
                    StarEnergyGui.openMain(player);
                } else if (slot == SLOT_PREV && gui.getPage() > 0) {
                    StarEnergyGui.openRaidList(player, gui.getPage() - 1);
                } else if (slot == SLOT_NEXT && gui.getPage() < StarEnergyGui.getMaxPage()) {
                    StarEnergyGui.openRaidList(player, gui.getPage() + 1);
                }
            }
            case CONFIRM -> {
                if (slot == SLOT_CANCEL) {
                    StarEnergyGui.openMain(player);
                } else if (slot == SLOT_CONFIRM) {
                    if (!processing.add(player.getUniqueId())) {
                        return;
                    }
                    try {
                        exchange(player, gui.getSelectedStone());
                    } finally {
                        processing.remove(player.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof StarEnergyGui) {
            e.setCancelled(true);
        }
    }

    private void exchange(Player player, String selectedStone) {
        boolean select = selectedStone != null;
        String stoneId = select ? selectedStone : rollStone();

        // 보상 아이템을 먼저 생성해 유효성 확인 (소모 전에 실패 방지)
        ItemStack reward = MMOItems.plugin.getItem(STONE_TYPE, stoneId);
        if (reward == null) {
            fail(player, "초월석 정보를 찾을 수 없습니다. 관리자에게 문의해주세요. §7(" + stoneId + ")");
            return;
        }

        StarEnergyRepository repository = StarEnergyRepository.getInstance();
        Integer owned = repository.getCachedAmount(player);
        if (owned == null) {
            fail(player, "별의 기운 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
            return;
        }
        if (owned < ENERGY_COST) {
            fail(player, "별의 기운이 부족합니다. §7(§c" + NUMBER.format(owned) + "§7 / " + NUMBER.format(ENERGY_COST) + ")");
            return;
        }
        int catalystOwned = countMMOItem(player, CATALYST_TYPE, CATALYST_ID);
        if (catalystOwned < CATALYST_COST) {
            fail(player, ColorManager.format("§f") + itemName(CATALYST_TYPE, CATALYST_ID) + ColorManager.format(" §f이(가) 부족합니다. §7(§c" + catalystOwned + "§7 / " + CATALYST_COST + "개)"));
            return;
        }
        if (select && !EconomyManager.hasMoney(player, SELECT_GOLD_COST)) {
            fail(player, "골드가 부족합니다. §7(필요: " + GOLD + NUMBER.format(SELECT_GOLD_COST) + " 골드§7)");
            return;
        }

        // 별의 기운은 DB 기준으로 최종 차감 (다른 채널에서 변동됐을 수 있음)
        if (!repository.consumeAmount(player.getUniqueId().toString(), player.getName(), ENERGY_COST)) {
            repository.loadUserData(player);
            fail(player, "별의 기운이 부족합니다.");
            return;
        }
        removeMMOItem(player, CATALYST_TYPE, CATALYST_ID, CATALYST_COST);
        if (select) {
            EconomyManager.removeMoney(player, SELECT_GOLD_COST);
        }

        reward.setAmount(1);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(reward);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }

        player.closeInventory();
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1.4f);
        String stoneName = itemName(STONE_TYPE, stoneId);
        player.sendMessage(ColorManager.format(PREFIX + (select ? "선택 교환" : "무작위 교환") + "이 완료되었습니다!"));
        player.sendMessage(ColorManager.format("  §7- §f획득 : ") + stoneName);
        Integer remain = repository.getCachedAmount(player);
        player.sendMessage(ColorManager.format("  §7- §f남은 별의 기운 : " + STAR + NUMBER.format(remain == null ? 0 : remain) + " §7/ " + NUMBER.format(StarEnergyRepository.MAX_AMOUNT)));
    }

    private String rollStone() {
        int total = 0;
        for (int weight : STONE_WEIGHTS) {
            total += weight;
        }
        int roll = ThreadLocalRandom.current().nextInt(total);
        for (int i = 0; i < STONE_WEIGHTS.length; i++) {
            roll -= STONE_WEIGHTS[i];
            if (roll < 0) {
                return STONE_IDS.get(i);
            }
        }
        return STONE_IDS.get(STONE_IDS.size() - 1);
    }

    private void fail(Player player, String message) {
        player.sendMessage(ColorManager.format(RED + "[ 별의 기운 ] §f" + message));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 1f);
    }

    private void removeMMOItem(Player player, String type, String id, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack content = contents[i];
            if (content == null || content.getType() == Material.AIR) {
                continue;
            }
            if (!matches(content, type, id)) {
                continue;
            }
            int stackAmount = content.getAmount();
            if (stackAmount <= remaining) {
                remaining -= stackAmount;
                player.getInventory().setItem(i, null);
            } else {
                content.setAmount(stackAmount - remaining);
                remaining = 0;
            }
        }
        player.updateInventory();
    }
}
