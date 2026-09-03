package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.swlab.etcetera.Convinience.QuestBossBar;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.QuestAlertSettingRepository;

public class QuestAnnounceListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            if (e.getPlayer().isOnline()) {
                QuestBossBar.getInstance().show(e.getPlayer());
            }
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        QuestBossBar.getInstance().remove(e.getPlayer());
        QuestAlertSettingRepository.getInstance().remove(e.getPlayer().getUniqueId());
    }

    /** 퀘스트 완료 공통 처리. IDEQuestListener 가 호출한다. */
    public static void handleQuestFinish(Player player, int questId) {
        if (questId < 999) {
            QuestBossBar.getInstance().show(player);
        }
    }
}
