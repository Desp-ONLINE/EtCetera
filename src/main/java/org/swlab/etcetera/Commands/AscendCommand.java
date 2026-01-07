package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Util.CommandUtil;

public class AscendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        String nickname = player.getName();

        String jobName = strings[0];
        String howManyTimeString = "";
        switch (strings[1]) {
            case "1":
                howManyTimeString = "첫 번째";
                break;
            case "2":
                howManyTimeString = "두 번째";
                break;
            case "3":
                howManyTimeString = "세 번째";
                break;
            case "4":
                howManyTimeString = "모든";
                break;
        }

        String message = ColorManager.format("§f      " + nickname + " §7§o님께서 #5E76FF§o§n초월자, 아크의§r §7§o(" + jobName + ") #85B0FF§o§n" + howManyTimeString + " 시련§r §7§o을 완료했습니다.");

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
        return false;
    }

}
