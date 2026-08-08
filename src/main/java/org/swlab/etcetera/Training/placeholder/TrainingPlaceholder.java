package org.swlab.etcetera.Training.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.ranking.DamageRankEntry;
import org.swlab.etcetera.Training.ranking.DamageRankingManager;

public class TrainingPlaceholder extends PlaceholderExpansion {

    private final TrainingManager training;

    public TrainingPlaceholder(TrainingManager training) {
        this.training = training;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "training";
    }

    @Override
    public @NotNull String getAuthor() {
        return "binggre";
    }

    @Override
    public @NotNull String getVersion() {
        return EtCetera.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        DamageRankingManager manager = training.getRankingManager();
        if (manager == null) {
            return "";
        }
        // %training_rank_<직업|모두>_<순위>[_name|_damage]%
        if (params.startsWith("rank_")) {
            return handleRank(manager, params.substring("rank_".length()));
        }
        // %training_myrank_<직업|모두>%
        if (params.startsWith("myrank_") && player != null) {
            String job = params.substring("myrank_".length());
            int rank = manager.getPlayerRank(player.getUniqueId(), job);
            return rank == -1 ? "-" : String.valueOf(rank);
        }
        // %training_mydamage_<직업|모두>%
        if (params.startsWith("mydamage_") && player != null) {
            String job = params.substring("mydamage_".length());
            DamageRankEntry entry = manager.getPlayerEntry(player.getUniqueId(), job);
            return entry == null ? "-" : String.format("%.1f", entry.getDamage());
        }
        return null;
    }

    private String handleRank(DamageRankingManager manager, String sub) {
        String[] parts = sub.split("_");
        if (parts.length < 2) {
            return "";
        }

        // 직업 이름에 _가 들어갈 수 있어 뒤에서부터 해석한다: [직업]_[순위][_name|_damage]
        String field = null;
        String lastPart = parts[parts.length - 1];
        int posIndex;
        if (lastPart.equals("name") || lastPart.equals("damage")) {
            field = lastPart;
            posIndex = parts.length - 2;
        } else {
            posIndex = parts.length - 1;
        }

        int position;
        try {
            position = Integer.parseInt(parts[posIndex]);
        } catch (NumberFormatException e) {
            return "";
        }
        if (posIndex < 1) {
            return "";
        }

        StringBuilder jobBuilder = new StringBuilder(parts[0]);
        for (int i = 1; i < posIndex; i++) {
            jobBuilder.append("_").append(parts[i]);
        }
        String job = jobBuilder.toString();

        DamageRankEntry entry = manager.getRank(job, position);
        if (entry == null) {
            return "-";
        }
        if (field == null) {
            return String.format("%s - %.1f", entry.getPlayerName(), entry.getDamage());
        }
        return switch (field) {
            case "name" -> entry.getPlayerName();
            case "damage" -> String.format("%.1f", entry.getDamage());
            default -> "";
        };
    }
}
