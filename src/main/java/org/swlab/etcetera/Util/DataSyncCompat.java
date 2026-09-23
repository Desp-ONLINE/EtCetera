package org.swlab.etcetera.Util;

import org.bukkit.entity.Player;
import org.swlab.etcetera.EtCetera;

/**
 * DataSync 브릿지.
 * 튜토리얼 채널(channelType = "tuto")에서는 DataSync 연동을 전부 끈다.
 * DataSync 클래스는 내부 클래스 안에서만 참조하므로,
 * 해당 플러그인이 서버에 없어도 NoClassDefFoundError 없이 동작한다.
 */
public final class DataSyncCompat {

    private static final String TUTORIAL_CHANNEL = "tuto";

    private DataSyncCompat() {
    }

    public static boolean isEnabled() {
        return !TUTORIAL_CHANNEL.equals(EtCetera.getChannelType());
    }

    /** 인벤토리 데이터 로드 중인지. DataSync 가 꺼진 채널에서는 항상 false. */
    public static boolean isDataLoading(Player player) {
        return isEnabled() && Sync.isDataLoading(player);
    }

    // ---------------------------------------------------------------- DataSync

    private static final class Sync {

        static boolean isDataLoading(Player player) {
            return org.dople.dataSync.inventory.InventorySyncListener.isDataLoading(player);
        }
    }
}
