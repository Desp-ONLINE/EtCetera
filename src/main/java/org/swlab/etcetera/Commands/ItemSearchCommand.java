package org.swlab.etcetera.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Convinience.ItemSearchGui;

import java.util.List;

/**
 * /템 <ID>
 * 타입 상관없이 ID 에 <ID> 가 포함된 모든 MMOItem 을 GUI 로 보여주고, 클릭하면 지급한다. OP 전용.
 */
public class ItemSearchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다.");
            return true;
        }
        if (!player.isOp()) {
            return true;
        }
        if (args.length < 1) {
            player.sendMessage("§c사용법: /템 <ID>");
            return true;
        }

        List<ItemSearchGui.Entry> entries = ItemSearchGui.search(args[0]);
        if (entries.isEmpty()) {
            player.sendMessage("§cID 에 '" + args[0] + "' 이(가) 포함된 아이템이 없습니다.");
            return true;
        }
        ItemSearchGui.open(player, args[0], entries);
        return true;
    }
}
