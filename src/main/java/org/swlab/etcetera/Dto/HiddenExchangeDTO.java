package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HiddenExchangeDTO {

    private String uuid;
    private String nickname;
    private int usedCount;

}
