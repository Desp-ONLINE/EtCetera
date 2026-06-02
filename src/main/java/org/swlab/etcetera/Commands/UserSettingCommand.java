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
            case "데미지":
                userSettingRepository.toggleShowDamageChat(player);
                return true;
            case "쿨타임":
                userSettingRepository.toggleShowSkillCooldownNotice(player);
                return true;
            case "쿨타임표시":
                userSettingRepository.toggleShowSkillCooldownItem(player);
                return true;
            case "시간":
                if (strings.length < 2) {
                    showPlayerCommandUsage(player);
                    return true;
                }
                switch (strings[1]) {
                    case "낮":
                        userSettingRepository.setPlayerTime(player, 3000, "낮");
                        break;
                    case "밤":
                        userSettingRepository.setPlayerTime(player, 15000, "밤");
                        break;
                    case "새벽":
                        userSettingRepository.setPlayerTime(player, 23000, "새벽");
                        break;
                    case "기본":
                        userSettingRepository.setPlayerTime(player, -1, "기본");
                        break;
                    default:
                        showPlayerCommandUsage(player);
                }
                return true;
            default:
                showPlayerCommandUsage(player);
        }


        return true;
    }

    public void showPlayerCommandUsage(Player player){
        player.sendMessage("");
        player.sendMessage("§7  /설정 정보 §f- 전체 채팅에서 플레이어 닉네임에 마우스 커서를 올리면 공개되는 정보 여부를 공개/비공개 합니다.");
        player.sendMessage("§7  /설정 데미지 §f- 채팅에 데미지를 넣을 때 마다 출력합니다.");
        player.sendMessage("§7  /설정 쿨타임 §f- 특수무기/합성무기 스킬의 쿨타임 종료 알림을 켜고/끕니다.");
        player.sendMessage("§7  /설정 쿨타임표시 §f- 무기 아이템에 스킬 쿨타임 표시 여부를 켜고/끕니다.");
        player.sendMessage("§7  /설정 시간 <낮/밤/새벽/기본> §f- 나에게만 보이는 하늘 시간을 고정합니다. (기본: 서버 시간)");
        player.sendMessage("");

    }
}
