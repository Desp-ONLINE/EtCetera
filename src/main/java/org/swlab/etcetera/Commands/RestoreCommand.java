package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.mongodb.client.MongoCollection;
import fr.skytasul.quests.BeautyQuests;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        HashMap<String, Integer> jobList = new HashMap<>();
        jobList.put("크루세이더", 1);
        jobList.put("파우스트", 2);
        jobList.put("오베론", 3);
        jobList.put("인페르노", 4);
        jobList.put("제피르", 5);
        jobList.put("루인드", 6);
        jobList.put("판", 7);
        if(strings.length == 0){
            player.sendMessage("");
            player.sendMessage(ColorManager.format("#25A79D /복구 [전직] [차수(2/3/4/각성)] §f- 해당 전직의 서를 복구받습니다. §7§o(ex: /복구 전직 2 - 2차 전직의 서를 복구 받습니다.)"));
            player.sendMessage(ColorManager.format("#25A79D /복구 버닝 §f- 버닝 완료 아이템을 복구 받습니다. 메인 퀘스트 41을 클리어 하고, 레벨이 45 이상이어야 합니다."));
            player.sendMessage("");
            return true;
        }
        switch (strings[0]) {
            case "전직":
                if(strings[1].equals("2")){
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if(BasicWeaponCommand.checkisFinished(player, 90000+jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 20)){
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서2");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }
                }
                if(strings[1].equals("3")){
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if(BasicWeaponCommand.checkisFinished(player, 90010+jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 45)){
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서3");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }

                }
                if(strings[1].equals("4")){
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if(BasicWeaponCommand.checkisFinished(player, 90020+jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 70)){
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서4");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }
                }
            case "버닝":
                DatabaseRegister databaseRegister = DatabaseRegister.getInstance();
                MongoCollection<Document> burningLog = databaseRegister.getMongoDatabase().getCollection("BurningLog");
                Document first = burningLog.find(new Document("uuid", player.getUniqueId().toString())).first();
                if(first != null){
                    player.sendMessage("§c 이미 붉은 특수 물약을 획득했습니다.");
                    return false;
                }
                MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                int level = mmoCoreAPI.getPlayerData(player).getLevel();
                if(!(BeautyQuests.getInstance().getPlayersManager().getAccount(player).getQuestDatas(BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(41)).isFinished()) && level < 45){
                    player.sendMessage("§c 레벨이 45보다 낮거나, 메인 퀘스트 41을 아직 클리어 하지 않으셨습니다!");
                    return false;
                }
                Document document = new Document("uuid", player.getUniqueId().toString()).append("received", true);
                burningLog.insertOne(document);
                ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_붉은특수물약");
                List<ItemStack> itemStackList = new ArrayList<>();
                itemStackList.add(item);
                Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "버닝 아이템 보상입니다.", 0, itemStackList);
                MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);
                player.sendMessage("§a 복구가 완료되었습니다! §7§o(/메일함)");
                return true;
            default:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#25A79D /복구 [전직] [차수] §f- 해당 전직의 서를 복구받습니다. §7§o(ex: /복구 전직 2 - 2차 전직의 서를 복구 받습니다.)"));
                player.sendMessage(ColorManager.format("#25A79D /복구 버닝 §f- 버닝 완료 아이템을 복구 받습니다. 메인 퀘스트 41을 클리어 하고, 레벨이 45 이상이어야 합니다. "));
                player.sendMessage("");
                return true;

        }
    }
}
