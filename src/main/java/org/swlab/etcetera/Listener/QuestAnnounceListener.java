package org.swlab.etcetera.Listener;

import fr.skytasul.quests.api.events.QuestFinishEvent;
import org.bukkit.Bukkit;
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

    @EventHandler
    public void onQuestFinish(QuestFinishEvent e) {
        int questId = e.getQuest().getId();
        if (questId < 999) {
            QuestBossBar.getInstance().show(e.getPlayer());
        }
    }
}
