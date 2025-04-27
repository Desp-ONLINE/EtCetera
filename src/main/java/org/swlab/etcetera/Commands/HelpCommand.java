package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        player.sendMessage("");
        player.sendMessage(" §b> §f아래 사이트에서 서버에 대한 추가적인 정보를 확인해보실 수 있습니다!");
        player.sendMessage(" §f§ohttps://mysterious-physician-12f.notion.site/IDE-ONLINE-1dc3f2fd56f58041842aed7a2066ac45");
        player.sendMessage(" ");
        return false;
    }
}
