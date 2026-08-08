package org.swlab.etcetera.Training.ranking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DamageRankEntry implements Comparable<DamageRankEntry> {

    private final UUID uuid;
    private final String playerName;
    private final String job;
    private final double damage;

    @Override
    public int compareTo(DamageRankEntry other) {
        return Double.compare(other.damage, this.damage);
    }
}
