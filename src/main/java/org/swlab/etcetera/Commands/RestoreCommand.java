package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.mongodb.client.MongoCollection;
import fr.skytasul.quests.BeautyQuests;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.player.CustomInventoryData;
import net.Indyuce.inventory.player.InventoryItem;
import net.Indyuce.inventory.player.InventoryLookupMode;
import net.Indyuce.inventory.player.PlayerData;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmoitems.MMOItems;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.babelTower.api.BabelTowerAPI;
import org.dople.guidance.database.PlayerRepository;
import org.dople.guidance.dto.PlayerDto;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RestoreCommand implements CommandExecutor {

    public RestoreCommand() {
        upgradeLogCollection = MongoLibraryPlugin.getInst()
                .getMongoClient()
                .getDatabase("SystemLog")
                .getCollection("UpgradeLog");
        upgradeCollection = MongoLibraryPlugin.getInst()
                .getMongoClient()
                .getDatabase("Upgrade")
                .getCollection("Weapons");
        timeRaidPlayerCollection = DatabaseRegister.getInstance()
                .getMongoDatabase()
                .getCollection("TimeDungeonPlayer");
        babel122RestoreCollection = DatabaseRegister.getInstance()
                .getMongoDatabase()
                .getCollection("Babel122RestoreLog");
//        accRestoredCollection = MongoLibraryPlugin.getInst().getMongoClient().getDatabase("EtCetera").getCollection("AccRestored");
    }

    MongoCollection<Document> upgradeLogCollection;
    MongoCollection<Document> upgradeCollection;
    MongoCollection<Document> timeRaidPlayerCollection;
    MongoCollection<Document> babel122RestoreCollection;
//    MongoCollection<Document> accRestoredCollection;

    // 2026-07-25 기준 바벨탑 122층 이상 클리어 유저 스냅샷 (BabelTower.PlayerData 에서 추출한 닉네임)
    private static final Set<String> BABEL_122_ELIGIBLE_NAMES = Set.of(
            "__Taeju", "kainue", "tibasion", "neoul_2", "pikachu0630", "Hike1",
            "Penguinvlrt", "_cokaPanda_", "rfsf2", "LOOKISM_GunPark", "ILIXO", "asd46578", "dople_L"
    );

    // TODO: 지급할 보상 아이템 목록 설정 { MMOItems 타입, 아이템 ID, 수량 }
    private static final List<String[]> BABEL_122_REWARDS = List.<String[]>of(
            new String[]{"MISCELLANEOUS", "기타_무색의휘장조각", "30"}
    );

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        HashMap<String, Integer> jobList = new HashMap<>();
        jobList.put("드레드노트", 0);
        jobList.put("크루세이더", 1);
        jobList.put("파우스트", 2);
        jobList.put("오베론", 3);
        jobList.put("인페르노", 4);
        jobList.put("제피르", 5);
        jobList.put("루인드", 6);
        jobList.put("판", 7);
        jobList.put("페이탈", 8);
        jobList.put("퀘이사", 9);
        if (strings.length == 0) {
            player.sendMessage("");
            player.sendMessage(ColorManager.format("#25A79D /복구 [전직] [차수(2/3/4/각성)] §f- 해당 전직의 서를 복구받습니다. §7§o(ex: /복구 전직 2 - 2차 전직의 서를 복구 받습니다.)"));
//            player.sendMessage(ColorManager.format("#25A79D /복구 버닝 §f- 버닝 완료 아이템을 복구 받습니다. 메인 퀘스트 41을 클리어 하고, 레벨이 45 이상이어야 합니다."));
            player.sendMessage(ColorManager.format("#25A79D /복구 바벨탑 §f- 내가 클리어 한 모든 바벨탑의 공략증을 획득합니다."));
            player.sendMessage(ColorManager.format("#25A79D /복구 길라잡이 §f- 일부 받지 못한 길라잡이 보상을 수령합니다."));
            player.sendMessage(ColorManager.format("#25A79D /복구 공략증 §f- 익스트림 황금의 미궁(Lv.10) 클리어 유저가 공략증을 복구받습니다."));
            player.sendMessage(ColorManager.format("#25A79D /복구 바벨탑122 §f- 7월 25일까지 바벨탑 122층을 클리어한 유저가 보상을 수령합니다. §7§o(1회 한정)"));
            player.sendMessage("");
            return true;
        }
        switch (strings[0]) {
            case "바벨탑":

                if (!EtCetera.getChannelType().equals("lobby")) {
                    player.sendMessage("§c 로비에서만 이용하실 수 있습니다.");
                    return true;
                }
                int clearFloor = BabelTowerAPI.getPlayerData(player).getClearFloor();

                if (clearFloor < 30) {
                    player.sendMessage("§c 받을 수 있는 복구가 없습니다. 현재 내 바벨탑 공략 성공 층: §f" + clearFloor);
                }
                if (clearFloor >= 30) {
                    ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_30");
                    player.getInventory().addItem(item);
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 30층 공략증)");

                }
                if (clearFloor >= 60) {
                    ItemStack item2 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_60");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 60층 공략증)");
                    player.getInventory().addItem(item2);
                }
                if (clearFloor >= 70) {
                    ItemStack item3 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_70");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 70층 공략증)");
                    player.getInventory().addItem(item3);

                }
                if (clearFloor >= 80) {
                    ItemStack item3 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_80");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 80층 공략증)");
                    player.getInventory().addItem(item3);

                }
                if (clearFloor >= 90) {
                    ItemStack item3 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_90");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 90층 공략증)");
                    player.getInventory().addItem(item3);

                }if (clearFloor >= 95) {
                    ItemStack item3 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_95");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 95층 공략증)");
                    player.getInventory().addItem(item3);

                }if (clearFloor >= 100) {
                    ItemStack item3 = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_바벨탑증표_100");
                    player.sendMessage("§a 아이템 복구가 완료되었습니다. (바벨탑 100층 공략증)");
                    player.getInventory().addItem(item3);

                }
                return false;
            case "바벨탑122": {

                String playerUuid = player.getUniqueId().toString();

                // 이미 수령했는지 확인 (1회 제한)
                if (babel122RestoreCollection.find(new Document("uuid", playerUuid)).first() != null) {
                    player.sendMessage("§c 이미 바벨탑 122층 보상을 수령하셨습니다.");
                    return true;
                }

                // 스냅샷 대상자 확인
                if (!BABEL_122_ELIGIBLE_NAMES.contains(player.getName())) {
                    player.sendMessage("§c 보상 대상이 아닙니다. (7월 25일까지 바벨탑 122층을 클리어한 유저만 수령할 수 있습니다.)");
                    return true;
                }

                // 수령 내역 저장 (중복 수령 방지를 위해 지급 전에 기록)
                Document receiveLog = new Document("uuid", playerUuid)
                        .append("name", player.getName())
                        .append("receivedAt", new Date());
                babel122RestoreCollection.insertOne(receiveLog);

                List<ItemStack> rewardItems = new ArrayList<>();
                for (String[] reward : BABEL_122_REWARDS) {
                    ItemStack rewardItem = MMOItems.plugin.getItem(reward[0], reward[1]);
                    rewardItem.setAmount(Integer.parseInt(reward[2]));
                    rewardItems.add(rewardItem);
                }
                Mail rewardMail = MMOMail.getInstance().getMailAPI()
                        .createMail("관리자", "바벨탑 122층 클리어 보상입니다.", 0, rewardItems);
                MMOMail.getInstance().getMailAPI().sendMail(player.getName(), rewardMail);

                player.sendMessage("§a 바벨탑 122층 클리어 보상이 지급되었습니다! §7§o(/메일함)");
                return true;
            }
            case "전직":
                if (strings[1].equals("2")) {
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if (BasicWeaponCommand.checkisFinished(player, 90000 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 20)) {
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서2");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }
                }
                if (strings[1].equals("3")) {
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if (BasicWeaponCommand.checkisFinished(player, 90010 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 45)) {
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서3");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }

                }
                if (strings[1].equals("4")) {
                    MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                    PlayerClass playerClass = mmoCoreAPI.getPlayerData(player).getProfess();
                    if (BasicWeaponCommand.checkisFinished(player, 90020 + jobList.get(playerClass.getName()), mmoCoreAPI.getPlayerData(player).getLevel(), 70)) {
                        player.sendMessage("§a 복구가 완료 되었습니다. 인벤토리를 확인해주세요.");
                        ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_전직의증서4");
                        player.getInventory().addItem(item);
                        return true;
                    } else {
                        player.sendMessage("§c 복구 조건에 맞지 않습니다. ( 해당 직업으로 전직 퀘스트를 클리어 하지 않았거나, 레벨이 부족합니다. )");
                        return false;
                    }
                }
//            case "버닝":
//                DatabaseRegister databaseRegister = DatabaseRegister.getInstance();
//                MongoCollection<Document> burningLog = databaseRegister.getMongoDatabase().getCollection("BurningLog");
//                Document first = burningLog.find(new Document("uuid", player.getUniqueId().toString())).first();
//                if (first != null) {
//                    player.sendMessage("§c 이미 붉은 특수 물약을 획득했습니다.");
//                    return false;
//                }
//                MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
//                int level = mmoCoreAPI.getPlayerData(player).getLevel();
//                if (!(BeautyQuests.getInstance().getPlayersManager().getAccount(player).getQuestDatas(BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(41)).isFinished()) && level < 45) {
//                    player.sendMessage("§c 레벨이 45보다 낮거나, 메인 퀘스트 41을 아직 클리어 하지 않으셨습니다!");
//                    return false;
//                }
//                Document document = new Document("uuid", player.getUniqueId().toString()).append("received", true);
//                burningLog.insertOne(document);
//                ItemStack item = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_붉은특수물약");
//                List<ItemStack> itemStackList = new ArrayList<>();
//                itemStackList.add(item);
//                Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "버닝 아이템 보상입니다.", 0, itemStackList);
//                MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);
//                player.sendMessage("§a 복구가 완료되었습니다! §7§o(/메일함)");
//                return true;
            case "공략증":
                // 황금의 미궁 익스트림(난이도 10, cleared 키 "1-10") 클리어 기록이 있는 유저에게
                // 익스트림 황금의 미궁 LV1 공략증을 복구 지급한다. (골드 보상 없음, 아이템만 지급)
                // 횟수 제한 없이 언제든 다시 받을 수 있다.
                String uuid = player.getUniqueId().toString();

                // 타임 던전 클리어 기록 조회
                Document timeRaidPlayer = timeRaidPlayerCollection.find(new Document("uuid", uuid)).first();
                List<String> clearedList = timeRaidPlayer == null
                        ? new ArrayList<>()
                        : timeRaidPlayer.getList("cleared", String.class);
                if (clearedList == null) {
                    clearedList = new ArrayList<>();
                }

                // "1-10" 클리어 여부 확인
                if (!clearedList.contains("1-10")) {
                    player.sendMessage("§c 복구 조건에 맞지 않습니다. (익스트림 황금의 미궁(Lv.10)을 클리어한 기록이 없습니다.)");
                    return true;
                }

                // 공략증 지급 (아이템만)
                ItemStack labyrinthItem = MMOItems.plugin.getItem("MISCELLANEOUS", "퀘스트_익스트림황금의미궁LV1");
                labyrinthItem.setAmount(1);
                player.getInventory().addItem(labyrinthItem);

                player.sendMessage("§a 아이템 복구가 완료되었습니다. (익스트림 황금의 미궁 LV1 공략증)");
                return true;
            case "길라잡이":
                PlayerDto playerData = PlayerRepository.getInstance().getPlayerData(player);
                int id = playerData.getId();
                if (id >= 41) {
                    ItemStack item1 = MMOItems.plugin.getItem("TITLE", "칭호_ਜ");
                    player.getInventory().addItem(item1);
                } else {
                    player.sendMessage("§c 42번째 길라잡이를 클리어 하셔야 합니다!");
                }
                return true;
            case "방어구":

                List<Document> armorUpgradeLogs = upgradeLogCollection.find(
                        new Document("success", true)
                                .append("uuid", player.getUniqueId().toString())
                                .append("item_id", new Document("$regex", "^방어구"))
                ).into(new ArrayList<>());

                Document bestArmorLog = armorUpgradeLogs.stream()
                        .filter(log -> log.getString("item_id") != null)
                        .max((a, b) -> compareArmorItemId(a.getString("item_id"), b.getString("item_id")))
                        .orElse(null);

                if (bestArmorLog != null) {

                    Document first = upgradeCollection.find(new Document("beforeWeapon", bestArmorLog.getString("item_id"))).first();
                    String afterWeapon = first.getString("afterWeapon");

                    ItemStack armor = MMOItems.plugin.getItem("ARMOR", afterWeapon);
                    player.getInventory().addItem(armor);
                }
                return true;
//            case "123123ㄱㄴㄷ":
//
//                if(isRestored(player)){
//                    player.sendMessage("§c 이미 복구가 완료되었습니다.");
//                    return true;
//                }
//
//
//                PlayerData playerData1 = MMOInventory.plugin.getDataManager().get(player);
//                List<InventoryItem> items = playerData1.getItems(InventoryLookupMode.IGNORE_RESTRICTIONS);
//                List<ItemStack> itemStacks = new ArrayList<>();
//                for (InventoryItem item : items) {
//                    ItemStack itemStack = item.getItemStack();
//                    itemStacks.add(itemStack);
//                }
//
//                Mail mail = MMOMail.getInstance().getMailAPI().createMail("관리자", "기존 장비창 오류 아이템 복구입니다.", 0, itemStacks);
//                MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);
//
//                player.sendMessage("§a 기존 장비 데이터 복구가 완료되었습니다.");
//
//
//                return true;
            default:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#25A79D /복구 [전직] [차수] §f- 해당 전직의 서를 복구받습니다. §7§o(ex: /복구 전직 2 - 2차 전직의 서를 복구 받습니다.)"));
                player.sendMessage(ColorManager.format("#25A79D /복구 길라잡이 §f- 일부 받지 못한 길라잡이 보상을 수령합니다."));
                player.sendMessage(ColorManager.format("#25A79D /복구 공략증 §f- 익스트림 황금의 미궁(Lv.10) 클리어 유저가 공략증을 복구받습니다."));
                player.sendMessage(ColorManager.format("#25A79D /복구 바벨탑122 §f- 7월 25일까지 바벨탑 122층을 클리어한 유저가 보상을 수령합니다. §7§o(1회 한정)"));
//                player.sendMessage(ColorManager.format("#25A79D /복구 버닝 §f- 버닝 완료 아이템을 복구 받습니다. 메인 퀘스트 41을 클리어 하고, 레벨이 45 이상이어야 합니다. "));
                player.sendMessage("");
                return true;

        }
    }

//    public boolean isRestored(Player player) {
//        return accRestoredCollection.find(new Document("uuid", player.getUniqueId().toString())).first() != null;
//    }

    private int compareArmorItemId(String left, String right) {
        return Integer.compare(getArmorScore(left), getArmorScore(right));
    }

    private int getArmorScore(String itemId) {
        if (itemId == null) return Integer.MIN_VALUE;

        String prefix = "방어구_";
        if (!itemId.startsWith(prefix)) return Integer.MIN_VALUE;

        String body = itemId.substring(prefix.length());
        int splitIndex = body.length();
        while (splitIndex > 0 && Character.isDigit(body.charAt(splitIndex - 1))) {
            splitIndex--;
        }

        String tier = body.substring(0, splitIndex);
        String numberPart = body.substring(splitIndex);
        int level = numberPart.isEmpty() ? -1 : Integer.parseInt(numberPart);

        int tierOrder = switch (tier) {
            case "모험가" -> 0;
            case "숙련자" -> 1;
            case "영웅" -> 2;
            case "군주" -> 3;
            case "태고" -> 4;
            case "지배자" -> 5;
            default -> -1;
        };

        return tierOrder * 10000 + level;
    }
}
