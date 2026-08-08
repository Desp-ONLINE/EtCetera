package org.swlab.etcetera.Training.ranking;

import com.binggre.mongolibraryplugin.base.MongoData;
import lombok.Getter;

@Getter
public class RankingData implements MongoData<String> {

    private final String id;
    private final String uuid;
    private final String playerName;
    private final String job;
    private final double damage;

    public RankingData(String uuid, String playerName, String job, double damage) {
        this.id = uuid + "_" + job;
        this.uuid = uuid;
        this.playerName = playerName;
        this.job = job;
        this.damage = damage;
    }

    @Override
    public String getId() {
        return id;
    }
}
