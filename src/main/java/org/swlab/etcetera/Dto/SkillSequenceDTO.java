package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** 자동 연계 시스템: 플레이어가 등록한 합성무기 키(강화 단계 제외 ID) 목록. 순서대로 시전된다. */
@Getter
@Setter
@Builder
public class SkillSequenceDTO {

    private String uuid;
    private List<String> weapons;
}
