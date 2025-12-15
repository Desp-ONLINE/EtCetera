package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;
import org.swlab.etcetera.Util.CommandUtil;

public class UserSettingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        UserSettingRepository userSettingRepository = UserSettingRepository.getInstance();

        if (strings.length == 0) {
            showPlayerCommandUsage(player);
            return false;
        }
        String arg1 = strings[0];

        switch (arg1){
            case "정보":
                userSettingRepository.toggleVisibleInformation(player);
                return true;
            default:
                showPlayerCommandUsage(player);
        }


        return true;
    }

    public void showPlayerCommandUsage(Player player){
        player.sendMessage("");
        player.sendMessage("§7  /설정 정보 §f- 전체 채팅에서 플레이어 닉네임에 마우스 커서를 올리면 공개되는 정보 여부를 공개/비공개 합니다.");
        player.sendMessage("");

    }
}
