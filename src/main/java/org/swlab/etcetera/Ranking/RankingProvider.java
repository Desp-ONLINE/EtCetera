package org.swlab.etcetera.Ranking;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 랭킹 홀로그램 컨텐츠 제공자.
 *
 * <p>외부 플러그인(바벨탑, 길드레이드 등)은 이 인터페이스를 구현하고
 * onEnable에서 {@link RankingHologramManager#registerProvider(RankingProvider)}로 등록하면
 * 15분 주기 갱신과 /랭킹위치설정 명령어에 자동으로 편입된다.
 */
public interface RankingProvider {

    /** 컨텐츠 식별자. /랭킹위치설정 &lt;키&gt; 와 위치 저장 파일에 그대로 쓰인다. (예: "전투력") */
    String getContentKey();

    /**
     * 메인 스레드에서 실행되는 수집 단계 (온라인 플레이어 스냅샷 등).
     * 필요 없으면 구현하지 않아도 된다.
     */
    default void collectSync() {
    }

    /**
     * 홀로그램에 표시할 줄 목록 (헤더 포함, 색코드 포함).
     * 비동기 스레드에서 호출되므로 DB 조회를 해도 된다.
     */
    List<String> buildLines();

    /**
     * 유저 접속 직후 개인 순위 줄 계산 전에 메인 스레드에서 호출되는 수집 단계.
     * 정기 갱신 때는 {@link #collectSync()}가 대신 호출되므로,
     * 접속자 단위 스냅샷(길드 소속 등)이 필요한 컨텐츠만 구현하면 된다.
     */
    default void collectViewerSync(org.bukkit.entity.Player player) {
    }

    /**
     * 접속자별 개인 순위 줄 (홀로그램 하단에 본인에게만 표시).
     *
     * <p>보통 {@link #buildLines()} 직후 같은 비동기 스레드에서 호출되지만,
     * 유저 접속 직후에는 buildLines 없이 단독으로도 호출되므로
     * 마지막 집계 캐시를 사용해 구현하면 된다. 비동기 스레드이므로 DB 조회를 해도 된다.
     *
     * @param viewers 메인 스레드에서 수집한 접속자 스냅샷 (uuid → 닉네임)
     * @return uuid → 색코드 포함 한 줄. 맵에 없는 유저는 개인 순위 줄을 표시하지 않는다.
     */
    default Map<UUID, String> buildViewerLines(Map<UUID, String> viewers) {
        return Map.of();
    }

    /**
     * 홀로그램 주변에 뿌릴 파티클 색상. null이면 파티클을 표시하지 않는다.
     */
    default org.bukkit.Color getParticleColor() {
        return null;
    }
}
