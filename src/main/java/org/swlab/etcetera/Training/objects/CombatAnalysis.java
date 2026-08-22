package org.swlab.etcetera.Training.objects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CombatAnalysis {

    @Getter
    public static class Record {
        private final String source;
        /** 시전 아이템의 MMOItems ID (MMOItems 아이템이 아니면 null) */
        private String itemId;
        private double totalDamage;
        private int hits;
        private double maxHit;

        private Record(String source) {
            this.source = source;
        }

        private void add(double damage) {
            totalDamage += damage;
            hits++;
            maxHit = Math.max(maxHit, damage);
        }
    }

    private final Map<String, Record> records = new LinkedHashMap<>();

    public void record(String source, String itemId, double damage) {
        Record record = records.computeIfAbsent(source, Record::new);
        record.add(damage);
        if (record.itemId == null && itemId != null) {
            record.itemId = itemId;
        }
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public double getTotalDamage() {
        return records.values().stream().mapToDouble(Record::getTotalDamage).sum();
    }

    public List<Record> sortedByDamage() {
        List<Record> sorted = new ArrayList<>(records.values());
        sorted.sort(Comparator.comparingDouble(Record::getTotalDamage).reversed());
        return sorted;
    }
}
