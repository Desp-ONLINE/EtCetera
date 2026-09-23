package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

/**
 * IDETuto(신규 튜토리얼) 엔딩 후 로비 도착 시 띄우는 회귀 연출.
 * 튜토 서버에서 "채널 워프 lobby 튜토메시지" 로 넘어오며 실행된다.
 * 실행 즉시 튜토리얼 클리어로 처리하므로(DB 저장 + 권한 부여),
 * 이후 DataLoadEvent 의 미완료자 튜토리얼 강제 이동에 걸리지 않는다.
 */
public class TutorialMessageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        // 연출보다 먼저 클리어 처리 (DataSyncListener 의 100틱 뒤 미완료 체크보다 앞서야 함)
        TutorialCompleteCommand.markTutorialCleared(player);

        // 채널 이동 직후라 화면이 아직 로딩 중일 수 있어 잠시 뒤에 시작
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (!player.isOnline()) return;
            player.sendTitle("§8", "§7눈을 뜨자, 낯익은 마을이었다.", 10, 60, 20);
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.6f, 0.8f);
        }, 20);
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (!player.isOnline()) return;
            player.sendTitle("§7...분명 죽었을 텐데.", "§f어떻게 된거지.", 10, 60, 20);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 0.7f);
        }, 20 + 90);
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (!player.isOnline()) return;
            player.sendMessage("");
            player.sendMessage("§e[!] §fIDE ONLINE에서의 새로운 시간이 시작되었습니다.");
            player.sendMessage("§7앞의 메인 퀘스트 NPC, §e제미나이§7에게 말을 걸어 여정을 시작하세요.");
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        }, 20 + 180);
        // 연출이 끝난 뒤 실제 직업 선택 (/튜토완료 와 동일)
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (!player.isOnline()) return;
            CommandUtil.runCommandAsOP(player, "직업");
        }, 20 + 180 + 60);
        return true;
    }
}
