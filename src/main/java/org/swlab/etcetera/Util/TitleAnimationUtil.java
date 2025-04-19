package org.swlab.etcetera.Util;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

public class TitleAnimationUtil {

    public static TitleAnimationUtil instance = null;

    public static TitleAnimationUtil getInstance() {
        if (instance == null) {
            instance = new TitleAnimationUtil();
        }
        return instance;
    }

    public void sendAnimation(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut, String color1, String color2) {
        int delay = 2; // 틱 단위
        String[] split = title.split("");
        int length = split.length;

        // 1단계: 벌어진 글자 출력 (처음 1회)
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            StringBuilder spacedTitle = new StringBuilder();
            for (String s : split) spacedTitle.append(s).append(" ");
            player.sendTitle(
                    ColorManager.format(color2) + spacedTitle.toString().trim(),
                    ColorManager.format(color2) + subTitle,
                    fadeIn,
                    stay,
                    fadeOut
            );
        }, 0);

        // 2단계: 점점 붙기 (소리 없음)
        for (int step = 1; step <= length; step++) {
            final int currentStep = step;
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                StringBuilder animatedTitle = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    if (i < currentStep) {
                        animatedTitle.append(split[i]);
                    } else {
                        animatedTitle.append(split[i]).append(" ");
                    }
                }
                player.sendTitle(
                        ColorManager.format(color2) + animatedTitle.toString().trim(),
                        ColorManager.format(color2) + subTitle,
                        fadeIn,
                        stay,
                        fadeOut
                );
            }, delay * currentStep);
        }

        // 애니메이션의 피치 증가용 소리 타이밍: 총 3번만 재생
        int soundStartTick = delay * (length + 2);
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () ->
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.3f), soundStartTick); // 시작 강조

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () ->
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.6f), soundStartTick + 6);

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () ->
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.9f), soundStartTick + 12);

        // 3단계: 강조 애니메이션 (왼쪽 → 오른쪽)
        for (int i = 0; i < length; i++) {
            final int highlightIndex = i;
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                StringBuilder finalTitle = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    if (j == highlightIndex) {
                        finalTitle.append(ColorManager.format(color1)).append(split[j]);
                    } else {
                        finalTitle.append(ColorManager.format(color2)).append(split[j]);
                    }
                }
                player.sendTitle(
                        finalTitle.toString(),
                        ColorManager.format(color2) + subTitle,
                        fadeIn,
                        stay,
                        fadeOut
                );
            }, soundStartTick + delay * i);
        }
    }







}
