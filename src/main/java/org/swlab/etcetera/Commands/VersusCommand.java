package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class VersusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
//        if (!EtCetera.getChannelType().equals("pvp")) {
//            player.sendMessage("§c (/pvp) 채널 에서만 이용하실 수 있습니다.");
//            return false;
//        }
        if (args.length == 0) {
            player.sendMessage(ColorManager.format("#5798FF /대전 매칭 &f- 대전 매칭 대기열에 입장합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대전 취소 &f- 대전 매칭 대기열에 퇴장합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대전 관전 <닉네임> &f- 대전 중인 해당 플레이어의 경기를 관전합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대전 칭호 &f- 대전 1/2/3위 한정 칭호를 활성/비활성화 합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대전 보상 &f- 대전을 통해 획득 가능한 보상을 확인합니다."));
            player.sendMessage(ColorManager.format("#5798FF /대전 순위 &f- 대전 전체 TOP 10과 내 정보를 확인합니다."));
            player.sendMessage("");
            player.sendMessage(ColorManager.format("#5798FF [!] 대전 보상은 위키에서 확인하실 수 있습니다!"));
            player.sendMessage("");
            return true;
        }
        switch (args[0]) {
            case "매칭":
                CommandUtil.runCommandAsOP(player, "rank queue");
                break;
            case "칭호":
                CommandUtil.runCommandAsOP(player, "rank title");
                break;
            case "순위":
                CommandUtil.runCommandAsOP(player, "rank top");
                break;
            case "보상":
                CommandUtil.runCommandAsOP(player, "rank reward");
                break;
            case "취소":
                CommandUtil.runCommandAsOP(player, "rank leave");
                break;
            case "관전":
                if(args[1].isEmpty()){
                    player.sendMessage(ColorManager.format("#5798FF /대전 관전 <닉네임> &f- 대전 중인 해당 플레이어의 경기를 관전합니다."));
                    break;
                }
                CommandUtil.runCommandAsOP(player,"rank spectate "+args[1]);
        }
        return true;
    }
}
