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

    public void record(String source, double damage) {
        records.computeIfAbsent(source, Record::new).add(damage);
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
