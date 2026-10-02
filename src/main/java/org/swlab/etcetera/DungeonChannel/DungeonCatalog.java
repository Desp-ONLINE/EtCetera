package org.swlab.etcetera.DungeonChannel;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 던전(사냥터) 목록. 기존 GUI 플러그인의 "텔레포트1~8" 메뉴 아이템을 그대로 옮긴 것.
 * areaName 은 Area 플러그인의 영역 이름이자 워프 이름("<areaName>_입구")의 앞부분이다.
 */
public final class DungeonCatalog {

    public record DungeonEntry(int number, int slot, String areaName, Material material, String displayName, List<String> lore) {
    }

    private static final List<DungeonEntry> ENTRIES = new ArrayList<>();

    static {
        add(1, 1, "얕은숲", Material.GRASS_BLOCK, "§a 얕은 숲 §7§o( 첫 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 더스티 [§eLv.1§f]",
                "§f - 포레스트호그 [§eLv.3§f]",
                "§f - 포레스티안 [§eLv.5§f]");

        add(2, 2, "깊은숲", Material.DIRT_PATH, "§2 깊은 숲 §7§o( 두 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 다크 뱃 [§eLv.8§f]",
                "§f - 크로우 [§eLv.10§f]",
                "§f - 다크 울프 [§eLv.12§f]");

        add(3, 3, "고블린야영지", Material.PODZOL, "§2 고블린 야영지 §7§o( 세 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 고블린 나무꾼 [§eLv.14§f]",
                "§f - 고블린 일꾼 [§eLv.17§f]",
                "§f - 고블린 병사 [§eLv.20§f]");

        add(19, 5, "균형의길", Material.STRIPPED_MANGROVE_LOG, "§x§f§f§9§f§9§f균§x§e§5§a§d§b§7형§x§c§b§b§a§c§f의 §x§9§7§d§5§f§f길 §7§o ( 열 아홉 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 피로스 리본 [§eLv.162§f]",
                "§f - 아이셜 리본 [§eLv.166§f]",
                "§f - 피로스 카니지 [§eLv.170§f]");

        add(20, 6, "이율의협곡", Material.STRIPPED_WARPED_STEM, "§x§f§a§7§4§7§4이§x§d§d§8§4§8§f율§x§c§0§9§3§a§b의 §x§8§6§b§2§e§2협§x§6§9§c§2§f§d곡 §7§o ( 스무 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 아이셜 카니지 [§eLv.175§f]",
                "§f - 피로스 네크로 [§eLv.179§f]",
                "§f - 아이셜 네크로 [§eLv.183§f]");

        add(21, 7, "설화의화맥", Material.PALE_OAK_LOG, "§x§f§f§2§5§2§5설§x§d§1§3§e§5§1화§x§a§4§5§8§7§c의 §x§4§9§8§b§d§3화§x§1§b§a§4§f§f맥 §7§o ( 스물 한 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 피로스 랜서 [§eLv.188§f]",
                "§f - 아이셜 랜서 [§eLv.192§f]",
                "§f - 피로스 센티넬 [§eLv.196§f]");

        add(4, 10, "요정의숲", Material.MOSS_BLOCK, "§b 요정의 숲 §7§o( 네 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 대지의 요정 [§eLv.23§f]",
                "§f - 숲의 요정 [§eLv.25§f]",
                "§f - 호수의 요정 [§eLv.28§f]");

        add(5, 11, "엘프반군은거지", Material.MYCELIUM, "§6 엘프 반군 은거지 §7§o( 다섯 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 엘프 반군 정령 [§eLv.30§f]",
                "§f - 엘프 반군 전사 [§eLv.33§f]",
                "§f - 엘프 반군 궁수 [§eLv.36§f]");

        add(6, 12, "메마른숲", Material.PACKED_MUD, "§6 메마른 숲 §7§o( 여섯 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 메마른 푸른 정령 [§eLv.38§f]",
                "§f - 메마른 붉은 정령 [§eLv.41§f]",
                "§f - 분노한 나무 정령 [§eLv.45§f]");

        add(7, 19, "황무지", Material.SANDSTONE, "§6 황무지 §7§o( 일곱 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 무어 캐멀 [§eLv.49§f]",
                "§f - 무어 언데드 [§eLv.52§f]",
                "§f - 무어 스켈레톤 [§eLv.55§f]");

        add(8, 20, "사막의중심", Material.SAND, "§6 사막의 중심 §7§o( 여덟 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 데저트 아르마딜로 [§eLv.58§f]",
                "§f - 데저트 라마 [§eLv.61§f]",
                "§f - 데저트 스콜피온 [§eLv.63§f]");

        add(9, 21, "이글거리는곳", Material.RED_SAND, "§c 이글거리는 곳 §7§o( 아홉 번째 던전 )",
                "",
                "§7[ 등장 몬스터 ]",
                "§f - 본 브레이커 [§eLv.65§f]",
                "§f - 올드 샤먼 [§eLv.68§f]",
                "§f - 블레이즈 [§eLv.70§f]");

        add(10, 28, "블레이즈서식지", Material.RED_NETHER_BRICKS, "§x§f§f§6§a§8§2블§x§f§2§5§f§7§4레§x§e§6§5§5§6§6이§x§d§9§4§a§5§8즈 §x§c§0§3§4§3§b서§x§b§4§2§a§2§d식§x§a§7§1§f§1§f지 §7§o( 열 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 블레이즈 워리어 [§eLv.73§f]",
                "§f - 블레이즈 메이지 [§eLv.76§f]",
                "§f - 하이 블레이즈 [§eLv.80§f]");

        add(11, 29, "열이식어가는곳", Material.NETHERRACK, "§x§f§f§7§e§9§3열§x§f§0§8§c§a§1이 §x§d§2§a§9§b§c식§x§c§4§b§8§c§9어§x§b§5§c§6§d§7가§x§a§6§d§4§e§4는 §x§8§8§f§1§f§f곳 §7§o( 열 한 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 제네럴 블레이즈 [§eLv.83§f]",
                "§f - 예티 [§eLv.86§f]",
                "§f - 서리 부엉이 [§eLv.90§f]");

        add(12, 30, "냉기의화원", Material.SNOW_BLOCK, "§x§5§4§d§a§f§4냉§x§5§4§c§1§e§8기§x§5§4§a§8§d§b의 §x§5§4§7§7§c§2화§x§5§4§5§e§b§6원 §7§o( 열 두 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 야크 [§eLv.93§f]",
                "§f - 서리 독수리 [§eLv.97§f]",
                "§f - 페르마프로스트 [§eLv.100§f]");

        add(13, 37, "공허로향하는길", Material.PURPLE_CONCRETE, "§x§4§7§7§7§c§5공§x§4§d§7§3§c§3허§x§5§3§6§e§c§1로 §x§6§0§6§6§b§e향§x§6§6§6§1§b§c하§x§6§c§5§d§b§a는 §x§7§8§5§4§b§6길 §7§o( 열 세 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 보이드 락 [§eLv.105§f]",
                "§f - 보이드 크랩 [§eLv.108§f]",
                "§f - 보이드 헤지혹 [§eLv.112§f]");

        add(14, 38, "원무의공간", Material.OBSIDIAN, "§x§a§1§4§7§c§5원§x§9§2§4§3§b§a무§x§8§3§4§0§a§f의 §x§6§4§3§9§9§8공§x§5§5§3§5§8§d간 §7§o( 열 네 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 보이드 클램 [§eLv.115§f]",
                "§f - 보이드 슬라임 [§eLv.118§f]",
                "§f - 보이드 스펙터 [§eLv.121§f]");

        add(15, 39, "아공간", Material.CRYING_OBSIDIAN, "§x§5§1§1§c§6§6아§x§5§2§3§7§5§c공§x§5§2§5§2§5§2간 §7§o( 열 다섯 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 보이드 리자드 [§eLv.123§f]",
                "§f - 보이드 키네틱 [§eLv.126§f]",
                "§f - 보이드 리퍼 [§eLv.130§f]");

        add(16, 46, "망자의기로", Material.CHERRY_WOOD, "§x§8§1§4§f§a§5망§x§7§4§5§3§8§c자§x§6§8§5§7§7§4의 §x§5§b§5§b§5§b기로 §7§o( 열 여섯 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 데칼 언데드 [§eLv.132§f]",
                "§f - 보우먼 언데드 [§eLv.135§f]",
                "§f - 워록 언데드 [§eLv.138§f]");

        add(17, 47, "망각의영역", Material.TUFF, "§x§5§b§5§b§5§b망§x§7§f§7§f§7§f각§x§a§4§a§4§a§4의 §x§9§2§9§2§9§2영§x§5§b§5§b§5§b역 §7§o( 열 일곱 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 마시알 [§eLv.140§f]",
                "§f - 블라드 [§eLv.144§f]",
                "§f - 펜서 [§eLv.148§f]");

        add(18, 48, "소멸된회랑", Material.CRACKED_DEEPSLATE_TILES, "§x§7§6§8§1§9§b소§x§6§8§7§1§9§4멸§x§5§9§6§2§8§d된 §x§6§1§6§a§9§1회§x§7§6§8§1§9§b랑 §7§o( 열 여덟 번째 던전 )",
                "",
                "§7 [ 등장 몬스터 ]",
                "§f - 아르쿠스 [§eLv.151§f]",
                "§f - 소시르 [§eLv.154§f]",
                "§f - 오버웨이트 [§eLv.157§f]");
    }

    private DungeonCatalog() {
    }

    public static List<DungeonEntry> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    private static void add(int number, int slot, String areaName, Material material, String displayName, String... lore) {
        ENTRIES.add(new DungeonEntry(number, slot, areaName, material, displayName, List.of(lore)));
    }
}
