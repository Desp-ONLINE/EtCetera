package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadArgument implements CommandArgument {
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        sender.sendMessage(ColorManager.format("#55FF55리로드 완료."));
        return true;
    }

    @Override
    public String getArg() {
        return "리로드";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "설정을 리로드합니다.";
    }

    @Override
    public String getPermission() {
        return "training.admin.reload";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
