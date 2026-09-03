package org.swlab.etcetera.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * IDEQuest 퀘스트 조회 브릿지.
 * IDEQuest 클래스는 내부 클래스 안에서만 참조하므로,
 * 해당 플러그인이 서버에 없어도 NoClassDefFoundError 없이 동작한다.
 */
public final class QuestCompat {

    private static final String IDE_QUEST = "IDEQuest";

    private QuestCompat() {
    }

    public static boolean isIDEQuestEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(IDE_QUEST) && Ide.loaded();
    }

    /** 해당 id의 퀘스트가 정의되어 있는지. */
    public static boolean questExists(int questId) {
        return isIDEQuestEnabled() && Ide.exists(questId);
    }

    /** 퀘스트 이름. 없으면 null. */
    public static String questName(int questId) {
        return isIDEQuestEnabled() ? Ide.name(questId) : null;
    }

    /** 퀘스트를 시작한 적이 있는지 (진행 중이거나 완료 이력 포함). */
    public static boolean hasQuestData(Player player, int questId) {
        return isIDEQuestEnabled() && Ide.hasQuestData(player, questId);
    }

    /** 플레이어 퀘스트 데이터가 로드됐는지 (IDEQuest는 접속 약 2초 뒤 로드). 로드 전엔 모든 조회가 "없음"이다. */
    public static boolean isDataLoaded(Player player) {
        return isIDEQuestEnabled() && Ide.isDataLoaded(player);
    }

    /** 완료한 퀘스트 중 id가 limit 미만인 것의 최댓값. 없으면 0. */
    public static int maxFinishedBelow(Player player, int limit) {
        return isIDEQuestEnabled() ? Ide.maxFinishedBelow(player, limit) : 0;
    }

    /** 1회 이상 완료했는지. */
    public static boolean hasFinished(Player player, int questId) {
        return isIDEQuestEnabled() && Ide.hasFinished(player, questId);
    }

    /** 현재 진행 중인지. */
    public static boolean isActive(Player player, int questId) {
        return isIDEQuestEnabled() && Ide.isActive(player, questId);
    }

    /** 시작했거나 완료했는지 (isActive || hasFinished). */
    public static boolean hasStartedOrFinished(Player player, int questId) {
        return isActive(player, questId) || hasFinished(player, questId);
    }

    // ---------------------------------------------------------------- IDEQuest

    private static final class Ide {

        static boolean loaded() {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.isLoaded();
        }

        static boolean exists(int questId) {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.quest(questId) != null;
        }

        static String name(int questId) {
            org.example.dople.iDEQuest.quest.Quest quest = org.example.dople.iDEQuest.api.IDEQuestAPI.quest(questId);
            return quest != null ? quest.name() : null;
        }

        static boolean hasQuestData(Player player, int questId) {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.hasQuestData(player.getUniqueId(), questId);
        }

        static boolean isDataLoaded(Player player) {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.isDataLoaded(player.getUniqueId());
        }

        static int maxFinishedBelow(Player player, int limit) {
            int max = 0;
            for (int id : org.example.dople.iDEQuest.api.IDEQuestAPI.finishedQuestIds(player.getUniqueId())) {
                if (id < limit && id > max) {
                    max = id;
                }
            }
            return max;
        }

        static boolean hasFinished(Player player, int questId) {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.hasFinished(player.getUniqueId(), questId);
        }

        static boolean isActive(Player player, int questId) {
            return org.example.dople.iDEQuest.api.IDEQuestAPI.isActive(player.getUniqueId(), questId);
        }
    }
}
