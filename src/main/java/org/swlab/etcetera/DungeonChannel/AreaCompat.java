package org.swlab.etcetera.DungeonChannel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Area 플러그인(org.khw3268480.area) 연동.
 * Area 가 EtCetera 를 참조하고 있어 컴파일 의존을 걸면 순환이 되므로, 리플렉션으로 AreaAPI#getAreaName 만 호출한다.
 */
public final class AreaCompat {

    private static Plugin resolvedPlugin;
    private static Method getAreaName;
    private static Object apiInstance;

    private AreaCompat() {
    }

    /** 플레이어가 서 있는 Area 이름(예: "얕은숲"). Area 플러그인이 없거나 영역 밖이면 null. */
    public static String getAreaName(Player player) {
        if (!resolve()) {
            return null;
        }
        try {
            return (String) getAreaName.invoke(apiInstance, player);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean resolve() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Area");
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        // 리로드로 플러그인 인스턴스가 바뀌면 다시 찾는다
        if (plugin == resolvedPlugin && apiInstance != null) {
            return true;
        }
        try {
            Class<?> apiClass = plugin.getClass().getClassLoader().loadClass("org.khw3268480.area.Api.AreaAPI");
            Object instance = apiClass.getField("instance").get(null);
            if (instance == null) {
                return false;
            }
            getAreaName = apiClass.getMethod("getAreaName", Player.class);
            apiInstance = instance;
            resolvedPlugin = plugin;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
