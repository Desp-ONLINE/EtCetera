package org.swlab.etcetera.Util;

import com.ticxo.modelengine.api.ModelEngineAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.extern.flogger.Flogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.swlab.etcetera.EtCetera;

public class DamageIndicatorUtil {


    public static void summonTextDisplay(Player player, double damage, Entity victim, boolean isCritical) {

        Location startLoc = victim.getLocation().add(0, 2.0, 0);
        World world = victim.getWorld();

        double maxHealth = MythicBukkit.inst().getMobManager().getMythicMobInstance(victim).getEntity().getMaxHealth();


        // TextDisplay 생성 및 기본 세팅
        TextDisplay display = world.spawn(startLoc, TextDisplay.class);
        if (isCritical) {
            display.text(Component.text("§6" + damage));
            display.setTransformation(new Transformation(
                    new Vector3f(0, 1, 0),                  // 위치 오프셋
                    new AxisAngle4f(0, 0, 0, 1),            // 회전 없음
                    new Vector3f(3.5f,3.5f,3.5f),         // 글자 크기 1.5배
                    new AxisAngle4f(0, 0, 0, 1)
            ));
        } else {
            display.text(Component.text("§f" + damage));
            display.setTransformation(new Transformation(
                    new Vector3f(0, 1, 0),                  // 위치 오프셋
                    new AxisAngle4f(0, 0, 0, 1),            // 회전 없음
                    new Vector3f(1.5f, 1.5f, 1.5f),         // 글자 크기 1.5배
                    new AxisAngle4f(0, 0, 0, 1)
            ));
        }
        display.setBillboard(TextDisplay.Billboard.CENTER);
        display.setSeeThrough(true);
        display.setDefaultBackground(false);
        display.setShadowed(false);
        display.setGravity(false);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0)); // 배경 투명



        // 부드럽게 위로 떠오르기 애니메이션 (1초간 20틱)
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                if (!display.isValid()) {
                    cancel();
                    return;
                }
                if (ticks >= maxTicks) {
                    display.remove();
                    cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;
                double offsetY = easeOutQuad(progress) * 0.8; // 최대 0.8블록 위로
                Location newLoc = startLoc.clone().add(0, offsetY, 0);
                display.teleport(newLoc);

                ticks++;
            }

            // 부드러운 가속/감속 효과 (Easing 함수)
            private double easeOutQuad(double t) {
                return t * (2 - t);
            }
        }.runTaskTimer(EtCetera.getInstance(), 0L, 1L);
    }
}
