package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.objects.TrainingRoom;
import org.swlab.etcetera.Training.repository.TrainingRoomRepository;

public class DeleteArgument implements CommandArgument {

    private final TrainingRoomRepository repository = TrainingManager.getInstance().getRoomRepository();

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int i = NumberUtil.parseInt(strings[1]);
        if (i <= 0) {
            sender.sendMessage(ColorManager.format("#FF6B6B숫자를 입력해 주세요."));
            return false;
        }

        TrainingRoom trainingRoom = repository.get(i);
        if (trainingRoom == null) {
            sender.sendMessage(ColorManager.format("#FF6B6B존재하지 않는 아이디입니다."));
            return false;
        }
        repository.remove(i);
        repository.deleteById(i);
        sender.sendMessage(ColorManager.format("#55FF55%dID #FFFFFF훈련장을 삭제했습니다.".formatted(i)));
        return true;
    }

    @Override
    public String getArg() {
        return "삭제";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "<ID> - 훈련장을 삭제합니다.";
    }

    @Override
    public String getPermission() {
        return "training.admin.delete";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
