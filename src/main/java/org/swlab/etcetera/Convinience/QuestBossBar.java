package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Repositories.QuestAlertSettingRepository;
import org.swlab.etcetera.Util.QuestCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestBossBar {

    public static QuestBossBar instance;

    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    public QuestBossBar() {
        instance = this;
    }

    public static QuestBossBar getInstance() {
        if (instance == null) {
            instance = new QuestBossBar();
        }
        return instance;
    }

    /** 메인 퀘스트 길 안내 보스바 사용 여부. */
    public static final boolean ENABLED = true;

    /** 메인 퀘스트 id 범위 상한 (이 미만이 메인 퀘스트). */
    private static final int MAIN_QUEST_LIMIT = 1000;

    public void show(Player player) {
        if (!ENABLED) {
            remove(player);
            return;
        }
        if (!QuestAlertSettingRepository.getInstance().isEnabled(player.getUniqueId())) {
            return;
        }
        int nextQuestId = getNextQuestId(player);
        if (nextQuestId == -1) {
            remove(player);
            return;
        }
        String info = QuestNpcData.getQuestInfo(nextQuestId);
        if (info == null) {
            remove(player);
            return;
        }
        String title = ColorManager.format("#FFF285[ 메인 퀘스트 길 안내 ] §f" + info + " §7§o(/메인퀘스트 명령어를 통해 즉시 이동 가능)");
        BossBar bossBar = bossBars.get(player.getUniqueId());
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SOLID);
            bossBar.setProgress(1.0);
            bossBar.addPlayer(player);
            bossBars.put(player.getUniqueId(), bossBar);
        } else {
            bossBar.setTitle(title);
        }
    }

    public void remove(Player player) {
        BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {

            bossBar.removeAll();
        }
    }

    public void removeAll() {
        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
    }

    /**
     * 안내할 다음 메인 퀘스트: 완료한 메인 퀘스트(1000번 미만) 중 가장 높은 번호 + 1.
     * 데이터가 아직 로드되지 않았거나(접속 직후), 다음 퀘스트가 없으면(전부 완료) -1.
     */
    private int getNextQuestId(Player player) {
        if (!QuestCompat.isIDEQuestEnabled() || !QuestCompat.isDataLoaded(player)) {
            return -1;
        }
        int next = QuestCompat.maxFinishedBelow(player, MAIN_QUEST_LIMIT) + 1;
        if (next >= MAIN_QUEST_LIMIT || !QuestCompat.questExists(next) || !QuestNpcData.hasQuest(next)) {
            return -1;
        }
        return next;
    }
}
