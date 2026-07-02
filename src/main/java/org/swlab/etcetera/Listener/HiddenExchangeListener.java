package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmotimereset.api.WeeklyResetEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Convinience.HiddenExchangeGui;
import org.swlab.etcetera.Repositories.HiddenExchangeRepository;
import org.swlab.etcetera.Util.HiddenMaterialConfig;

import java.util.HashMap;

/**
 * 히든 재료 교환 GUI 클릭 처리 + 주간 초기화(WeeklyResetEvent) 처리.
 */
public class HiddenExchangeListener implements Listener {

    private static final String PASTEL_MINT = "#B5EAD7";
    private static final String PASTEL_PINK = "#FFB7C5";
    private static final String PASTEL_RED = "#FF9AA2";

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof HiddenExchangeGui gui)) {
            return;
        }
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        // 하단(플레이어) 인벤토리 클릭은 무시
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getInventory())) {
            return;
        }

        String material = gui.getMaterial(e.getRawSlot());
        if (material == null) {
            return;
        }

        if (gui.getStage() == HiddenExchangeGui.Stage.SELECT_SOURCE) {
            HiddenExchangeGui.openTargetSelect(player, material);
            return;
        }

        doExchange(player, gui.getSourceId(), material);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof HiddenExchangeGui) {
            e.setCancelled(true);
        }
    }

    private void doExchange(Player player, String source, String target) {
        if (source == null || target == null) {
            return;
        }
        if (source.equals(target)) {
            player.sendMessage(ColorManager.format(PASTEL_RED + "[ 어둠의 연금술사 ] §f같은 재료로는 교환할 수 없습니다."));
            return;
        }

        HiddenExchangeRepository repository = HiddenExchangeRepository.getInstance();
        if (!repository.canExchange(player)) {
            player.sendMessage(ColorManager.format(PASTEL_RED + "[ 어둠의 연금술사 ] §f이번 주 교환 횟수를 모두 사용했습니다."));
            player.closeInventory();
            return;
        }

        // 보상 아이템을 먼저 생성해 유효성 확인 (소모 전에 실패 방지)
        ItemStack reward = MMOItems.plugin.getItem(HiddenMaterialConfig.HIDDEN_MATERIAL_TYPE, target);
        if (reward == null) {
            player.sendMessage(ColorManager.format(PASTEL_RED + "[ 어둠의 연금술사 ] §f교환할 재료 정보를 찾을 수 없습니다."));
            return;
        }

        int coreCount = countMMOItem(player, HiddenMaterialConfig.COST_ITEM_TYPE, HiddenMaterialConfig.COST_ITEM_ID);
        if (coreCount < HiddenMaterialConfig.COST_AMOUNT) {
            String costName = HiddenExchangeGui.itemName(HiddenMaterialConfig.COST_ITEM_TYPE, HiddenMaterialConfig.COST_ITEM_ID);
            player.sendMessage(ColorManager.format(PASTEL_RED + "[ 어둠의 연금술사 ] §f") + costName
                    + ColorManager.format(PASTEL_RED + " §f이(가) 부족합니다. (§c" + coreCount + "§f / " + HiddenMaterialConfig.COST_AMOUNT + "개)"));
            return;
        }

        int sourceCount = countMMOItem(player, HiddenMaterialConfig.HIDDEN_MATERIAL_TYPE, source);
        if (sourceCount < 1) {
            player.sendMessage(ColorManager.format(PASTEL_RED + "[ 어둠의 연금술사 ] §f교환할 재료를 가지고 있지 않습니다."));
            return;
        }

        // 소모 : 원초의핵 15개 + 원본 재료 1개
        removeMMOItem(player, HiddenMaterialConfig.COST_ITEM_TYPE, HiddenMaterialConfig.COST_ITEM_ID, HiddenMaterialConfig.COST_AMOUNT);
        removeMMOItem(player, HiddenMaterialConfig.HIDDEN_MATERIAL_TYPE, source, 1);

        reward.setAmount(1);
        giveOrDrop(player, reward);

        repository.increaseUsedCount(player);

        String sourceName = HiddenExchangeGui.materialName(source);
        String targetName = HiddenExchangeGui.materialName(target);

        player.closeInventory();
        player.sendMessage(ColorManager.format(PASTEL_MINT + "[ 어둠의 연금술사 ] §f교환이 완료되었습니다!"));
        player.sendMessage(ColorManager.format(PASTEL_PINK + "  §7- §f") + sourceName
                + ColorManager.format(" §7→ §f") + targetName);
        player.sendMessage(ColorManager.format(PASTEL_MINT + "  §f이번 주 남은 횟수 : §a" + repository.getRemaining(player)
                + "§f / " + HiddenMaterialConfig.WEEKLY_LIMIT + "회"));
    }

    private boolean matches(ItemStack item, String type, String id) {
        String itemId = MMOItems.getID(item);
        if (itemId == null || !itemId.equals(id)) {
            return false;
        }
        String typeName = MMOItems.getTypeName(item);
        return typeName != null && typeName.equals(type);
    }

    private int countMMOItem(Player player, String type, String id) {
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

    private void giveOrDrop(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }
    }

    @EventHandler
    public void onWeeklyReset(WeeklyResetEvent e) {
        HiddenExchangeRepository.getInstance().resetDatas();
    }
}
