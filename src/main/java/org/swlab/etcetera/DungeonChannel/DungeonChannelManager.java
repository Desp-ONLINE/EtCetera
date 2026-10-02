package org.swlab.etcetera.DungeonChannel;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 던전 채널 상태 공유.
 * - 던전 서버(channelType=dungeon): 5초마다 접속자 목록과 Area 별 인원을 Mongo 에 기록 (하트비트)
 * - 모든 서버: 2초마다 전체 채널 문서를 읽어 캐시, 열려 있는 던전 GUI 를 갱신
 * 하트비트가 끊긴 채널은 접속 불가로 표시한다.
 */
public class DungeonChannelManager {

    public static final int CHANNEL_COUNT = 8;

    private static final String COLLECTION = "DungeonChannelStatus";
    private static final long PUBLISH_INTERVAL_TICKS = 100L;
    private static final long POLL_INTERVAL_TICKS = 40L;
    // 하트비트(updatedAt)가 이 시간 동안 바뀌지 않으면 서버가 죽은 것으로 본다 (읽는 서버 시계 기준이라 서버 간 시차와 무관)
    private static final long STALE_MILLIS = 20_000L;
    // 처음 보는 문서는 변화 여부를 알 수 없으므로 기록한 서버 시계 기준으로 판단 (시차 감안해 넉넉히)
    private static final long ABSOLUTE_STALE_MILLIS = 60_000L;

    private static DungeonChannelManager instance;

    private final MongoCollection<Document> collection;
    private final Map<Integer, ChannelStatus> cache = new ConcurrentHashMap<>();
    // 채널별 마지막으로 본 updatedAt 과, 그 값이 바뀐 것을 확인한 로컬 시각
    private final Map<Integer, Long> lastUpdatedAt = new HashMap<>();
    private final Map<Integer, Long> lastChangedLocal = new HashMap<>();
    private final AtomicBoolean polling = new AtomicBoolean(false);
    private final List<BukkitTask> tasks = new ArrayList<>();

    private DungeonChannelManager() {
        collection = DatabaseRegister.getInstance().getMongoDatabase().getCollection(COLLECTION);
    }

    public static DungeonChannelManager getInstance() {
        return instance;
    }

    public static void enable(EtCetera plugin) {
        instance = new DungeonChannelManager();
        instance.start(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }
        instance.tasks.forEach(BukkitTask::cancel);
        instance.tasks.clear();
        if (isDungeonServer()) {
            // 정상 종료 시 바로 접속 불가로 표시 (하트비트 만료를 기다리지 않음)
            try {
                instance.collection.updateOne(Filters.eq("_id", EtCetera.getChannelNumber()),
                        Updates.combine(Updates.set("online", false), Updates.set("players", new ArrayList<>()),
                                Updates.set("areaCounts", new Document())));
            } catch (Exception e) {
                EtCetera.getInstance().getLogger().warning("던전 채널 상태 종료 기록 실패: " + e.getMessage());
            }
        }
        instance = null;
    }

    public static boolean isDungeonServer() {
        int number = EtCetera.getChannelNumber();
        return "dungeon".equals(EtCetera.getChannelType()) && number >= 1 && number <= CHANNEL_COUNT;
    }

    public ChannelStatus getStatus(int channel) {
        return cache.getOrDefault(channel, ChannelStatus.offline(channel));
    }

    private void start(EtCetera plugin) {
        if (isDungeonServer()) {
            tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> publish(plugin), 20L, PUBLISH_INTERVAL_TICKS));
        }
        tasks.add(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::poll, 30L, POLL_INTERVAL_TICKS));
        tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, DungeonChannelManager::refreshOpenGuis, 40L, POLL_INTERVAL_TICKS));
    }

    /** 메인 스레드에서 스냅샷을 만들고 Mongo 기록은 비동기로. */
    private void publish(EtCetera plugin) {
        List<String> players = new ArrayList<>();
        Map<String, Integer> areaCounts = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isVanished(player)) {
                continue;
            }
            players.add(player.getName());
            String areaName = AreaCompat.getAreaName(player);
            if (areaName != null) {
                areaCounts.merge(areaName, 1, Integer::sum);
            }
        }
        players.sort(String.CASE_INSENSITIVE_ORDER);

        Document document = new Document("_id", EtCetera.getChannelNumber())
                .append("online", true)
                .append("players", players)
                .append("areaCounts", new Document(new HashMap<>(areaCounts)))
                .append("updatedAt", System.currentTimeMillis());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                collection.replaceOne(Filters.eq("_id", EtCetera.getChannelNumber()), document, new ReplaceOptions().upsert(true));
            } catch (Exception e) {
                plugin.getLogger().warning("던전 채널 상태 기록 실패: " + e.getMessage());
            }
        });
    }

    private void poll() {
        if (!polling.compareAndSet(false, true)) {
            return;
        }
        try {
            long now = System.currentTimeMillis();
            Set<Integer> seen = new HashSet<>();
            for (Document document : collection.find()) {
                if (!(document.get("_id") instanceof Number id)) {
                    continue;
                }
                int channel = id.intValue();
                seen.add(channel);
                cache.put(channel, toStatus(channel, document, now));
            }
            for (int channel = 1; channel <= CHANNEL_COUNT; channel++) {
                if (!seen.contains(channel)) {
                    cache.put(channel, ChannelStatus.offline(channel));
                }
            }
        } catch (Exception e) {
            EtCetera.getInstance().getLogger().warning("던전 채널 상태 조회 실패: " + e.getMessage());
        } finally {
            polling.set(false);
        }
    }

    private ChannelStatus toStatus(int channel, Document document, long now) {
        long updatedAt = document.get("updatedAt") instanceof Number n ? n.longValue() : 0L;
        Long previous = lastUpdatedAt.get(channel);
        if (previous == null) {
            lastUpdatedAt.put(channel, updatedAt);
            // 처음 본 문서: 기록 서버 시계 기준으로 이미 오래됐다면 변화가 없었던 것으로 취급
            lastChangedLocal.put(channel, now - updatedAt > ABSOLUTE_STALE_MILLIS ? 0L : now);
        } else if (previous != updatedAt) {
            lastUpdatedAt.put(channel, updatedAt);
            lastChangedLocal.put(channel, now);
        }

        boolean alive = now - lastChangedLocal.getOrDefault(channel, 0L) <= STALE_MILLIS;
        if (!alive || !document.getBoolean("online", false)) {
            return ChannelStatus.offline(channel);
        }

        List<String> players = new ArrayList<>(document.getList("players", String.class, new ArrayList<>()));
        players.sort(Comparator.comparing(String::toLowerCase));
        Map<String, Integer> areaCounts = new HashMap<>();
        Document areas = document.get("areaCounts", Document.class);
        if (areas != null) {
            areas.forEach((name, value) -> {
                if (value instanceof Number count) {
                    areaCounts.put(name, count.intValue());
                }
            });
        }
        return new ChannelStatus(channel, true, List.copyOf(players), Map.copyOf(areaCounts));
    }

    private static void refreshOpenGuis() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
            if (holder instanceof DungeonChannelGui gui) {
                gui.render();
            } else if (holder instanceof DungeonTeleportGui gui) {
                gui.render();
            }
        }
    }

    private static boolean isVanished(Player player) {
        for (MetadataValue value : player.getMetadata("vanished")) {
            if (value.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
