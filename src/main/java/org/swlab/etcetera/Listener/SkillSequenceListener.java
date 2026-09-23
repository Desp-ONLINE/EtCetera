package org.swlab.etcetera.Listener;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.swlab.etcetera.Convinience.SkillSequenceGui;
import org.swlab.etcetera.Convinience.SkillSequenceRunner;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.SkillSequenceRepository;
import org.swlab.etcetera.Util.MergedWeaponUtil;

import java.util.List;

/**
 * 자동 연계 시스템 : 트리거 아이템 우클릭(시전) / 웅크리고 우클릭(등록 GUI), 1번째 무기 스킬 직접 시전 시 나머지 자동 연계,
 * 등록 GUI 클릭 처리, 접속 시 로드·퇴장 시 저장.
 */
public class SkillSequenceListener implements Listener {

    public static final String DEFAULT_TRIGGER_ITEM_ID = "기타_자동연계";

    public static String getTriggerItemId() {
        return EtCetera.getInstance().getConfig().getString("skillSequence.triggerItemId", DEFAULT_TRIGGER_ITEM_ID);
    }

    public static boolean isTriggerItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        String id = MMOItems.getID(item);
        return id != null && id.equals(getTriggerItemId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTriggerUse(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isTriggerItem(e.getItem())) return;

        // MMOItems 등 다른 플러그인이 아이템 사용/블록 상호작용으로 처리하지 않게 막는다.
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setCancelled(true);

        Player player = e.getPlayer();
        if (player.isSneaking()) {
            SkillSequenceGui.open(player);
        } else {
            SkillSequenceRunner.run(player);
        }
    }

    /**
     * 1번째로 등록한 무기의 스킬을 플레이어가 직접(우클릭 등) 시전하면 2번째 무기부터 자동 연계한다.
     * 자동 연계가 시전하는 스킬(TriggerType.API)은 제외해 재귀 발동을 막는다.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFirstWeaponCast(PlayerCastSkillEvent e) {
        Player player = e.getPlayer();
        // 서버의 MythicLib 빌드에는 SkillMetadata.getTrigger() 가 없으므로 트리거 종류 대신 실행기 플래그로 자동 연계 시전을 구분한다.
        String castName = e.getCast().getClass().getSimpleName() + "/" + e.getCast().getHandler().getId();
        SkillSequenceRunner.debug(player, "스킬 시전 감지: " + castName);

        if (SkillSequenceRunner.isCastingByRunner(player)) {
            SkillSequenceRunner.debug(player, "무시: 자동 연계가 시전한 스킬");
            return;
        }
        if (!(e.getCast() instanceof AbilityData ability)) {
            SkillSequenceRunner.debug(player, "무시: 아이템 스킬(AbilityData)이 아님");
            return;
        }
        if (SkillSequenceRunner.isRunning(player)) {
            SkillSequenceRunner.debug(player, "무시: 이미 연계 시전 중");
            return;
        }

        List<String> sequence = SkillSequenceRepository.getInstance().getSequence(player);
        if (sequence.size() < 2) {
            SkillSequenceRunner.debug(player, "무시: 등록 무기 " + sequence.size() + "개 (2개 이상 필요)");
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        String heldKey = MergedWeaponUtil.keyOf(held);
        if (heldKey == null || !heldKey.equals(sequence.get(0))) {
            SkillSequenceRunner.debug(player, "무시: 손에 든 무기 키=" + heldKey + ", 1번째 등록=" + sequence.get(0));
            return;
        }
        // 손에 든 1번째 무기의 스킬이 맞는지 확인 (다른 출처의 스킬로는 발동하지 않음). 핸들러 ID + 트리거로 비교한다.
        boolean belongsToHeld = false;
        for (AbilityData heldAbility : MergedWeaponUtil.activeAbilities(held)) {
            if (heldAbility.getHandler().getId().equals(ability.getHandler().getId())
                    && heldAbility.getTrigger().name().equals(ability.getTrigger().name())) {
                belongsToHeld = true;
                break;
            }
        }
        if (!belongsToHeld) {
            SkillSequenceRunner.debug(player, "무시: 손에 든 무기의 스킬이 아님 (" + ability.getHandler().getId() + "@" + ability.getTrigger().name() + ")");
            return;
        }

        SkillSequenceRunner.debug(player, "1번째 무기 스킬 확인 → 다음 틱에 2번째부터 연계 시작");
        // 스킬 시전 이벤트 처리 중에 다른 스킬을 시전하지 않도록 다음 틱에 시작
        Bukkit.getScheduler().runTask(EtCetera.getInstance(), () -> {
            if (player.isOnline()) SkillSequenceRunner.runChained(player);
        });
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof SkillSequenceGui gui)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        SkillSequenceRepository repository = SkillSequenceRepository.getInstance();

        // 아래(플레이어) 인벤토리 클릭 : 합성무기 등록
        if (!e.getClickedInventory().equals(e.getInventory())) {
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            String key = MergedWeaponUtil.keyOf(clicked);
            if (key == null) {
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "§c합성무기만 등록할 수 있습니다.");
                return;
            }
            if (repository.contains(player, key)) {
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "§c이미 등록된 무기입니다.");
                return;
            }
            if (repository.getSequence(player).size() >= SkillSequenceRepository.MAX_SIZE) {
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "§c최대 " + SkillSequenceRepository.MAX_SIZE + "개까지 등록할 수 있습니다.");
                return;
            }
            if (repository.add(player, key)) {
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + MergedWeaponUtil.displayName(clicked) + "§f을(를) §e"
                        + repository.getSequence(player).size() + "번째§f로 등록했습니다.");
                gui.render();
            }
            return;
        }

        // 위(GUI) 인벤토리 클릭
        int rawSlot = e.getRawSlot();
        int index = SkillSequenceGui.sequenceIndexOf(rawSlot);
        if (index >= 0) {
            if (index >= repository.getSequence(player).size()) return;
            if (e.isRightClick()) {
                if (repository.moveForward(player, index)) gui.render();
            } else {
                String key = repository.getSequence(player).get(index);
                if (repository.remove(player, index)) {
                    player.sendMessage(SkillSequenceRunner.MSG_PREFIX + MergedWeaponUtil.displayNameOfKey(key) + "§f의 등록을 해제했습니다.");
                    gui.render();
                }
            }
            return;
        }

        if (rawSlot == SkillSequenceGui.SLOT_CAST) {
            player.closeInventory();
            SkillSequenceRunner.run(player);
            return;
        }
        if (rawSlot == SkillSequenceGui.SLOT_CLEAR) {
            repository.clear(player);
            player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "등록된 무기를 모두 해제했습니다.");
            gui.render();
        }
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof SkillSequenceGui) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        SkillSequenceRepository.getInstance().loadUserData(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SkillSequenceRepository repository = SkillSequenceRepository.getInstance();
        repository.saveUserData(e.getPlayer());
        repository.unload(e.getPlayer());
    }
}
