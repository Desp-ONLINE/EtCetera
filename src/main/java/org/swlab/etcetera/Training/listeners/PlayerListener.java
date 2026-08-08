package org.swlab.etcetera.Training.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.swlab.etcetera.Training.objects.TrainingController;

public class PlayerListener implements Listener {

    // 데미지 집계는 CombatListener(PlayerAttackEvent)에서 처리한다.
    // EntityDamageByEntityEvent로 집계하면 같은 타격이 두 번 계산된다.

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TrainingController controller = TrainingController.get(player);
        if (controller != null) {
            controller.getRoom().quit();
        }
    }
}
