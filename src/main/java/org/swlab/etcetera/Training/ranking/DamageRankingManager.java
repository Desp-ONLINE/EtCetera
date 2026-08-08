package org.swlab.etcetera.Training.ranking;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DamageRankingManager {

    private static final String ALL_KEY = "모두";
    private static final long REFRESH_INTERVAL = 20L * 60 * 30; // 30분

    private final RankingRepository repository;
    private final Map<String, List<DamageRankEntry>> rankingByJob = new ConcurrentHashMap<>();
    private final int taskId;

    public DamageRankingManager(Plugin plugin) {
        repository = new RankingRepository(plugin, "Training", "Ranking", new HashMap<>());
        repository.init();
        refreshRanking();
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshRanking, REFRESH_INTERVAL, REFRESH_INTERVAL).getTaskId();
    }

    public void addRecord(UUID uuid, String playerName, String job, double damage) {
        String key = uuid + "_" + job;
        RankingData existing = repository.get(key);
        if (existing == null || damage > existing.getDamage()) {
            RankingData data = new RankingData(uuid.toString(), playerName, job, damage);
            repository.putIn(data);
            repository.save(data);
            refreshRanking();
        }
    }

    public void refreshRanking() {
        Map<String, List<DamageRankEntry>> newRankings = new HashMap<>();
        Map<UUID, DamageRankEntry> allBest = new HashMap<>();

        for (RankingData data : repository.values()) {
            UUID uuid = UUID.fromString(data.getUuid());
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.isOp()) {
                continue;
            }
            String job = data.getJob();
            DamageRankEntry entry = new DamageRankEntry(uuid, data.getPlayerName(), job, data.getDamage());
            newRankings.computeIfAbsent(job, k -> new ArrayList<>()).add(entry);

            DamageRankEntry best = allBest.get(uuid);
            if (best == null || data.getDamage() > best.getDamage()) {
                allBest.put(uuid, entry);
            }
        }

        for (List<DamageRankEntry> list : newRankings.values()) {
            Collections.sort(list);
        }

        List<DamageRankEntry> allRanking = new ArrayList<>(allBest.values());
        Collections.sort(allRanking);
        newRankings.put(ALL_KEY, allRanking);

        rankingByJob.clear();
        rankingByJob.putAll(newRankings);
    }

    public DamageRankEntry getRank(String job, int position) {
        List<DamageRankEntry> list = rankingByJob.get(job);
        if (list == null || position < 1 || position > list.size()) {
            return null;
        }
        return list.get(position - 1);
    }

    public int getPlayerRank(UUID uuid, String job) {
        List<DamageRankEntry> list = rankingByJob.get(job);
        if (list == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUuid().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }

    public DamageRankEntry getPlayerEntry(UUID uuid, String job) {
        List<DamageRankEntry> list = rankingByJob.get(job);
        if (list == null) {
            return null;
        }
        for (DamageRankEntry entry : list) {
            if (entry.getUuid().equals(uuid)) {
                return entry;
            }
        }
        return null;
    }

    public Set<String> getJobs() {
        return rankingByJob.keySet();
    }

    public void shutdown() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
