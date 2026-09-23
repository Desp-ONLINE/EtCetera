package org.swlab.etcetera.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dople.dataSync.event.DataLoadEvent;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.TutorialRepository;
import org.swlab.etcetera.Util.CommandUtil;

/**
 * DataSync 의 DataLoadEvent 핸들러 모음.
 * DataSyncCompat.isEnabled() 일 때만 등록된다 (튜토리얼 채널에서는 등록되지 않음).
 */
public class DataSyncListener implements Listener {

    @EventHandler
    public void onDataLoad(DataLoadEvent event) {
        DataLoadListener.getInstance().putPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onProfileLoad(DataLoadEvent e) {
        Player player = e.getPlayer();
        player.setHealth(player.getMaxHealth());
//        NameTagUtil.setPlayerNameTag(player);

        if (EtCetera.getChannelType().equals("lobby")) {
            if (!TutorialRepository.getInstance().isTutorialCompleted(player)) {
                Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                    CommandUtil.runCommandAsOP(player, "튜토리얼");
                    player.sendMessage("§a 튜토리얼을 진행해주세요!");
                }, 100L);
            }
        }
    }
}
