package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.UserSettingRepository;
import org.swlab.etcetera.Training.listeners.CombatListener;
import org.swlab.etcetera.Training.objects.TrainingController;
import org.swlab.idedamagesystem.api.IDEDamageDebug;
import org.swlab.idedamagesystem.api.IDEDamageEvent;

import java.math.BigDecimal;

/**
 * 데미지 연산은 IDEDamageSystem 플러그인(idedamage)에서 처리하고,
 * 여기서는 들어간 idedamage 의 데미지 표기/훈련장 누적만 처리한다.
 * IDEDamageSystem 이 켜져 있을 때만 등록된다.
 */
public class IDEDamageDisplayListener implements Listener {

    @EventHandler
    public void onIDEDamage(IDEDamageEvent e) {
        Player attacker = e.getAttacker();
        LivingEntity victim = e.getVictim();
        double fixedDamage = Math.round(e.getDamage());

        // 훈련장 누적 데미지는 표기값과 동일한 이 최종 데미지를 그대로 사용한다
        TrainingController trainingController = TrainingController.get(attacker);
        if (trainingController != null) {
            CombatListener.SourceItem source = CombatListener.sourceOf(e.getAttack().getAttacker(), e.getAttack().getDamage());
            trainingController.addDamage(fixedDamage, source.name(), source.itemId());
        }

        IDEDamageDebug.log("§d[표기] §7" + attacker.getName() + " -> " + victim.getName() + " " + (long) fixedDamage
                + (e.isCritical() ? " (크리)" : "") + (fixedDamage <= 5 ? " §c5 이하라 표기 생략" : ""));

        // 데미지 5 이하는 표기하지 않음
        if (fixedDamage <= 5) {
            return;
        }

        String prefix = e.isCritical() ? "ᎌ §6" : "ᎈ §c";
        if (UserSettingRepository.getInstance().isShowDamageChat(attacker)) {
            if (victim instanceof Cow || victim instanceof Player) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(EtCetera.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        attacker.sendMessage("    " + victim.getName() + "§f : " + prefix + NumberUtil.applyComma((long) fixedDamage) + " §cDamage §7(남은 체력: " + NumberUtil.applyComma(BigDecimal.valueOf(victim.getHealth())) + " | " + String.format("%.2f", victim.getHealth() / victim.getMaxHealth() * 100) + "%)");
                    }
                }, 1L);
            }
        }
        attacker.sendTitle("", e.isCritical() ? criticalDamageString(fixedDamage) : damageString(fixedDamage), 5, 10, 5);
    }

    public String criticalDamageString(double fixedDamage) {
        return "§f                                                                   ᎌ §6" + NumberUtil.applyComma((long) fixedDamage);
    }

    public String damageString(double fixedDamage) {
        return "§f                                                                   ᎈ §c" + NumberUtil.applyComma((long) fixedDamage);
    }
}
