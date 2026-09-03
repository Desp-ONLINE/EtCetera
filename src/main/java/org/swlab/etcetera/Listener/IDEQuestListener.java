package org.swlab.etcetera.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.example.dople.iDEQuest.event.DialogMessageEvent;
import org.example.dople.iDEQuest.event.PlayerQuestDataLoadEvent;
import org.swlab.etcetera.Convinience.QuestBossBar;
import org.example.dople.iDEQuest.event.QuestCompleteEvent;

/**
 * IDEQuest 퀘스트 완료·대사 이벤트 처리.
 * IDEQuest 플러그인이 서버에 있을 때만 등록된다 (EtCetera#registerEvents).
 */
public class IDEQuestListener implements Listener {

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent e) {
        Player player = e.getPlayer();
        int questId = e.getQuestId();
        ClassChangeListener.handleQuestFinish(player, questId, e.getQuest().name());
        QuestAnnounceListener.handleQuestFinish(player, questId);
    }

    /** 데이터가 로드된 시점(접속 약 2초 뒤)에 보스바를 띄운다. 접속 즉시는 데이터가 없어 1번 퀘스트로 잘못 나온다. */
    @EventHandler
    public void onDataLoad(PlayerQuestDataLoadEvent e) {
        QuestBossBar.getInstance().show(e.getPlayer());
    }

    /** 대사를 타이틀로 보여준다. */
    @EventHandler
    public void onDialogMessage(DialogMessageEvent e) {
        Player player = e.getPlayer();
        if (e.isNpcLine()) {
            player.sendTitle("§6" + e.getNpcName(), e.getText());
        } else {
            player.sendTitle("§e" + player.getName(), e.getText());
        }
        // 채팅 출력과 효과음은 IDEQuest가 그대로 처리하므로 이벤트를 취소하지 않는다.
    }
}
