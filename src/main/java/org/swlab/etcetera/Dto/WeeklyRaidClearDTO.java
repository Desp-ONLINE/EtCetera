package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WeeklyRaidClearDTO {

    private String uuid;
    private String nickname;
    private Integer clearCount;
    private String weekStart;

}
