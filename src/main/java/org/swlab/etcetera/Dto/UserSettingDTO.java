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
    private boolean showDamageChat;
    private boolean showSkillCooldownNotice;
    private boolean showSkillCooldownItem;
    private int playerTime;
    /** /쿨타임감소 가 적용될 핫바 슬롯 번호(1~9). 기본 2. */
    private int cooldownReduceSlot;

}
