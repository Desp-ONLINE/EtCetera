package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MimicDataDTO {

    private List<String> eliteInternalNames;
    private String mimicInternalName;
    private int spawnChance;
    private String rewardType;
    private String rewardId;
    private int rewardAmount;

}
