package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Convinience.HiddenExchangeGui;
import org.swlab.etcetera.Repositories.HiddenExchangeRepository;
import org.swlab.etcetera.Util.HiddenMaterialConfig;

public class HiddenExchangeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        int remaining = HiddenExchangeRepository.getInstance().getRemaining(player);
        player.sendMessage(ColorManager.format("#B5EAD7[ 어둠의 연금술사 ] §f이번 주 남은 교환 횟수 : §a" + remaining
                + "§f / " + HiddenMaterialConfig.WEEKLY_LIMIT + "회"));

        HiddenExchangeGui.openSourceSelect(player);
        return true;
    }
}
