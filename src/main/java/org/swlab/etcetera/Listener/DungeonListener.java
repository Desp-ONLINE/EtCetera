package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmodungeon.api.DungeonClearEvent;
import com.binggre.mmodungeon.api.DungeonFailedEvent;
import com.binggre.mmodungeon.api.DungeonJoinEvent;
import com.binggre.mmodungeon.api.DungeonQuitEvent;
import com.binggre.mmodungeon.objects.PlayerClearLog;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.RaidCoinRepository;
import org.swlab.etcetera.Util.CommandUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DungeonListener implements Listener {

    private List<Integer> jinRegionCommandersDungeonID = new ArrayList<Integer>(Arrays.asList(13, 14, 15));

//    private HashMap<Player, LocalDateTime> firstClearCooldown = new HashMap<>();


    @EventHandler
    public void onDungeonFail(DungeonFailedEvent e) {

        List<Player> members = e.getDungeonRoom().getMembers();
        for (Player member : members) {
            MMOPlayerData mmoPlayerData = MMOPlayerData.get(member.getUniqueId());
            mmoPlayerData.getCooldownMap().clearAllCooldowns();
        }
        e.setCancelledAmount(true);
    }

//    @EventHandler
//    public void onDungeonCooldown(DungeonJoinEvent e) {
//
//        if (e.getDungeon().getId() == 113) {
//            LocalDateTime localDateTime = LocalDateTime.now();
//
//            for (PlayerDungeon playerDungeon : e.getPlayerDungeons()) {
//                Player player = playerDungeon.toPlayer();
//
//                if (firstClearCooldown.get(player) == null) {
//                    firstClearCooldown.put(player, localDateTime);
//                } else {
//                    LocalDateTime joinedTime = firstClearCooldown.get(player);
//
//                    long minutes = Duration.between(joinedTime, localDateTime).toMinutes();
//
//                    if (minutes < 22) {
//                        player.sendMessage("§c 퍼스트 클리어 이벤트에 의한 쿨타임(22분) 이 존재합니다. 남은 시간: 약 §f"+(22-minutes)+"§c분");
//                        e.setCancelled(true);
//                    }
//                }
//            }
//
//
//        }
//
//
//    }

    @EventHandler
    public void onDungeonClear(DungeonClearEvent e) {

        List<Player> members = e.getDungeonRoom().getMembers();
        for (Player member : members) {
            MMOPlayerData mmoPlayerData = MMOPlayerData.get(member.getUniqueId());
            mmoPlayerData.getCooldownMap().clearAllCooldowns();
            if (!e.getDungeonRoom().isClear()) {
                return;
            }
            if (members.size() > 1) {
                return;
            }
            boolean a = RaidCoinRepository.getInstance().giveNormalReward(member, e.getDungeonRoom().getConnected().getName());
            boolean b = RaidCoinRepository.getInstance().giveSpecialReward(member, e.getDungeonRoom().getConnected().getName());

            if (a || b) {
                RaidCoinRepository.getInstance().updateUserRaidData(member, e.getDungeonRoom().getConnected().getName());
            }
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDungeonEnter(DungeonJoinEvent e) {
        Integer dungeonID = e.getDungeon().getId();

        boolean joinable = true;
        if (jinRegionCommandersDungeonID.contains(dungeonID)) {
            for (PlayerDungeon playerDungeon : e.getPlayerDungeons()) {
                for (Integer i : jinRegionCommandersDungeonID) {
                    PlayerClearLog clearLog = playerDungeon.getClearLog(i);
                    if (!clearLog.isJoinableDate()) {
                        joinable = false;
                    }
                }
            }
            if (!joinable) {
                e.setCancelMessage("§c 이미 진 레기온의 군단장 레이드를 오늘 플레이 하셨습니다.");
                e.setCancelled(true);
            }
        }


        if (!e.isCancelled()) {
            for (PlayerDungeon playerDungeon : e.getPlayerDungeons()) {
                Player player = playerDungeon.toPlayer();
                sendEnterMessage(dungeonID, player);
            }
        }


        // 메시지 출력부


    }

    public void sendEnterMessage(Integer dungeonID, Player player) {
        switch (dungeonID) {
            case 1:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#929292    오염된 땅의 주인, 티쿠스의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 티쿠스의 회전 공격은 강력합니다. 회전 공격을 조심하세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 티쿠스는 그의 수하들을 소환합니다. 그의 수하들을 처치하면서 티쿠스를 처치하세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#FF7070  ~  오염된 땅  ~"), ColorManager.format("§7§o 쥐의 왕, 티쿠스"));
                break;
            case 2:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#99F7FF    푸른 정화된 땅, 운디네의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 운디네의 광선 공격은 최대 체력의 고정 데미지를 받습니다. 조심하세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 운디네는 가끔 하늘을 날아오릅니다. 원거리 공격으로 대체하세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#99F7FF  ~  푸른 정화된 땅  ~"), ColorManager.format("§7§o 청정의 용, 운디네"));
                break;
            case 3:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#9AFF99    엘프 반군의 수장, 베로니카의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 베로니카는 남은 체력이 66% 일 때부터 더 강력한 공격을 시전합니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 베로니카는 자연의 힘을 받아 곰으로 변신하기도 합니다."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#99F7FF  ~  엘프 반군 은거지  ~"), ColorManager.format("§7§o 엘프 반군의 수장, 베로니카"));
                break;
            case 4:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#864B1B    흑심을 가진 주술사, 엘더 샤먼의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 엘더 샤먼은 남은 체력이 60% 일 때 세뇌된 베로니카를 소환합니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 엘더 샤먼은 가끔 토템을 소환합니다. 토템을 파괴하여 공략에 성공하세요!"));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#864B1B  ~  주술사의 동굴  ~"), ColorManager.format("§7§o 흑심을 가진 주술사, 엘더 샤먼"));
                break;
            case 5:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#9B6C3F    사막의 와이번, 질란트의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 질란트의 패턴은 단조롭습니다. 그의 패턴 전 모션을 보고 피해보세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 질란트의 내려 찍기 데미지는 매우 강력합니다. 주의하세요!"));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#9B6C3F  ~  와이번의 지하 동굴  ~"), ColorManager.format("§7§o 사막의 와이번, 질란트"));
                break;
            case 6:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#D76B56    반란의 마음으로 만들어진 기계, KRM-100의 개발지로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] KRM-100의 화염을 뿌리는 공격은 데미지가 강력합니다. 주의하세요!"));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] KRM-100의 내려 찍기 데미지는 매우 강력합니다. 주의하세요!"));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#D76B56  ~  아크바르의 개발지  ~"), ColorManager.format("§7§o 거대 기계, KRM-100"));
                break;
            case 7:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#4E2B5A    공허의 존재, 페르세포네의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 페르세포네는 3개의 페이즈로 이루어져 있습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 페르세포네는 변신 중 무적입니다. 데미지를 가할 수 있는 타이밍을 잘 잡아보세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#4E2B5A  ~  공허의 영역  ~"), ColorManager.format("§7§o 공허의 존재, 페르세포네"));
                break;
            case 8:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#3690D9    뇌전의 기사, 페룬의 영역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 페룬은 번개를 이용해 매우 빠른 이동을 하는 스킬을 가지고 있습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 페룬은 도끼를 던진 후 매우 빠른 속도로 이동한 후 여러분을 타격합니다."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#3690D9  ~  뇌전의 영역  ~"), ColorManager.format("§7§o 뇌전의 기사, 페룬"));
                break;
            case 13:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#FF7070    흑영의 군단장, 칼리드의 성역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 진 칼리드는 즉사 패턴을 가진 레이드 보스입니다. 즉사 패턴 시전 시 무적을 통해 살아남으세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 진 칼리드는 매우 빠른 기동력을 가진 레이드 보스입니다. 스턴과 무력화를 이용해 처치해보세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#FF7070  ~  군단장의 성역  ~"), ColorManager.format("§7§o 흑영의 군단장, 진 칼리드"));
                break;
            case 14:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#57995B    녹풍의 군단장, 알타이르의 성역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 진 알타이르는 원거리에서 계속해서 화살을 발사하는 레이드 보스입니다. 기동력을 활용해 그의 화살을 피해 처치하세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 진 알타이르는 공격 시 마다 구르기를 통해 여러분의 스킬을 회피합니다. 스턴과 무력화를 이용해 처치해보세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#57995B  ~  군단장의 성역  ~"), ColorManager.format("§7§o 녹풍의 군단장, 진 알타이르"));
                break;
            case 15:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#661529    적염의 군단장, 카인의 성역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 진 카인은 대부분의 공격이 근접 공격입니다. 원거리에서 주로 전투하는 경우가 좋습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 진 카인의 스킬은 한 방 한 방이 강력합니다. 스킬 회피에 집중하세요!"));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#661529  ~  군단장의 성역  ~"), ColorManager.format("§7§o 적염의 군단장, 진 카인"));
                break;
            case 109:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#7C4557    공허의 대제, 카날로아의 성역으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 카날로아는 특정 체력에 도달할 때 마다 중앙으로 이동한 후 맵에 공허의 구를 발사합니다. 이 때, 카날로아는 무적입니다. 피하는데에 집중하세요."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 카날로아는 그의 본체의 정육면체의 구체와 함께 합니다. 정육면체의 공격은 매우 강력하니 주의하세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#7C4557  ~  공허의 중심  ~"), ColorManager.format("§7§o 공허의 대제, 카날로아"));
                break;
            case 110:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#20314F    까마귀의 기사, 레이븐의 전장으로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 레이븐은 특정 체력에 도달했을 때, 그의 용, 파프니르를 소환합니다. 함께 처치해야 합니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 파프니르의 화염 공격은 매우 강력하지만 시전하는 데에 시간이 꽤나 소요됩니다."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#20314F  ~  흑기사의 전장  ~"), ColorManager.format("§7§o 까마귀의 기사, 레이븐"));
                break;
            case 111:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#20314F    레기온의 군단장들이 등장했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 레기온의 군단장들이 각 성역을 벗어났기에 비교적 약해져 있습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 카인을 처치해야 다른 군단장들을 타격할 수 있습니다."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#20314F  ~  죽음의 전장  ~"), ColorManager.format("§7§o 레기온의 군단장들"));
                break;
            case 112:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#AC4141    화염 동굴의 거대 괴수, 바실리스크의 동굴로 진입했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 바실리스크는 여러분을 공격하다 지칩니다. 이 때만 데미지를 받습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 용암에 빠지면 최대 체력의 10% 고정 데미지를 받습니다."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#AC4141  ~  화염의 동굴  ~"), ColorManager.format("§7§o 거대 괴수, 바실리스크"));
                break;
            case 113:
                player.sendMessage("");
                player.sendMessage(ColorManager.format("#20314F    탑의 대제, 레기온이 등장했습니다. "));
                player.sendMessage(ColorManager.format(""));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 1 ] 레기온의 총 군단장을 먼저 처치해야 그를 처치할 수 있습니다."));
                player.sendMessage(ColorManager.format("§7§o        [ RAID TIP 2 ] 레기온은 가끔 맵에 레이저를 발사합니다. 이 레이저는 고정 데미지를 가하니 주의하세요."));
                player.sendMessage("");
                player.sendTitle(ColorManager.format("#20314F  ~  레기온의 성역  ~"), ColorManager.format("§7§o 탑의 대제, 레기온"));
                break;
        }
    }


    @EventHandler
    public void onDungeonQuit(DungeonFailedEvent e) {
        for (Player player : e.getDungeonRoom().getMembers()) {
            CommandUtil.runCommandAsOP(player, "spawn");

        }
    }

//    @EventHandler
//    public void onRaidFirstClear(DungeonClearEvent e) {
//        if (e.getDungeonRoom().getConnected().getId().equals(113) && e.getDungeonRoom().isClear()) {
//            String emptyMessage = "§c§n                                                                                 §r";
//
//            String message = ColorManager.format("             #474747탑#757575의 #D1D1D1대#FFFFFF제#FFFFFF가 #FFFFFF무#FFFFFF너#FFFFFF졌#EED7D7습#DDAFAF니#CB8787다#BA5F5F.");
//            List<PlayerData> members = e.getParty().getMembers();
//            int size = members.size();
//            int i = 0;
//
//            String parties = "§7 처치 파티: ";
//            for (PlayerData member : members) {
//                i++;
//                if (i != size) {
//                    parties += member.getPlayer().getName() + ", ";
//                } else {
//                    parties += member.getPlayer().getName();
//                }
//
//            }
//
//            Bukkit.broadcastMessage(emptyMessage);
//            Bukkit.broadcastMessage("");
//            Bukkit.broadcastMessage(message);
//            Bukkit.broadcastMessage(parties);
//            Bukkit.broadcastMessage(emptyMessage);
//
//
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyMessage);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, "");
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, message);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, parties);
//            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, emptyMessage);
//
//
//        }
//    }

}

