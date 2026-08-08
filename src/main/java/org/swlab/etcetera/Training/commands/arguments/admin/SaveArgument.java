package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.objects.RoomCreator;

public class SaveArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        RoomCreator roomCreator = RoomCreator.get(player, -1);
        if (!roomCreator.isRegistered()) {
            player.sendMessage(ColorManager.format("#FF6B6B생성 모드가 아닙니다."));
            return false;
        }
        Location location = roomCreator.getLocation();
        Location entityLocation = roomCreator.getEntityLocation();

        if (location == null) {
            player.sendMessage(ColorManager.format("#FF6B6B텔레포트 위치가 설정되지 않았습니다."));
            return false;
        }
        if (entityLocation == null) {
            player.sendMessage(ColorManager.format("#FF6B6B허수아비 위치가 설정되지 않았습니다."));
            return false;
        }
        roomCreator.create();
        roomCreator.unregister();
        player.sendMessage(ColorManager.format("#55FF55%dID #FFFFFF훈련장이 생성되었습니다.".formatted(roomCreator.getRoomId())));
        return true;
    }

    @Override
    public String getArg() {
        return "저장";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "훈련장 설정을 저장합니다.";
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
