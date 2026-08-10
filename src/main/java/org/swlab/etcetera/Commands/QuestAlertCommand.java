package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Convinience.QuestBossBar;
import org.swlab.etcetera.Repositories.QuestAlertSettingRepository;

public class QuestAlertCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        QuestAlertSettingRepository.getInstance().toggle(player.getUniqueId());
        boolean enabled = QuestAlertSettingRepository.getInstance().isEnabled(player.getUniqueId());
        if (enabled) {
            player.sendMessage(ColorManager.format("#78FF7C[퀘스트] §f퀘스트 알림을 활성화했습니다."));
            QuestBossBar.getInstance().show(player);
        } else {
            player.sendMessage(ColorManager.format("#78FF7C[퀘스트] §f퀘스트 알림을 비활성화했습니다."));
            QuestBossBar.getInstance().remove(player);
        }
        return true;
    }
}
