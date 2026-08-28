package org.swlab.etcetera.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Ranking.RankingHologramManager;

import java.util.ArrayList;
import java.util.List;

/**
 * /랭킹위치설정 <컨텐츠이름> - 해당 컨텐츠의 랭킹 홀로그램을 현재 위치에 배치한다. (OP 전용)
 * 외부 플러그인(바벨탑, 길드레이드 등)이 등록한 컨텐츠도 여기서 통합 관리한다.
 */
public class RankingLocationCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        RankingHologramManager manager = RankingHologramManager.getInstance();
        if (manager == null) {
            player.sendMessage("§c랭킹 관리자가 아직 활성화되지 않았습니다.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c사용법: /랭킹위치설정 <컨텐츠이름>");
            player.sendMessage("§7이 서버에 등록된 컨텐츠: §f" + String.join("§7, §f", manager.getContentKeys()));
            return true;
        }

        String key = args[0];
        // 서 있는 위치보다 2블럭 위에 홀로그램 중심이 오도록 배치
        Location location = player.getLocation().add(0, 2.0, 0);
        if (!manager.setLocation(key, location)) {
            player.sendMessage("§c등록되지 않은 컨텐츠입니다: §f" + key);
            player.sendMessage("§7이 서버에 등록된 컨텐츠: §f" + String.join("§7, §f", manager.getContentKeys()));
            return true;
        }
        player.sendMessage("§a[" + key + "] 랭킹 홀로그램 위치를 현재 위치로 설정했습니다. 잠시 후 랭킹이 표시됩니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        RankingHologramManager manager = RankingHologramManager.getInstance();
        if (manager == null || args.length != 1) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String key : manager.getContentKeys()) {
            if (key.startsWith(args[0])) {
                result.add(key);
            }
        }
        return result;
    }
}
