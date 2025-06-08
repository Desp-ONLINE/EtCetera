package org.swlab.etcetera.Commands;

import fr.nocsy.mcpets.api.MCPetsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestSkipCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(!player.isOp()){
            return false;
        }
        String string = strings[0];
        String name = strings[1];
        int i = Integer.parseInt(string);
        for(int j = 0; j < i; j++){
            player.performCommand("bq start "+name+" "+j);
            player.performCommand("bq finish "+name+" "+j);
        }
        return false;
    }
}
