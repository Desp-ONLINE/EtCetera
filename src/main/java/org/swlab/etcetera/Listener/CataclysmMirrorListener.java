package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Convinience.CataclysmMirrorGui;
import org.swlab.etcetera.Util.JobWeaponUtil;
import org.swlab.etcetera.Util.JobWeaponUtil.JobWeapon;

/**
 * 격변의 거울 : 우클릭으로 GUI 를 열고, 인벤토리의 직업 무기를
 * 같은 차수/강화 등급의 다른 직업 무기로 변환한다.
 */
public class CataclysmMirrorListener implements Listener {

    private static final String HEADER = "#FFF285  [ 격변의 거울 ] §f";
    private static final String ERROR = "#FF8888 ";

    @EventHandler
    public void onUseMirror(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!matches(item, CataclysmMirrorGui.MIRROR_TYPE, CataclysmMirrorGui.MIRROR_ID)) {
            return;
        }
        e.setCancelled(true);
        CataclysmMirrorGui.openWeaponSelect(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof CataclysmMirrorGui gui)) {
            return;
        }
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }

        if (gui.getStage() == CataclysmMirrorGui.Stage.SELECT_WEAPON) {
            // 1단계 : 하단(내 인벤토리) 클릭만 처리
            if (e.getClickedInventory().equals(e.getInventory())) {
                return;
            }
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }
            JobWeapon weapon = JobWeaponUtil.parse(clicked);
            if (weapon == null) {
                player.sendMessage(ColorManager.format(ERROR + "직업 무기만 변환할 수 있습니다."));
                return;
            }
            CataclysmMirrorGui.openTargetSelect(player, e.getSlot(), MMOItems.getID(clicked), weapon);
            return;
        }

        // 2단계 : 상단(GUI) 클릭만 처리
        if (!e.getClickedInventory().equals(e.getInventory())) {
            return;
        }
        String targetJob = gui.getJob(e.getRawSlot());
        if (targetJob == null) {
            return;
        }
        convert(player, gui, targetJob);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof CataclysmMirrorGui) {
            e.setCancelled(true);
        }
    }

    private void convert(Player player, CataclysmMirrorGui gui, String targetJob) {
        JobWeapon source = gui.getSource();
        int slot = gui.getSourceSlot();

        // GUI 를 여는 사이 무기가 옮겨졌을 수 있으므로 슬롯을 다시 검증
        ItemStack current = player.getInventory().getItem(slot);
        String currentId = current == null ? null : MMOItems.getID(current);
        if (currentId == null || !currentId.equals(gui.getSourceId())) {
            player.closeInventory();
            player.sendMessage(ColorManager.format(ERROR + "변환할 무기를 찾을 수 없습니다. 다시 시도해주세요."));
            return;
        }

        // 6차 이상 무기는 변환할 직업의 6차 전직 퀘스트를 클리어해야 변환 가능
        if (JobWeaponUtil.requiresTier6Quest(source.tier())
                && !JobWeaponUtil.hasFinishedTier6Quest(player, targetJob)) {
            player.sendMessage(ColorManager.format(ERROR + "초월 이후 무기는 해당 직업의 초월 전직 퀘스트를 클리어해야 변환할 수 있습니다."));
            return;
        }

        // 보상 아이템을 먼저 생성해 유효성 확인 (소모 전에 실패 방지)
        String targetId = JobWeaponUtil.buildId(source.tier(), targetJob, source.enhance());
        ItemStack converted = MMOItems.plugin.getItem(JobWeaponUtil.WEAPON_TYPE, targetId);
        if (converted == null) {
            player.sendMessage(ColorManager.format(ERROR + "변환할 무기 정보를 찾을 수 없습니다."));
            return;
        }

        if (countMMOItem(player, CataclysmMirrorGui.MIRROR_TYPE, CataclysmMirrorGui.MIRROR_ID) < 1) {
            player.closeInventory();
            player.sendMessage(ColorManager.format(ERROR + "격변의 거울을 가지고 있지 않습니다."));
            return;
        }

        // 소모 : 격변의 거울 1개, 원본 무기는 새 무기로 교체
        removeMMOItem(player, CataclysmMirrorGui.MIRROR_TYPE, CataclysmMirrorGui.MIRROR_ID, 1);
        converted.setAmount(1);
        player.getInventory().setItem(slot, converted);
        player.updateInventory();

        player.closeInventory();
        player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1.2f);
        player.sendMessage(ColorManager.format(HEADER + "무기 변환이 완료되었습니다!"));
        player.sendMessage(ColorManager.format("#A5D8FF  - §f") + CataclysmMirrorGui.weaponName(gui.getSourceId())
                + ColorManager.format(" §7→ §f") + CataclysmMirrorGui.weaponName(targetId));
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
}
