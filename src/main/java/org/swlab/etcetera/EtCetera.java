package org.swlab.etcetera;

import com.binggre.mmotimeraid.api.TimeRaidClearEvent;
import com.binggre.mmotimeraid.objects.TimeRaid;
import com.binggre.velocitysocketclient.VelocityClient;
import com.mongodb.client.MongoCollection;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.swlab.etcetera.Commands.*;
import org.swlab.etcetera.Convinience.SkillCooldownNotice;
import org.swlab.etcetera.Convinience.TipNotice;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Listener.*;
import org.swlab.etcetera.Placeholder.CooldownPlaceholder;
import org.swlab.etcetera.Placeholder.LevelPlaceholder;
import org.swlab.etcetera.Util.PetUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public final class EtCetera extends JavaPlugin {

    public static String channelType = "";
    public static int channelNumber = 0;
    public static EtCetera instance;

    private String lastCheckedDate = "";

    public static EtCetera getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CooldownPlaceholder(this).register();
            new LevelPlaceholder(this).register();
        }
        FileConfiguration config = getConfig();
        config.addDefault("channelType", "lobby");
        config.addDefault("channelNumber", 0);
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        channelNumber = config.getInt("channelNumber");
        new DatabaseRegister();
        registerEvents();
        registerCommands();
        startDayChangeCheckScheduler();

        Set<OfflinePlayer> operators = Bukkit.getOperators();
        ArrayList<String> opUsers = new ArrayList<>(Arrays.asList("dople_L", "Dawn__L", "BingleBingleNao"));
        for (OfflinePlayer operator : operators) {
            if (!(opUsers.contains(operator.getName()))) {
                operator.setOp(false);
            }
        }
        startAutoNotice();
        loadAllDatas();
        SkillCooldownNotice.scheduleStart();


        VelocityClient.getInstance().getConnectClient().registerListener(FirstJoinVelocityListener.class);
    }

    public static String getChannelType() {
        return channelType;
    }

    public static int getChannelNumber() {
        return channelNumber;
    }

    public void loadAllDatas() {
        new PetUtil();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PetUtil.loadPlayerPetData(player);
        }
    }

    public void saveAllDatas() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Pet activePet = MCPetsAPI.getActivePet(player.getUniqueId());
            if (!(activePet == null)) {
                String id = activePet.getId();
                PetUtil.savePlayerPetData(player, id);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveAllDatas();
    }

    public void startAutoNotice() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(TipNotice.getNotice());
                onlinePlayer.sendMessage("");
            }
        }, 20L, 2400L);
    }

    public void startDayChangeCheckScheduler() {
        if (!EtCetera.getChannelType().equals("lobby")) {
            return;
        }
        lastCheckedDate = getCurrentDate();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            String currentDate = getCurrentDate();
            if (!currentDate.equals(lastCheckedDate)) {
                MongoCollection<Document> jumpmapLog = DatabaseRegister.getInstance().getMongoDatabase().getCollection("JumpmapLog");
                Document first = jumpmapLog.find().first();
                Document updateDocument = new Document().append("players", new ArrayList<String>());
                jumpmapLog.updateOne(first, new Document("$set", updateDocument));
            }
            lastCheckedDate = getCurrentDate();
        }, 20L, 20L);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(System.currentTimeMillis()));
    }


    public void registerEvents() {
        if (channelType.equals("lobby")) {
            Bukkit.getPluginManager().registerEvents(new CrateListener(), this);
            Bukkit.getPluginManager().registerEvents(new DungeonListener(), this);


        }
        Bukkit.getPluginManager().registerEvents(new LeapListener(), this);
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
        Bukkit.getPluginManager().registerEvents(new PetListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        Bukkit.getPluginManager().registerEvents(new BabelListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClassChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new MiningAndFarmingListener(), this);
        Bukkit.getPluginManager().registerEvents(new DialogSendListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestExpansionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsumableListener(), this);
        Bukkit.getPluginManager().registerEvents(new ModelEngineListener(), this);
        Bukkit.getPluginManager().registerEvents(new JumpMapListener(), this);
        Bukkit.getPluginManager().registerEvents(new GoldItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new TimeDungeonListener(), this);
        Bukkit.getPluginManager().registerEvents(new AFKListener(), this);
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNBTTagCommand());
        if (!EtCetera.getChannelType().equalsIgnoreCase("afk")) {
            getCommand("spawn").setExecutor(new SpawnCommand());
        }
        getCommand("광산").setExecutor(new MineWarpCommand());
        getCommand("던전").setExecutor(new DungeonCommand());
        getCommand("아포칼립스").setExecutor(new ApocalypseCommand());
        getCommand("쓰레기통").setExecutor(new TrashcanCommand());
        getCommand("UI").setExecutor(new UICommand());
        getCommand("마나회복").setExecutor(new ManaCommand());
        getCommand("장비").setExecutor(new AccCommand());
        getCommand("강화").setExecutor(new ReinforceCommand());
        getCommand("도플명령어").setExecutor(new AdminCommand());
        getCommand("대결").setExecutor(new VersusCommand());
        getCommand("퀘스킵").setExecutor(new QuestSkipCommand());
        getCommand("채").setExecutor(new ChannelCommand());
        getCommand("쿨초기화").setExecutor(new CoolResetCommand());
        getCommand("튜토완료").setExecutor(new TutorialCompleteCommand());
        getCommand("양조").setExecutor(new BrewingCommand());
        getCommand("튜토리얼").setExecutor(new TutorialCommand());
        getCommand("텔레포트").setExecutor(new TeleportCommand());
        getCommand("쿠폰").setExecutor(new CouponCommand());
        getCommand("합성").setExecutor(new MergeCommand());
        getCommand("전리품").setExecutor(new RewardSellCommand());
        getCommand("장사글").setExecutor(new TradeCommand());
        getCommand("레이드").setExecutor(new RaidCommand());
        getCommand("타임던전").setExecutor(new TimeDungeonCommand());
        getCommand("판도라").setExecutor(new PandoraCommand());
        getCommand("스텟").setExecutor(new StatCommand());
        getCommand("직업").setExecutor(new ClassSelectCommand());
        getCommand("환던티켓지급").setExecutor(new AdventureCommand());
        getCommand("펫").setExecutor(new PetCommand());
        getCommand("낚시").setExecutor(new FishingCommand());
        getCommand("기본템").setExecutor(new BasicWeaponCommand());
        getCommand("퀘스트").setExecutor(new QuestCommand());
        getCommand("g").setExecutor(new GuildChatCommand());
        getCommand("훈련장").setExecutor(new TrainingCommand());
        getCommand("마을").setExecutor(new VillageCommand());
        getCommand("마을").setTabCompleter(new VillageCommand());
        getCommand("메뉴").setExecutor(new MenuCommand());
        getCommand("후원").setExecutor(new DonationCommand());
        getCommand("전체지급").setExecutor(new AllGIveCommand());
        getCommand("메일함").setExecutor(new MailBoxCommand());
        getCommand("시장").setExecutor(new MarketCommand());
        getCommand("채집").setExecutor(new FarmingCommand());
        getCommand("디스코드").setExecutor(new DiscordCommand());
        getCommand("엔더상자").setExecutor(new EnderchestCommand());
        getCommand("창고").setExecutor(new ChestCommand());
        getCommand("파티").setExecutor(new PartyCommand());
        getCommand("루비").setExecutor(new CashCommand());
        getCommand("잠수").setExecutor(new AFKCommand());
        getCommand("잠수포인트").setExecutor(new AFKPointCommand());
        getCommand("PVP").setExecutor(new PvpCommand());
        getCommand("음악").setExecutor(new MusicCommand());
        getCommand("정보").setExecutor(new InformationCommand());
        getCommand("복구").setExecutor(new RestoreCommand());
        getCommand("도움말").setExecutor(new HelpCommand());
        getCommand("필드보스").setExecutor(new BossCommand());
        getCommand("친구").setExecutor(new FriendCommand());
        getCommand("엘븐하임").setExecutor(new ElvenheimCommand());
        getCommand("칼리마").setExecutor(new KalimaCommand());
        getCommand("인페리움").setExecutor(new InferiumCommand());
        getCommand("아르크티카").setExecutor(new ArcticaCommand());
        getCommand("엡실론").setExecutor(new EpsilonCommand());
    }
}
