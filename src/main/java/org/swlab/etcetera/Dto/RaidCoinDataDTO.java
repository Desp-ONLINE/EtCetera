package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RaidCoinDataDTO {

    private String raidName;
    private Integer normalAmount;
    private Integer specialAmount;

}
