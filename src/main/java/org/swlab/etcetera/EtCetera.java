package org.swlab.etcetera;

import com.binggre.velocitysocketclient.VelocityClient;
import com.mongodb.client.MongoCollection;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.Commands.*;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.swlab.etcetera.Convinience.ClassSelectGui;
import org.swlab.etcetera.Convinience.QuestBossBar;
import org.swlab.etcetera.Convinience.SkillCooldownNotice;
import org.swlab.etcetera.Convinience.TipNotice;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Listener.*;
import org.swlab.etcetera.Placeholder.ChannelPlaceholder;
import org.swlab.etcetera.Placeholder.EntityPotionHudPlaceholder;
import org.swlab.etcetera.Placeholder.CombatPowerPlaceholder;
import org.swlab.etcetera.Placeholder.CooldownPlaceholder;
import org.swlab.etcetera.Placeholder.LevelPlaceholder;
import org.swlab.etcetera.Repositories.DogamRegisterRepository;
import org.swlab.etcetera.Repositories.MimicRepository;
import org.swlab.etcetera.Repositories.RaidCoinRepository;
import org.swlab.etcetera.Repositories.HiddenExchangeRepository;
import org.swlab.etcetera.Repositories.TutorialRepository;
import org.swlab.etcetera.Repositories.QuestAlertSettingRepository;
import org.swlab.etcetera.Repositories.UserSettingRepository;
import org.swlab.etcetera.Repositories.WeeklyRaidLimitRepository;
import org.swlab.etcetera.Ranking.AllianceLevelRankingProvider;
import org.swlab.etcetera.Ranking.BabelTowerRankingProvider;
import org.swlab.etcetera.Ranking.CombatPowerRankingProvider;
import org.swlab.etcetera.Ranking.GuildRaidRankingProvider;
import org.swlab.etcetera.Ranking.RankingHologramManager;
import org.swlab.etcetera.Ranking.TVersusRankingProvider;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Util.CommandUtil;
import org.swlab.etcetera.Util.DataSyncCompat;
import org.swlab.etcetera.Util.PetUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
            new ChannelPlaceholder(this).register();
            new CooldownPlaceholder(this).register();
            new LevelPlaceholder(this).register();
            new CombatPowerPlaceholder(this).register();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("BetterHud")) {
            new EntityPotionHudPlaceholder().register();
            getLogger().info("BetterHud 엔티티 포션 플레이스홀더 등록 완료 (entity_potion_duration / entity_potion_amplifier)");
        } else {
            getLogger().warning("BetterHud가 없거나 아직 활성화되지 않아 엔티티 포션 플레이스홀더를 등록하지 못했습니다.");
        }
        FileConfiguration config = getConfig();
        config.addDefault("channelType", "lobby");
        config.addDefault("channelNumber", 0);
        config.addDefault("tradeHighlightColor.buy", "#51A037");
        config.addDefault("tradeHighlightColor.sell", "#D9C338");
        config.addDefault("tradeHighlightColor.recruit", "#556E6D");
        // 훈련 세션 로그에 기록되는 밸런스 패치 버전. 밸런스 패치 시마다 올려서 전후 비교에 쓴다
        config.addDefault("training.balanceVersion", "v1");
        config.options().copyDefaults(true);
        saveConfig();
        channelType = config.getString("channelType");
        channelNumber = config.getInt("channelNumber");
        new DatabaseRegister();
        registerEvents();
        registerCommands();
        registerRepositories();
        TrainingManager.enable(this);
        RankingHologramManager.enable(this);
        RankingHologramManager.getInstance().registerProvider(new CombatPowerRankingProvider());
        RankingHologramManager.getInstance().registerProvider(new BabelTowerRankingProvider());
        if (isGuildEnabled()) {
            RankingHologramManager.getInstance().registerProvider(new GuildRaidRankingProvider());
        }
        RankingHologramManager.getInstance().registerProvider(new AllianceLevelRankingProvider());
        RankingHologramManager.getInstance().registerProvider(new TVersusRankingProvider());
        startDayChangeCheckScheduler();

        Set<OfflinePlayer> operators = Bukkit.getOperators();
        ArrayList<String> opUsers = new ArrayList<>(Arrays.asList("dople_L", "BingleBangleSoju", "IDE_SoRim"));
        for (OfflinePlayer operator : operators) {
            if (!(opUsers.contains(operator.getName()))) {
                operator.setOp(false);
            }
        }
        startAutoNotice();
        startClassSelectCheckScheduler();
        loadAllDatas();
        RaidCoinRepository.getInstance().loadCoinData();
        MimicRepository.getInstance().loadData();
        DogamRegisterRepository.getInstance().loadData();
        SkillCooldownNotice.scheduleStart();
        for (Player player : Bukkit.getOnlinePlayers()) {
            QuestBossBar.getInstance().show(player);
        }


        VelocityClient.getInstance().getConnectClient().registerListener(FirstJoinVelocityListener.class);
    }

    public void registerRepositories(){
        new UserSettingRepository();
        new TutorialRepository();
        new MimicRepository();
        new DogamRegisterRepository();
        new QuestAlertSettingRepository();
    }

    public static String getChannelType() {
        return channelType;
    }

    /** 튜토리얼 채널에서는 길드(MMOGuild) 연동을 끈다. */
    public static boolean isGuildEnabled() {
        return !channelType.equals("tuto");
    }

    public static int getChannelNumber() {
        return channelNumber;
    }

    public void loadAllDatas() {
        new PetUtil();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PetUtil.loadPlayerPetData(player);
            UserSettingRepository.getInstance().loadUserSetting(player);
            TutorialRepository.getInstance().loadTutorialData(player);
            DataLoadListener.getInstance().putPlayerData(player);
            RaidCoinRepository.getInstance().loadUserData(player);
            HiddenExchangeRepository.getInstance().loadUserData(player);
        }
        // 주간 레이드 횟수는 접속 시에만 로드되므로, 리로드 시 접속 중인 유저는 여기서 다시 로드한다
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            for (Player player : onlinePlayers) {
                if (!player.isOnline()) continue;
                WeeklyRaidLimitRepository.getInstance().loadUserData(player);
            }
        });

    }

    public void saveAllDatas() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Pet activePet = MCPetsAPI.getActivePet(player.getUniqueId());
            if (!(activePet == null)) {
                String id = activePet.getId();
                PetUtil.savePlayerPetData(player, id);

            }
            UserSettingRepository.getInstance().saveUserSetting(player);
            RaidCoinRepository.getInstance().saveUserData(player);
            HiddenExchangeRepository.getInstance().saveUserData(player);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        RankingHologramManager.disable();
        TrainingManager.disable();
        QuestBossBar.getInstance().removeAll();
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

    /**
     * 15초마다 직업이 없는(HUMAN) 유저를 스폰으로 이동시키고 직업 선택 GUI 를 띄운다.
     * 직업 변경은 로비에서만 가능하므로 로비에서만 동작하며, 튜토리얼 진행 중이거나
     * MMOCore 데이터가 아직 로드되지 않은(로드 전에는 기본 직업으로 보임) 유저는 제외한다.
     */
    public void startClassSelectCheckScheduler() {
        if (!EtCetera.getChannelType().equals("lobby")) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!PlayerData.has(player.getUniqueId())) continue;
                PlayerData playerData = PlayerData.get(player.getUniqueId());
                if (!playerData.isFullyLoaded()) continue;
                if (!playerData.getProfess().getName().equalsIgnoreCase("HUMAN")) continue;
                if (!TutorialRepository.getInstance().isTutorialCompleted(player)) continue;
                // 이미 직업을 고르고 있는 중이면 다시 이동시키지 않는다
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof ClassSelectGui) continue;

                CommandUtil.runCommandAsOP(player, "spawn");
                player.sendMessage("§c직업을 선택해주세요!");
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (player.isOnline()) {
                        ClassSelectGui.open(player);
                    }
                }, 10L);
            }
        }, 300L, 300L);
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
        Bukkit.getPluginManager().registerEvents(new MimicListener(), this);
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
        Bukkit.getPluginManager().registerEvents(new PetListener(), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        Bukkit.getPluginManager().registerEvents(new BabelListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClassChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new MiningAndFarmingListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestExpansionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsumableListener(), this);
        Bukkit.getPluginManager().registerEvents(new DataLoadListener(), this);
        // 튜토리얼 채널에서는 DataSync 연동을 끈다
        if (DataSyncCompat.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(new DataSyncListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new ModelEngineListener(), this);
        Bukkit.getPluginManager().registerEvents(new JumpMapListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new GoldItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new TrashcanListener(), this);
        Bukkit.getPluginManager().registerEvents(new HiddenExchangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClassSelectListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSearchListener(), this);
        Bukkit.getPluginManager().registerEvents(new CataclysmMirrorListener(), this);
        // 첫 클리어 보상은 길드 경험치를 지급하므로 MMOGuild 에 의존한다
        if (isGuildEnabled()) {
            Bukkit.getPluginManager().registerEvents(new FirstClearListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new AFKListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuestAnnounceListener(), this);
        if (Bukkit.getPluginManager().getPlugin("IDEQuest") != null) {
            Bukkit.getPluginManager().registerEvents(new IDEQuestListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new ItemFrameProtectListener(), this);
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            Bukkit.getPluginManager().registerEvents(new FurnitureProtectListener(), this);
        }
    }

    public void registerCommands() {
        getCommand("nbt검사").setExecutor(new CheckNBTTagCommand());
            getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("광산").setExecutor(new MineWarpCommand());
        getCommand("던전").setExecutor(new DungeonCommand());
        getCommand("설정").setExecutor(new UserSettingCommand());
        getCommand("아포칼립스").setExecutor(new ApocalypseCommand());
        getCommand("쓰레기통").setExecutor(new TrashcanCommand());
        getCommand("히든교환").setExecutor(new HiddenExchangeCommand());
        getCommand("히든재료교환").setExecutor(new HiddenExchangeCommand());
        getCommand("일괄분해").setExecutor(new DecompositeCommand());
        getCommand("일괄판매").setExecutor(new SellAllRewardCommand());
        getCommand("UI").setExecutor(new UICommand());
        if (isGuildEnabled()) {
            getCommand("길드레이드").setExecutor(new GuildRaidCommand());
            getCommand("g").setExecutor(new GuildChatCommand());
            // FirstClearListener 가 초기화한 컬렉션을 사용하는 명령어
            getCommand("레이드챌린지").setExecutor(new RaidChallengeCommand());
            getCommand("타임던전첫클리어보상").setExecutor(new TimeDungeonFirstClearCommand());
        }
        getCommand("보스장비").setExecutor(new RaidEquipmentCommand());
        getCommand("공헌의탑").setExecutor(new ContributeTowerCommand());
        getCommand("마나회복").setExecutor(new ManaCommand());
        getCommand("장비2").setExecutor(new AccCommand());
        getCommand("강화").setExecutor(new ReinforceCommand());
        getCommand("보스코인").setExecutor(new RaidCoinCommand());
        getCommand("도플명령어").setExecutor(new AdminCommand());
        getCommand("대결").setExecutor(new VersusCommand());
        getCommand("스케줄").setExecutor(new ScheduleCommand());
        getCommand("퀘스킵").setExecutor(new QuestSkipCommand());
        getCommand("초월완료").setExecutor(new AscendCommand());
        getCommand("채").setExecutor(new ChannelCommand());
        getCommand("쿨초기화").setExecutor(new CoolResetCommand());
        getCommand("쿨타임감소").setExecutor(new CooldownReduceCommand());
        getCommand("템").setExecutor(new ItemSearchCommand());
        getCommand("환던").setExecutor(new AdventureWarpCommand());
        getCommand("튜토완료").setExecutor(new TutorialCompleteCommand());
        getCommand("양조").setExecutor(new BrewingCommand());
        getCommand("튜토리얼").setExecutor(new TutorialCommand());
        getCommand("텔레포트").setExecutor(new TeleportCommand());
        getCommand("쿠폰").setExecutor(new CouponCommand());
        getCommand("사다리타기").setExecutor(new RandomLadderCommand());
        getCommand("합성").setExecutor(new MergeCommand());
        getCommand("전리품").setExecutor(new RewardSellCommand());
        getCommand("장사글").setExecutor(new TradeCommand());
        getCommand("레이드").setExecutor(new RaidCommand());
        getCommand("레이드횟수").setExecutor(new WeeklyRaidCountCommand());
        getCommand("타임던전").setExecutor(new TimeDungeonCommand());
        getCommand("판도라").setExecutor(new PandoraCommand());
        getCommand("1").setExecutor(new Lobby1Command());
        getCommand("2").setExecutor(new Lobby2Command());
        StatCommand statCommand = new StatCommand();
        getCommand("스텟").setExecutor(statCommand);
        getCommand("스텟").setTabCompleter(statCommand);
        getCommand("직업").setExecutor(new ClassSelectCommand());
        getCommand("환던티켓지급").setExecutor(new AdventureCommand());
        getCommand("베스페라").setExecutor(new VesperaCommand());
        getCommand("펫").setExecutor(new PetCommand());
        getCommand("낚시").setExecutor(new FishingCommand());
        getCommand("기본템").setExecutor(new BasicWeaponCommand());
        getCommand("퀘스트알림").setExecutor(new QuestAlertCommand());
        getCommand("마을").setExecutor(new VillageCommand());
        getCommand("마을").setTabCompleter(new VillageCommand());
        getCommand("메뉴").setExecutor(new MenuCommand());
        getCommand("후원").setExecutor(new DonationCommand());
        getCommand("전체지급").setExecutor(new AllGIveCommand());
        getCommand("메일함").setExecutor(new MailBoxCommand());
        getCommand("시장").setExecutor(new MarketCommand());
        getCommand("채집").setExecutor(new FarmingCommand());
        getCommand("디스코드").setExecutor(new DiscordCommand());
        getCommand("엔더상자").setExecutor(new EnderChestCommand());
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
        getCommand("점멸").setExecutor(new BlinkCommand());
        getCommand("질문").setExecutor(new QuestionCommand());
        DogamRegisterCommand dogamRegisterCommand = new DogamRegisterCommand();
        getCommand("도감등록증").setExecutor(dogamRegisterCommand);
        getCommand("도감등록증").setTabCompleter(dogamRegisterCommand);
        RankingLocationCommand rankingLocationCommand = new RankingLocationCommand();
        getCommand("랭킹위치설정").setExecutor(rankingLocationCommand);
        getCommand("랭킹위치설정").setTabCompleter(rankingLocationCommand);
    }
}
