package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.players.PlayerAccountImplementation;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Convinience.QuestNpcData;

public class MainQuestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return false;
        }
        if (!EtCetera.getChannelType().equals("lobby")) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c로비에서만 사용 하실 수 있는 명령어입니다."));
            return false;
        }
        PlayerAccountImplementation account = BeautyQuests.getInstance().getPlayersManager().getAccount(player);
        if (account == null) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c퀘스트 정보를 불러올 수 없습니다. 잠시 후 다시 시도해주세요."));
            return false;
        }
        int questId = getCurrentQuestId(account);
        if (questId == -1) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c진행할 수 있는 메인 퀘스트가 없습니다."));
            return false;
        }
        QuestNpcData.NpcLocation npc = QuestNpcData.getNpcLocation(questId);
        Quest quest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(questId);
        if (quest != null && quest.hasStarted(account)) {
            QuestNpcData.NpcLocation inProgressNpc = QuestNpcData.getInProgressNpcLocation(questId);
            if (inProgressNpc != null) {
                npc = inProgressNpc;
            }
        }
        if (npc == null) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c해당 퀘스트의 NPC 위치 정보가 없습니다."));
            return false;
        }
        int level = new MMOCoreAPI(EtCetera.getInstance()).getPlayerData(player).getLevel();
        if (level < npc.requiredLevel()) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c" + npc.requiredLevel() + "레벨 미만은 "
                    + npc.regionName() + "에 출입할 수 없습니다. §7(현재 " + level + "레벨)"));
            return false;
        }
        World world = Bukkit.getWorld(npc.world());
        if (world == null) {
            player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §c현재 서버에서는 이동할 수 없습니다."));
            return false;
        }
        player.teleport(new Location(world, npc.x(), npc.y(), npc.z(), npc.yaw(), 0.0f));
        player.sendMessage(ColorManager.format("#FFF285[ 메인 퀘스트 ] §f퀘스트 NPC §e" + npc.name() + "§f에게 이동했습니다."));
        return true;
    }

    private int getCurrentQuestId(PlayerAccountImplementation account) {
        for (int id = 1; id <= QuestNpcData.getMaxNpcQuestId(); id++) {
            Quest quest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(id);
            if (quest == null || quest.hasFinished(account)) {
                continue;
            }
            if (id == 1) {
                return id;
            }
            Quest prevQuest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(id - 1);
            if (prevQuest == null || prevQuest.hasFinished(account)) {
                return id;
            }
            return -1;
        }
        return -1;
    }
}
