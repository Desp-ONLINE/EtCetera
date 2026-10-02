package org.swlab.etcetera.DungeonChannel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 던전 채널 하나의 상태 스냅샷.
 *
 * @param online     접속 가능 여부 (하트비트가 살아 있는지)
 * @param players    접속 중인 플레이어 닉네임
 * @param areaCounts Area 이름별 인원 수 (예: "얕은숲" -> 3)
 */
public record ChannelStatus(int channel, boolean online, List<String> players, Map<String, Integer> areaCounts) {

    public static ChannelStatus offline(int channel) {
        return new ChannelStatus(channel, false, Collections.emptyList(), Collections.emptyMap());
    }

    public int areaCount(String areaName) {
        return areaCounts.getOrDefault(areaName, 0);
    }
}
