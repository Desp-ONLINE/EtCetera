package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class AllGIveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if(!player.isOp()){
            return false;
        }
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack clone = itemInMainHand.clone();
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§e [ 이벤트 ] §f"+player.getName()+" 님께서 "+ColorManager.format(itemInMainHand.getItemMeta().getDisplayName())+"§f x"+itemInMainHand.getAmount()+" §f을(를) 모든 유저에게 나누었습니다!");
        Bukkit.broadcastMessage("");

        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class,"");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class,"§e [ 이벤트 ] §f"+player.getName()+" 님께서 "+ColorManager.format(itemInMainHand.getItemMeta().getDisplayName())+"§f x"+itemInMainHand.getAmount()+" §f을(를) 모든 유저에게 나누었습니다!");
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class,"");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().addItem(clone);
        }


        return true;
    }
}
