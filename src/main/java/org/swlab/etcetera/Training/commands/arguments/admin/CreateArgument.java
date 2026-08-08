package org.swlab.etcetera.Training.commands.arguments.admin;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.objects.RoomCreator;
import org.swlab.etcetera.Training.objects.TrainingRoom;
import org.swlab.etcetera.Training.repository.TrainingRoomRepository;

public class CreateArgument implements CommandArgument {

    private final TrainingRoomRepository repository = TrainingManager.getInstance().getRoomRepository();

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int i = NumberUtil.parseInt(strings[1]);
        if (i <= 0) {
            sender.sendMessage(ColorManager.format("#FF6B6B숫자를 입력해 주세요."));
            return false;
        }
        TrainingRoom trainingRoom = repository.get(i);
        if (trainingRoom != null) {
            sender.sendMessage(ColorManager.format("#FF6B6B이미 존재하는 아이디입니다."));
        } else {
            RoomCreator roomCreator = RoomCreator.get(((Player) sender), i);
            roomCreator.register();
            sender.sendMessage(ColorManager.format("""

                  #FFD700━━━ #FFA500훈련장 생성 모드 #FFD700━━━
                  #55FF551단계 #777777: #FFFFFF/훈련관리 위치설정
                  #55FF552단계 #777777: #FFFFFF/훈련관리 엔티티위치설정
                  #55FF553단계 #777777: #FFFFFF/훈련관리 저장
                """));
        }

        return false;
    }

    @Override
    public String getArg() {
        return "생성";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "<ID> - 훈련장을 생성합니다.";
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
