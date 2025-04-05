package org.swlab.etcetera.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandUtil {

    public static void runCommandAsOP(Player player,String command){
        if(!player.isOp()){
            player.setOp(true);
            try{
                Bukkit.dispatchCommand(player, command);

            } catch (Exception e){
                player.setOp(false);

            }
            player.setOp(false);
        }
        else {
            Bukkit.dispatchCommand(player, command);
        }
    }
}
