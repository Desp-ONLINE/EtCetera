package org.swlab.etcetera.Ranking;

import java.util.List;

/**
 * 랭킹 홀로그램 컨텐츠 제공자.
 *
 * <p>외부 플러그인(바벨탑, 길드레이드 등)은 이 인터페이스를 구현하고
 * onEnable에서 {@link RankingHologramManager#registerProvider(RankingProvider)}로 등록하면
 * 30분 주기 갱신과 /랭킹위치설정 명령어에 자동으로 편입된다.
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
     * 홀로그램 주변에 뿌릴 파티클 색상. null이면 파티클을 표시하지 않는다.
     */
    default org.bukkit.Color getParticleColor() {
        return null;
    }
}
