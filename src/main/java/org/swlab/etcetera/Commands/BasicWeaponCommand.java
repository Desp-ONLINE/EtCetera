package org.swlab.etcetera.Commands;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.api.quests.QuestsManager;
import fr.skytasul.quests.players.PlayerQuestDatasImplementation;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

import java.util.HashMap;

public class BasicWeaponCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        HashMap<String, Integer> jobList = new HashMap<>();
        jobList.put("크루세이더", 1);
        jobList.put("파우스트", 2);
        jobList.put("오베론", 3);
        jobList.put("인페르노", 4);
        jobList.put("제피르", 5);
        jobList.put("루인드", 6);
        jobList.put("판", 7);
        jobList.put("페이탈", 8);
        HashMap<String, Integer> awakenedJobList = new HashMap<>();
        awakenedJobList.put("크루세이더", 1);
        awakenedJobList.put("파우스트", 2);
        awakenedJobList.put("오베론", 5);
        awakenedJobList.put("인페르노", 6);
        awakenedJobList.put("제피르", 4);
        awakenedJobList.put("루인드", 3);
        awakenedJobList.put("판", 7);
        awakenedJobList.put("페이탈", 8);

        // Arrays.asList("크루세이더", "파우스트", "오베론", "인페르노", "제피르", "루인드","판")

        if (strings.length == 0) {
            player.sendMessage("§e [기본템] §f/기본템 <1/2/3/4/각성> - §7지급 받으실 기본템의 전직 등급을 함께 작성해주세요.");
            player.sendMessage("§7                 ㄴ> 예) /기본템 4 - 본인 현재 직업의 4차 직업 기본템을 지급 받습니다.");
            return false;
        }

        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();


        switch (strings[0]) {
            case "2":
                if (checkisFinished(player, 90000 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 20)) {
                    if (playerClass.getName().equals("판")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_판2");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("루인드")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_루인드2");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("오베론")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_오베론2");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("제피르")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_제피르2");
                        player.getInventory().addItem(jobItem);
                    }
                    ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_2" + playerClass.getName() + "0");
                    player.getInventory().addItem(basicWeapon);
                    player.sendMessage("");
                    player.sendMessage("§e 기본템 지급이 완료되었습니다. +0 강화 등급으로는 언제든 복구가 가능하나, 강화 이후에는 해당 강화 등급으로 복구가 불가하니 주의해주세요.");

                } else {
                    player.sendMessage("§c 20레벨 미만이거나, 전직 퀘스트를 클리어 하지 않았습니다. 2차 전직 퀘스트는 엘븐하임의 전직관으로부터 수행할 수 있습니다.");
                    return false;

                }
                break;
            case "3":
                if (checkisFinished(player, 90010 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 45)) {
                    if (playerClass.getName().equals("판")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_판3");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("루인드")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_루인드3");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("오베론")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_오베론3");
                        player.getInventory().addItem(jobItem);
                    }if (playerClass.getName().equals("제피르")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_제피르3");
                        player.getInventory().addItem(jobItem);
                    }
                    ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_3" + playerClass.getName() + "0");
                    player.getInventory().addItem(basicWeapon);
                    player.sendMessage("");
                    player.sendMessage("§e 기본템 지급이 완료되었습니다. +0 강화 등급으로는 언제든 복구가 가능하나, 강화 이후에는 해당 강화 등급으로 복구가 불가하니 주의해주세요.");
                } else {
                    player.sendMessage("§c 45레벨 미만이거나, 전직 퀘스트를 클리어 하지 않았습니다. 3차 전직 퀘스트는 칼리마의 전직관으로부터 수행할 수 있습니다.");
                    return false;

                }
                break;
            case "4":
                if (checkisFinished(player, 90020 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 70)) {
                    if (playerClass.getName().equals("판")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_판4");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("루인드")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_루인드4");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("오베론")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_오베론4");
                        player.getInventory().addItem(jobItem);
                    }if (playerClass.getName().equals("제피르")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_제피르4");
                        player.getInventory().addItem(jobItem);
                    }
                    ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_4" + playerClass.getName() + "0");
                    player.getInventory().addItem(basicWeapon);
                    player.sendMessage("");
                    player.sendMessage("§e 기본템 지급이 완료되었습니다. +0 강화 등급으로는 언제든 복구가 가능하나, 강화 이후에는 해당 강화 등급으로 복구가 불가하니 주의해주세요.");
                } else {
                    player.sendMessage("§c 70레벨 미만이거나, 전직 퀘스트를 클리어 하지 않았습니다. 4차 전직 퀘스트는 인페리움의 전직관으로부터 수행할 수 있습니다.");
                    return false;

                }
                break;
            case "각성":
                if (checkisFinished(player, 90030 + awakenedJobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 100)) {
                    if (playerClass.getName().equals("판")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_판5");
                        player.getInventory().addItem(jobItem);
                    }
                    if (playerClass.getName().equals("루인드")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_루인드5");
                        player.getInventory().addItem(jobItem);
                    }if (playerClass.getName().equals("오베론")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_오베론5");
                        player.getInventory().addItem(jobItem);
                    }if (playerClass.getName().equals("제피르")) {
                        ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_제피르5");
                        player.getInventory().addItem(jobItem);
                    }
                    ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_5" + playerClass.getName() + "0");
                    player.getInventory().addItem(basicWeapon);
                    player.sendMessage("§e 기본템 지급이 완료되었습니다. +0 강화 등급으로는 언제든 복구가 가능하나, 강화 이후에는 해당 강화 등급으로 복구가 불가하니 주의해주세요.");
                } else {
                    player.sendMessage("§c 100레벨 미만이거나, 전직 퀘스트를 클리어 하지 않았습니다. 각성 전직 퀘스트는 아르크티카의 전직관으로부터 수행할 수 있습니다.");
                    return false;
                }
                break;
            case "1":
                if (playerClass.getName().equals("판")) {
                    ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_판1");
                    player.getInventory().addItem(jobItem);
                }
                if (playerClass.getName().equals("루인드")) {
                    ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_루인드1");
                    player.getInventory().addItem(jobItem);
                }
                if (playerClass.getName().equals("오베론")) {
                    ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_오베론1");
                    player.getInventory().addItem(jobItem);
                }if (playerClass.getName().equals("제피르")) {
                    ItemStack jobItem = MMOItems.plugin.getItem("JOB_EQUIPMENT", "긍지_제피르1");
                    player.getInventory().addItem(jobItem);
                }
                ItemStack basicWeapon = MMOItems.plugin.getItem("SWORD", "직업무기_1" + playerClass.getName() + "0");
                player.getInventory().addItem(basicWeapon);
                player.sendMessage("§e 기본템 지급이 완료되었습니다. +0 강화 등급으로는 언제든 복구가 가능하나, 강화 이후에는 해당 강화 등급으로 복구가 불가하니 주의해주세요.");
                break;
            default:
                player.sendMessage("§e [기본템] §f/기본템 <1/2/3/4/각성> - §7지급 받으실 기본템의 전직 등급을 함께 작성해주세요.");
                player.sendMessage("§7                 ㄴ> 예) /기본템 4 - 본인 현재 직업의 4차 직업 기본템을 지급 받습니다.");
                return false;


        }
        ItemStack basicArmor = MMOItems.plugin.getItem("ARMOR", "방어구_모험가0");
        player.getInventory().addItem(basicArmor);
        mmoCoreAPI.getPlayerData(player).setClassPoints(999);
        return true;

    }

    public static boolean checkisFinished(Player player, int questID, int level, int levelCondition) {
        QuestsManager questsManager = BeautyQuests.getInstance().getAPI().getQuestsManager();
        Quest quest = questsManager.getQuest(questID);
        PlayerQuestDatasImplementation questDatas = BeautyQuests.getInstance().getPlayersManager().getAccount(player).getQuestDatas(quest);
        return questDatas.isFinished() && level >= levelCondition;
    }
}
