package org.swlab.etcetera.Commands;

import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

import java.util.ArrayList;
import java.util.List;

public class VillageCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        int level = mmoCoreAPI.getPlayerData(player).getLevel();
        if(args.length == 0) {
            CommandUtil.runCommandAsOP(player, "gui open 마을");
            return false;
        }
        switch (args[0]){
            case "엘븐하임":
                if(EtCetera.getChannelType().equals("lobby")){
                    if(level < 20){
                        player.sendMessage("§c  20레벨 미만은 엘븐하임에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "워프 이동 엘븐하임_입구");
                }
                else {
                    if(level < 20){
                        player.sendMessage("§c  20레벨 미만은 엘븐하임에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 엘븐하임_입구");
                }
                return false;
            case "칼리마":
                if(EtCetera.getChannelType().equals("lobby")){
                    if(level < 45){
                        player.sendMessage("§c  45레벨 미만은 칼리마에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "워프 이동 칼리마_입구");
                }
                else {
                    if(level < 45){
                        player.sendMessage("§c  45레벨 미만은 칼리마에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 칼리마_입구");
                }
                return false;
            case "인페리움":
                if(EtCetera.getChannelType().equals("lobby")){
                    if(level < 70){
                        player.sendMessage("§c  70레벨 미만은 인페리움에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "워프 이동 인페리움_입구");
                }
                else {
                    if(level < 70){
                        player.sendMessage("§c  70레벨 미만은 인페리움에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 인페리움_입구");
                }
                return false;
            case "아르크티카":
                if(EtCetera.getChannelType().equals("lobby")){
                    if(level < 100){
                        player.sendMessage("§c  100레벨 미만은 아르크티카에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "워프 이동 아르크티카_입구");
                }
                else {
                    if(level < 100){
                        player.sendMessage("§c  100레벨 미만은 아르크티카에 출입할 수 없습니다.");
                        return false;
                    }
                    CommandUtil.runCommandAsOP(player, "채널 워프 lobby 워프 이동 아르크티카_입구");
                }
                return false;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        List<String> villageList = new ArrayList<>();
        if(args.length == 1){
            Player player = (Player) sender;
            MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
            int level = mmoCoreAPI.getPlayerData(player).getLevel();
            if(level >= 20){
                villageList.add("엘븐하임");
            }
            if(level >= 45){
                villageList.add("칼리마");
            }
            if(level >= 70){
                villageList.add("인페리움");
            }
            if(level >= 100){
                villageList.add("아르크티카");
            }
            if(level<20){
                villageList.add("이동 가능 마을이 없습니다!");
            }
        }
        return villageList;
    }
}
