package org.swlab.etcetera.Listener;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.api.DungeonClearEvent;
import com.binggre.mmodungeon.api.DungeonJoinEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.List;

public class DungeonLogListener implements Listener{

    static List<String> joinedUserNames = new ArrayList<>();

    public static List<String> getJoinedUserNames() {
        return joinedUserNames;
    }

    @EventHandler
    public void onItemConsume(DungeonJoinEvent e){
        List<String> users = new ArrayList<>();
        System.out.println("e.getParty().countMembers() = " + e.getParty().countMembers());
        System.out.println("e.getParty().getMember(0) = " + e.getParty().getMember(0));

        for (PlayerData member : e.getParty().getMembers()) {
            System.out.println("e.getParty().getMember(0).getPlayer().getName() = " + member.getPlayer().getName());
            joinedUserNames.add(member.getPlayer().getName()+e.getDungeon().getName());
        }
        System.out.println("레이드: "+e.getDungeon().getName()+" 참여. 멤버: ");
        for (PlayerData member : e.getParty().getMembers()) {
            System.out.println(member.getPlayer().getName()+e.getDungeon().getName());
        }
    }

    @EventHandler
    public void onDungeonClear(DungeonClearEvent e){
        System.out.println("레이드: "+e.getDungeonRoom().getConnected().getName()+" 종료. 멤버: ");
        for (PlayerData member : e.getParty().getMembers()) {
            System.out.print(member.getPlayer().getName()+ " ");
        }
        for (Player member : e.getDungeonRoom().getMembers()) {
            if(!joinedUserNames.remove(member.getName() + e.getDungeonRoom().getConnected().getName())){
            }
            System.out.println(member.getPlayer().getName()+ e.getDungeonRoom().getConnected().getName());
        }
    }

}