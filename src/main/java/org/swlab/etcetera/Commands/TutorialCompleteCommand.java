
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

public class TutorialCompleteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if(!player.hasPermission("tutorial")){
            String format = ColorManager.format("§e[!] §f"+player.getName()+"#FDFF85님#F8FD89께#F3FB8E서 #E9F796튜#E4F59B토#DFF39F리#DAF1A4얼#D5EFA8을 #CBEBB1완#C6E9B5료#C1E7B9하#BCE5BE셨#B8E3C2습#B3E0C6니#AEDECB다#A9DCCF! #9FD8D8모#9AD6DC두 #90D2E5환#8BD0E9영#86CEEE해#81CCF2주#7CCAF6세#77C8FB요#72C6FF!");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, format);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(format);
            Bukkit.broadcastMessage("");
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set tutorial");
        CommandUtil.runCommandAsOP(player, "spawn");
        player.sendTitle("§6알림", "§f앞의 메인 퀘스트 NPC, §e제미나이§f에게 말을 걸어주세요!");
        CommandUtil.runCommandAsOP(player, "직업");


        return true;
    }
}
