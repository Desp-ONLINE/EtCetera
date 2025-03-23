package org.swlab.etcetera.Commands;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.structure.QuestImplementation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class MergeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        QuestImplementation quest = BeautyQuests.getInstance().getQuestsManager().getQuest(32);
        if(!BeautyQuests.getInstance().getPlayersManager().getAccount(player).hasQuestDatas(quest)){
            player.sendMessage("§c  32번째 메인퀘스트를 완료해야합니다!");
            return true;
        }
        if(EtCetera.getChannelType().equals("lobby")){
            CommandUtil.runCommandAsOP(player, "워프 이동 합성");
            return true;
        }
        CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 합성");
        return false;
    }
}
