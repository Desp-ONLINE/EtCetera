package org.swlab.etcetera.Training.commands;

import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.Training.commands.arguments.user.RankingArgument;
import org.swlab.etcetera.Training.objects.TrainingController;
import org.swlab.etcetera.Training.objects.TrainingRoom;

import java.util.List;

public class UserCommand extends BetterCommand {

    @Override
    public String getCommand() {
        return "훈련";
    }

    @Override
    public boolean isSingleCommand() {
        return false;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return List.of(new RankingArgument());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            TrainingController controller = TrainingController.get(player);
            if (controller != null) {
                controller.getRoom().quit();
                player.performCommand("spawn");
            } else {
                TrainingRoom empty = TrainingRoom.findEmpty();
                if (empty == null) {
                    player.sendMessage(ColorManager.format("#FF6B6B훈련장이 꽉 찼습니다. 잠시 후 다시 시도해 주세요."));
                    return true;
                }
                empty.join(player);
            }
            return true;
        }

        if (args[0].equals("도움말")) {
            sendHelp(player);
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    private void sendHelp(Player player) {
        player.sendMessage(ColorManager.format("""

              #FFD700━━━━━━ #FFA500훈련장 명령어 #FFD700━━━━━━

              #FFA500/훈련  #777777- #FFFFFF훈련 참가 / 퇴장 (토글)
              #FFA500/훈련 랭킹 <직업|모두>  #777777- #FFFFFF랭킹 조회
              #FFA500/훈련 도움말  #777777- #FFFFFF명령어 안내

              #FFD700━━━━━━━━━━━━━━━━━━━━
            """));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0];
            return List.of("랭킹", "도움말").stream()
                    .filter(s -> s.startsWith(input))
                    .toList();
        }
        if (args.length == 2 && args[0].equals("랭킹")) {
            String input = args[1];
            return RankingArgument.TAB_OPTIONS.stream()
                    .filter(job -> job.startsWith(input))
                    .toList();
        }
        return List.of();
    }
}
