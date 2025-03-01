package org.swlab.etcetera;

import com.vexsoftware.votifier.model.Vote;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;
import org.swlab.etcetera.Commands.*;
import org.swlab.etcetera.Listener.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public final class EtCetera extends JavaPlugin {

    public static String channelType = "";
    public static int channelNumber = 0;
    public static EtCetera instance;

    public static EtCetera getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        FileConfiguration config = getConfig();
        config.addDefault("channelType", "lobby");
        config.addDefault("channelNumber", 0);
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        channelNumber = config.getInt("channelNumber");
        registerEvents();
        registerCommands();
        Set<OfflinePlayer> operators = Bukkit.getOperators();
        ArrayList<String> opUsers = new ArrayList<>(Arrays.asList("dople_L", "Dawn__L", "BingleBingleNao"));
        for (OfflinePlayer operator : operators) {
            if (!(opUsers.contains(operator.getName()))) {
                operator.setOp(false);
            }
        }

    }

    public static String getChannelType() {
        return channelType;
    }

    public static int getChannelNumber() {
        return channelNumber;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {
        if (channelType.equals("lobby")) {
            Bukkit.getPluginManager().registerEvents(new CrateListener(), this);
            Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
            Bukkit.getPluginManager().registerEvents(new DungeonFailListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new LeapListener(), this);
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClassChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new DialogSendListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNBTTagCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("광산").setExecutor(new MineWarpCommand());
        getCommand("던전").setExecutor(new DungeonCommand());
        getCommand("쓰레기통").setExecutor(new TrashcanCommand());
        getCommand("장비").setExecutor(new AccCommand());
        getCommand("강화").setExecutor(new ReinforceCommand());
        getCommand("합성").setExecutor(new MergeCommand());
        getCommand("전리품").setExecutor(new RewardSellCommand());
        getCommand("판도라").setExecutor(new PandoraCommand());
        getCommand("스텟").setExecutor(new StatCommand());
        getCommand("직업").setExecutor(new ClassSelectCommand());
        getCommand("낚시").setExecutor(new FishingCommand());
        getCommand("기본템").setExecutor(new BasicWeaponCommand());
        getCommand("퀘스트").setExecutor(new QuestCommand());
        getCommand("마을").setExecutor(new VillageCommand());
        getCommand("마을").setTabCompleter(new VillageCommand());
        getCommand("메뉴").setExecutor(new MenuCommand());
        getCommand("후원").setExecutor(new DonationCommand());
        getCommand("메일함").setExecutor(new MailBoxCommand());
        getCommand("시장").setExecutor(new MarketCommand());
        getCommand("추천").setExecutor(new VoteCommand());
        getCommand("디스코드").setExecutor(new DiscordCommand());
        getCommand("엔더상자").setExecutor(new EnderchestCommand());
        getCommand("창고").setExecutor(new ChestCommand());
        getCommand("파티").setExecutor(new PartyCommand());
        getCommand("루비").setExecutor(new CashCommand());
    }
}
