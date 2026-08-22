package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.gui.TrainingInfoGui;

public class InfoArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        TrainingInfoGui.open((Player) sender, 0);
        return true;
    }

    @Override
    public String getArg() {
        return "정보";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "플레이어별 스텟/사용 스킬 훈련 기록을 GUI 로 확인합니다.";
    }

    @Override
    public String getPermission() {
        return "training.admin.info";
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }
}
