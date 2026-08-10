package org.swlab.etcetera.Convinience;

import com.binggre.binggreapi.utils.ColorManager;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.players.PlayerAccountImplementation;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Repositories.QuestAlertSettingRepository;

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

    public void show(Player player) {
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

    private int getNextQuestId(Player player) {
        PlayerAccountImplementation account = BeautyQuests.getInstance().getPlayersManager().getAccount(player);
        if (account == null) {
            return -1;
        }
        for (int id = 1; id < 999; id++) {
            if (!QuestNpcData.hasQuest(id)) {
                continue;
            }
            Quest quest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(id);
            if (quest == null || quest.hasFinished(account)) {
                continue;
            }
            if (id == 1) {
                return id;
            }
            Quest prevQuest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(id - 1);
            if (prevQuest != null && prevQuest.hasFinished(account)) {
                return id;
            }
            return -1;
        }
        return -1;
    }
}
