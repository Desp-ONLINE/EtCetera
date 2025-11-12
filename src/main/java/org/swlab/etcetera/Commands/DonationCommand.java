package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DonationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        player.sendMessage("");
        player.sendMessage(" §b> §fIDE ONLINE에 후원해주셔서 감사합니다.");
        player.sendMessage(" §b> §f후원금은 100원 당 §c1 루비§f로 보상을 지급해드리고 있습니다.");
        player.sendMessage(" §b> §f후원은 아래사이트에서 진행하실 수 있습니다.");
        player.sendMessage(" §b> §f관련 명령어로 /후원상점 을 참고해주세요.");
        player.sendMessage("");
        player.sendMessage(" §b> §6계좌이체/무통장입금 후원: §fhttps://iderpg.codix.kr");
        player.sendMessage(" §b> §a상품권류 후원: §fhttps://skhcs.com/ide");
        player.sendMessage(" ");
        player.sendMessage(" §c※ 이용 약관은 필수로 확인해주세요.");
        player.sendMessage(" ");
        return false;
    }
}
