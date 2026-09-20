package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.cooldown.CooldownInfo;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Repositories.UserSettingRepository;

/**
 * /쿨타임감소 <초> [플레이어]
 * 대상의 핫바 슬롯(/설정 쿨타임감소 <1~9> 로 지정, 기본 2번) 무기의 스킬 남은 쿨타임을 <초>만큼 감소시킨다.
 * 권한 제한 없음. 콘솔/스킬 실행 시 대상 플레이어 지정 가능.
 */
public class CooldownReduceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§c사용법: /쿨타임감소 <초> [플레이어]");
            return true;
        }

        double seconds;
        try {
            seconds = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c수치는 숫자(초)여야 합니다: " + args[0]);
            return true;
        }
        if (seconds <= 0) {
            sender.sendMessage("§c수치는 0보다 커야 합니다.");
            return true;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§c접속 중인 플레이어가 아닙니다: " + args[1]);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c콘솔에서는 플레이어를 지정해야 합니다: /쿨타임감소 <초> <플레이어>");
                return true;
            }
            target = (Player) sender;
        }

        int slotNumber = UserSettingRepository.getInstance().getCooldownReduceSlot(target); // 1~9
        ItemStack item = target.getInventory().getItem(slotNumber - 1);
        if (item == null || item.getType() == Material.AIR || MMOItems.getType(item) == null) {
            sender.sendMessage("§c" + slotNumber + "번 슬롯에 무기가 없습니다!");
            return true;
        }

        // 합성무기는 템플릿이 없을 수 있으므로 실제 아이템(NBT)에서 스킬 목록을 읽는다.
        LiveMMOItem liveMMOItem = new LiveMMOItem(item);
        if (!liveMMOItem.hasData(ItemStats.ABILITIES)) {
            sender.sendMessage("§c" + slotNumber + "번 슬롯 무기에 스킬이 없습니다.");
            return true;
        }
        AbilityListData abilities = (AbilityListData) liveMMOItem.getData(ItemStats.ABILITIES);
        if (abilities == null || abilities.getAbilities().isEmpty()) {
            sender.sendMessage("§c" + slotNumber + "번 슬롯 무기에 스킬이 없습니다.");
            return true;
        }

        CooldownMap cooldownMap = MMOPlayerData.get(target.getUniqueId()).getCooldownMap();
        int reduced = 0;
        for (AbilityData ability : abilities.getAbilities()) {
            // 스킬 시전 시 MythicLib 이 사용하는 키(skill_<handlerId>)와 동일한 경로로 조회
            CooldownInfo info = cooldownMap.getInfo(ability.getCooldownPath());
            if (info == null || info.hasEnded()) continue;

            // 남은 쿨보다 많이 빼면 nextUse 가 과거로 밀리므로 남은 시간까지만 감소
            double remainingSeconds = info.getRemaining() / 1000.0;
            info.reduceFlat(Math.min(seconds, remainingSeconds));
            reduced++;
        }

        if (reduced == 0) {
            sender.sendMessage("§e" + slotNumber + "번 슬롯 무기의 스킬이 모두 쿨타임 상태가 아닙니다.");
            return true;
        }

        // 아이템 아이콘 쿨타임 오버레이(SkillCooldownNotice)가 다음 폴링에서 서버 쿨보다 짧아진 것을 감지해 맞춘다.
        String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                ? ColorManager.format(item.getItemMeta().getDisplayName())
                : item.getType().name();
        target.sendMessage("§a[알림] §f" + itemName + "§f의 스킬 쿨타임이 §e" + trim(seconds) + "초 §f감소했습니다.");
        if (sender != target) {
            sender.sendMessage("§a" + target.getName() + " 님의 " + itemName + "§a의 스킬 쿨타임이 " + trim(seconds) + "초 감소했습니다.");
        }
        return true;
    }

    private static String trim(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
