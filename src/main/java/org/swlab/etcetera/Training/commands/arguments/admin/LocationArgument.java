package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.objects.RoomCreator;

public class LocationArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;

        RoomCreator roomCreator = RoomCreator.get(player, -1);
        if (!roomCreator.isRegistered()) {
            player.sendMessage(ColorManager.format("#FF6B6B생성 모드가 아닙니다."));
            return false;
        }
        roomCreator.setLocation(player.getLocation());
        player.sendMessage(ColorManager.format("#55FF55텔레포트 위치를 설정했습니다."));
        return true;
    }

    @Override
    public String getArg() {
        return "위치설정";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "플레이어 텔레포트 위치를 설정합니다.";
    }

    @Override
    public String getPermission() {
        return "training.admin.create";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
