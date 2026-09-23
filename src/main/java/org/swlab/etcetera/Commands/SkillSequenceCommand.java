package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Convinience.SkillSequenceGui;
import org.swlab.etcetera.Convinience.SkillSequenceRunner;
import org.swlab.etcetera.Repositories.SkillSequenceRepository;

import java.util.List;

/**
 * /스킬          - 자동 연계 시스템 등록 GUI
 * /스킬 시전     - 등록한 순서대로 즉시 시전 (트리거 아이템 우클릭과 동일)
 * /스킬 초기화   - 등록 전체 해제
 */
public class SkillSequenceCommand implements TabExecutor {

    private static final List<String> SUB_COMMANDS = List.of("시전", "초기화", "디버그", "도움말");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다.");
            return true;
        }
        if (!SkillSequenceRepository.getInstance().isLoaded(player)) {
            SkillSequenceRepository.getInstance().loadUserData(player);
        }

        if (args.length == 0) {
            SkillSequenceGui.open(player);
            return true;
        }

        switch (args[0]) {
            case "시전", "사용" -> SkillSequenceRunner.run(player);
            case "초기화" -> {
                SkillSequenceRepository.getInstance().clear(player);
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "등록된 무기를 모두 해제했습니다.");
            }
            case "디버그" -> {
                if (!player.isOp()) {
                    sendUsage(player);
                    return true;
                }
                boolean on = SkillSequenceRunner.toggleDebug(player);
                player.sendMessage(SkillSequenceRunner.MSG_PREFIX + "디버그 출력을 " + (on ? "§a켰습니다" : "§c껐습니다") + "§f. (채팅 + 콘솔)");
            }
            default -> sendUsage(player);
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("");
        player.sendMessage("§a[자동 연계 시스템] §f등록한 합성무기의 스킬을 순서대로 자동 시전합니다.");
        player.sendMessage("§7  /스킬 §f- 무기 등록 GUI 를 엽니다.");
        player.sendMessage("§7  /스킬 시전 §f- 등록한 순서대로 즉시 시전합니다.");
        player.sendMessage("§7  /스킬 초기화 §f- 등록을 모두 해제합니다.");
        player.sendMessage("§7  자동 연계 아이템 우클릭 §f- 시전 / §7웅크리고 우클릭 §f- 등록 GUI");
        player.sendMessage("§7  1번째 무기의 스킬을 직접 사용 §f- 2번째부터 자동 연계");
        player.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return SUB_COMMANDS.stream().filter(s -> s.startsWith(args[0])).toList();
        }
        return List.of();
    }
}
