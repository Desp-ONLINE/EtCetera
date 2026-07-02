package org.swlab.etcetera.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 히든 재료 교환 시스템 설정 값 모음.
 * 실제 서버의 MMOItems ID 에 맞게 아래 목록/ID 를 수정하세요.
 */
public class HiddenMaterialConfig {

    private HiddenMaterialConfig() {
    }

    // 교환 시 소모되는 재료 (MMOItems)
    public static final String COST_ITEM_TYPE = "MISCELLANEOUS";
    public static final String COST_ITEM_ID = "기타_원초의핵";
    public static final int COST_AMOUNT = 15;

    // 주간 교환 가능 횟수 (월요일 자정 초기화 - WeeklyResetEvent)
    public static final int WEEKLY_LIMIT = 1;

    // 히든 재료는 모두 MISCELLANEOUS 타입
    public static final String HIDDEN_MATERIAL_TYPE = "MISCELLANEOUS";

    // 히든 재료 아이템 ID 목록 - 실제 MMOItems ID 로 교체하세요.
    public static final List<String> HIDDEN_MATERIAL_IDS = Collections.unmodifiableList(Arrays.asList(
            "히든_타계의별",
            "히든_영겁의서약석",
            "히든_에이션트프리즘"
    ));
}
