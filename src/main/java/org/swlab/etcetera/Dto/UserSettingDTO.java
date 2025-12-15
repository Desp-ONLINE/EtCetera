package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.swlab.etcetera.Repositories.UserSettingRepository;

@Getter
@Setter
@Builder
public class UserSettingDTO {

    private String uuid;
    private boolean isVisibleInformation;

}
