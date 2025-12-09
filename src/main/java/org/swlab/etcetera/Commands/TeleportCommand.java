package org.swlab.etcetera.Commands;

import com.mongodb.client.MongoCollection;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.IDEPass.api.IDEPassAPI;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class TeleportCommand implements CommandExecutor {

    Map<Integer, String> hashMap = new HashMap<>();

    MongoCollection<Document> playerTeleport;

    public TeleportCommand() {
        hashMap.put(1, "얕은숲_입구");
        hashMap.put(2, "깊은숲_입구");
        hashMap.put(3, "고블린야영지_입구");
        hashMap.put(4, "요정의숲_입구");
        hashMap.put(5, "엘프반군은거지_입구");
        hashMap.put(6, "메마른숲_입구");
        hashMap.put(7, "황무지_입구");
        hashMap.put(8, "사막의중심_입구");
        hashMap.put(9, "이글거리는곳_입구");
        hashMap.put(10, "블레이즈서식지_입구");
        hashMap.put(11, "열이식어가는곳_입구");
        hashMap.put(12, "냉기의화원_입구");
        hashMap.put(13, "공허로향하는길_입구");
        hashMap.put(14, "원무의공간_입구");
        hashMap.put(15, "아공간_입구");
        hashMap.put(16, "망자의기로_입구");
        hashMap.put(17, "망각의영역_입구");
        playerTeleport = DatabaseRegister.getInstance().getMongoDatabase().getCollection("PlayerTeleport");

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(EtCetera.getChannelType().equals("lobby")){
            Player player = (Player) sender;
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            insertDocument(player);
            int level = mmoCoreAPI.getPlayerData(player).getLevel();
            if(strings.length == 0){
                CommandUtil.runCommandAsOP(player, "gui open 텔레포트");
                return false;
            }
            if(!player.isOp()){
                return true;
            }


            int dungeonNumber = Integer.parseInt(strings[0]);
            int channelNumber = Integer.parseInt(strings[1]);
            String passType = IDEPassAPI.getPlayer(player.getUniqueId().toString()).getPassType();
            if (!canTeleport(player) && !passType.equals("full")) {
                player.sendMessage("§c 아직 순간이동 할 수 없습니다! 남은 쿨타임: "+(20-getLeftDuration(player))+"분");
                return false;
            }
            if(dungeonNumber <= 3){
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);
                return false;
            }
            else if(dungeonNumber <=6){
                if(level < 20){
                    player.sendMessage("§c 20레벨 이상인 경우에만 입장하실 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);

                return true;
            } else if (dungeonNumber <=9){
                if(level < 45){
                    player.sendMessage("§c 45레벨 이상인 경우에만 입장하실 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);

                return true;
            } else if (dungeonNumber <=12){
                if(level < 70){
                    player.sendMessage("§c 70레벨 이상인 경우에만 입장하실 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);

                return true;
            } else if (dungeonNumber <=15){
                if(level < 100){
                    player.sendMessage("§c 100레벨 이상인 경우에만 입장하실 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);
                return true;
            } else if (dungeonNumber <=18){
                if(level < 130){
                    player.sendMessage("§c 130레벨 이상인 경우에만 입장하실 수 있습니다.");
                    return false;
                }
                CommandUtil.runCommandAsOP(player, "채널 워프 dungeon"+channelNumber+" 워프 이동 "+hashMap.get(dungeonNumber));
                updateTeleportTime(player);

                return true;
            }
        }
        System.out.println("asdg");
        return false;
    }

    public void insertDocument(Player player){
        if(playerTeleport.find(new Document("uuid", player.getUniqueId().toString())).first() == null){
            Document document = new Document("uuid", player.getUniqueId().toString());
            document.append("nickname", player.getName()).append("latestTeleportedTime", "");
            playerTeleport.insertOne(document);
        }
    }

    public void updateTeleportTime(Player player){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("+09:00"));
        Document first = playerTeleport.find(new Document("uuid", player.getUniqueId().toString())).first();
        first.replace("latestTeleportedTime", now.toString());
        playerTeleport.replaceOne(new Document("uuid", player.getUniqueId().toString()), first);
    }
    public long getLeftDuration(Player player){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("+09:00"));
        Document first = playerTeleport.find(new Document("uuid", player.getUniqueId().toString())).first();
        String string = first.getString("latestTeleportedTime");
        OffsetDateTime parse = OffsetDateTime.parse(string);
        Duration duration = Duration.between(parse, now);
        return duration.toMinutes();
    }


    public boolean canTeleport(Player player){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("+09:00"));
        Document first = playerTeleport.find(new Document("uuid", player.getUniqueId().toString())).first();
        String string = first.getString("latestTeleportedTime");
        if(string.isEmpty()){
            return true;
        }
        OffsetDateTime parse = OffsetDateTime.parse(string);
        Duration duration = Duration.between(parse, now);
        return duration.toMinutes() >= 20;
    }
}
