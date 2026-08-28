package org.swlab.etcetera.Convinience;

import java.util.HashMap;
import java.util.Map;

public class QuestNpcData {

    public record NpcLocation(String name, String world, double x, double y, double z, float yaw,
                              int requiredLevel, String regionName) {
    }

    private static final Map<Integer, String> QUEST_INFO = new HashMap<>();
    private static final Map<Integer, Integer> QUEST_NPC = new HashMap<>();
    private static final Map<Integer, Integer> QUEST_NPC_IN_PROGRESS = new HashMap<>();
    private static final Map<Integer, NpcLocation> NPC_LOCATIONS = new HashMap<>();

    public static String getQuestInfo(int questId) {
        return QUEST_INFO.get(questId);
    }

    public static int getMaxQuestId() {
        return QUEST_INFO.size();
    }

    public static boolean hasQuest(int questId) {
        return QUEST_INFO.containsKey(questId);
    }

    public static NpcLocation getNpcLocation(int questId) {
        Integer npcId = QUEST_NPC.get(questId);
        return npcId == null ? null : NPC_LOCATIONS.get(npcId);
    }

    /**
     * 퀘스트 수락 이후에 찾아가야 하는 NPC가 시작 NPC와 다른 경우의 위치. 없으면 null.
     */
    public static NpcLocation getInProgressNpcLocation(int questId) {
        Integer npcId = QUEST_NPC_IN_PROGRESS.get(questId);
        return npcId == null ? null : NPC_LOCATIONS.get(npcId);
    }

    public static int getMaxNpcQuestId() {
        return QUEST_NPC.keySet().stream().max(Integer::compareTo).orElse(0);
    }

    private static void putNpcRange(int fromQuestId, int toQuestId, int npcId) {
        for (int id = fromQuestId; id <= toQuestId; id++) {
            QUEST_NPC.put(id, npcId);
        }
    }

    static {
        QUEST_INFO.put(1, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(2, "이리스 §7(/던전 포탈 옆에 있습니다)");
        QUEST_INFO.put(3, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(4, "헤파이스토스 §7(/강화 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(5, "헤파이스토스 §7(/강화 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(6, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(7, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(8, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(9, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(10, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(11, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(12, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(13, "알비스 §7(/던전 깊은 숲 중앙에 있습니다)");
        QUEST_INFO.put(14, "알비스 §7(/던전 깊은 숲 중앙에 있습니다)");
        QUEST_INFO.put(15, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(16, "굴베이그 §7(/광산 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(17, "굴베이그 §7(/광산 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(18, "굴베이그 §7(/광산 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(19, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(20, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(21, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(22, "제미나이 §7(/로비 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(23, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(24, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(25, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(26, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(27, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(28, "길베르 §7(/엘븐하임 입장 후 중앙 기준 8시 방향에 있습니다)");
        QUEST_INFO.put(29, "길베르 §7(/엘븐하임 입장 후 중앙 기준 8시 방향에 있습니다)");
        QUEST_INFO.put(30, "길베르 §7(/엘븐하임 입장 후 중앙 기준 8시 방향에 있습니다)");
        QUEST_INFO.put(31, "길베르 §7(/엘븐하임 입장 후 중앙 기준 8시 방향에 있습니다)");
        QUEST_INFO.put(32, "길베르 §7(/엘븐하임 입장 후 중앙 기준 8시 방향에 있습니다)");
        QUEST_INFO.put(33, "시엘 §7(/엘븐하임 던전 메마른 숲 포탈 옆에 있습니다)");
        QUEST_INFO.put(34, "시엘 §7(/엘븐하임 던전 메마른 숲 포탈 옆에 있습니다)");
        QUEST_INFO.put(35, "시엘 §7(/엘븐하임 던전 메마른 숲 포탈 옆에 있습니다)");
        QUEST_INFO.put(36, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(37, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(38, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(39, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");
        QUEST_INFO.put(40, "드리아드 §7(/엘븐하임 입장 후 앞으로 이동하세요)");

        // Citizens saves.yml 기준 퀘스트 NPC 좌표 (npcId -> 위치, 지역 레벨 제한 포함)
        NPC_LOCATIONS.put(1, new NpcLocation("제미나이", "world", 41.3851, 37.0, -737.3096, 87.0f, 0, "로비"));
        NPC_LOCATIONS.put(10, new NpcLocation("굴베이그", "world", 1137.2001, 112.0, 1199.6587, 25.2f, 15, "광산"));
        NPC_LOCATIONS.put(13, new NpcLocation("드리아드", "world", 10003.9741, 0.0, 10002.3835, 137.0f, 20, "엘븐하임"));
        NPC_LOCATIONS.put(22, new NpcLocation("길베르", "world", 10047.2625, 0.0, 10046.9404, 54.8f, 20, "엘븐하임"));
        NPC_LOCATIONS.put(32, new NpcLocation("아크바르", "world", -9707.5206, 38.0, 10199.7484, -1.6f, 45, "칼리마"));
        NPC_LOCATIONS.put(33, new NpcLocation("칼라인", "world", -9608.736, 24.5, 10183.2699, -132.9f, 45, "칼리마"));
        NPC_LOCATIONS.put(34, new NpcLocation("마틸다", "world", -9793.5063, 23.0, 10314.7007, -150.9f, 45, "칼리마"));
        NPC_LOCATIONS.put(68, new NpcLocation("마틸다", "world", 15039.0454, 63.0, 14814.2274, 111.0f, 70, "인페리움"));
        NPC_LOCATIONS.put(69, new NpcLocation("녹스", "world", 15057.5139, 115.0, 14621.661, 3.8f, 70, "인페리움"));
        NPC_LOCATIONS.put(99, new NpcLocation("제크", "world", 15051.408, 90.0, 14687.7866, -97.4f, 70, "인페리움"));
        NPC_LOCATIONS.put(100, new NpcLocation("스노르델", "world", 94.6152, 172.0625, 9958.4611, -176.6f, 100, "아르크티카"));
        NPC_LOCATIONS.put(109, new NpcLocation("녹스", "world", 87.6102, 149.0, 9913.1829, -24.4f, 100, "아르크티카"));
        NPC_LOCATIONS.put(363, new NpcLocation("데본", "world", 5324.4371, 36.0, 55246.9069, -0.9f, 130, "엡실론"));
        NPC_LOCATIONS.put(10001, new NpcLocation("알비스", "world", -23.2982, -46.0, -12.2425, -159.6f, 0, "깊은 숲"));
        NPC_LOCATIONS.put(10002, new NpcLocation("시엘", "world", 314.6648, -64.9397, -89.5449, -165.5f, 20, "메마른 숲"));
        NPC_LOCATIONS.put(6, new NpcLocation("판도라", "world", 212.3256, 6.0, -814.4146, -41.3f, 0, "판도라의 정원"));
        NPC_LOCATIONS.put(7, new NpcLocation("헤파이스토스", "world", 130.4945, 18.0, -617.1983, 179.0f, 0, "강화의 성소"));
        NPC_LOCATIONS.put(8, new NpcLocation("이리스", "world", 153.7302, 0.0, -734.5607, 42.1f, 0, "던전 입구"));

        // 퀘스트 id -> 시작 NPC id (q/*.yml의 starterNPC 기준)
        putNpcRange(1, 12, 1);
        putNpcRange(13, 14, 10001);
        putNpcRange(15, 15, 1);
        putNpcRange(16, 18, 10);
        putNpcRange(19, 22, 1);
        putNpcRange(23, 27, 13);
        putNpcRange(28, 32, 22);
        putNpcRange(33, 35, 10002);
        putNpcRange(36, 40, 13);
        putNpcRange(41, 41, 32);
        putNpcRange(42, 48, 33);
        putNpcRange(49, 52, 34);
        putNpcRange(53, 54, 68);
        putNpcRange(55, 61, 69);
        putNpcRange(62, 66, 99);
        putNpcRange(67, 69, 69);
        putNpcRange(70, 70, 109);
        putNpcRange(71, 82, 100);
        putNpcRange(83, 83, 69);
        putNpcRange(84, 84, 100);
        putNpcRange(85, 85, 69);
        putNpcRange(86, 86, 100);
        putNpcRange(87, 87, 69);
        putNpcRange(88, 88, 100);
        putNpcRange(89, 89, 69);
        putNpcRange(90, 91, 100);
        putNpcRange(92, 120, 363);
        putNpcRange(121, 122, 100);
        putNpcRange(123, 124, 363);
        putNpcRange(125, 127, 100);

        // 수락 후 목적지가 시작 NPC와 다른 퀘스트 (수락 전에는 시작 NPC에게 이동, q/*.yml의 stage npcID 기준)
        QUEST_NPC_IN_PROGRESS.put(1, 8);   // 이리스와 대화
        QUEST_NPC_IN_PROGRESS.put(4, 7);   // 헤파이스토스에게 강화
        QUEST_NPC_IN_PROGRESS.put(5, 7);   // 헤파이스토스에게 강화
        QUEST_NPC_IN_PROGRESS.put(9, 6);   // 판도라에게 열쇠 구매
        QUEST_NPC_IN_PROGRESS.put(12, 10001); // 알비스를 찾아라
        QUEST_NPC_IN_PROGRESS.put(15, 10);    // 광산의 굴베이그와 대화
        QUEST_NPC_IN_PROGRESS.put(22, 13);    // 엘븐하임의 드리아드와 대화
        QUEST_NPC_IN_PROGRESS.put(27, 22);    // 길베르를 찾아가기
        QUEST_NPC_IN_PROGRESS.put(32, 10002); // 메마른 숲의 시엘과 대화
        QUEST_NPC_IN_PROGRESS.put(40, 32);    // 칼리마의 아크바르와 대화
        QUEST_NPC_IN_PROGRESS.put(41, 33);    // 칼라인과 대화
        QUEST_NPC_IN_PROGRESS.put(48, 34);    // 칼리마의 마틸다와 대화
        QUEST_NPC_IN_PROGRESS.put(52, 68);    // 인페리움의 마틸다와 대화
        QUEST_NPC_IN_PROGRESS.put(54, 69);    // 인페리움의 녹스를 찾아가기
        QUEST_NPC_IN_PROGRESS.put(61, 99);    // 제크를 찾아가기
        QUEST_NPC_IN_PROGRESS.put(66, 69);    // 녹스를 만나기
        QUEST_NPC_IN_PROGRESS.put(70, 100);   // 스노르델을 만나기
        QUEST_NPC_IN_PROGRESS.put(71, 109);   // 아르크티카의 녹스와 대화
        QUEST_NPC_IN_PROGRESS.put(82, 69);    // 인페리움의 녹스와 대화
        QUEST_NPC_IN_PROGRESS.put(83, 100);   // 스노르델에게 전달
        QUEST_NPC_IN_PROGRESS.put(84, 69);    // 인페리움의 녹스에게 유리구슬 전달
        QUEST_NPC_IN_PROGRESS.put(85, 100);   // 스노르델에게 전달
        QUEST_NPC_IN_PROGRESS.put(86, 69);    // 녹스에게 스펙터의 눈 전달
        QUEST_NPC_IN_PROGRESS.put(87, 100);   // 스노르델에게 물어보기
        QUEST_NPC_IN_PROGRESS.put(88, 69);    // 녹스에게 암흑 심장 전달
        QUEST_NPC_IN_PROGRESS.put(89, 100);   // 스노르델에게 전달
        QUEST_NPC_IN_PROGRESS.put(91, 363);   // 엡실론의 데본을 찾아가기
        QUEST_NPC_IN_PROGRESS.put(120, 100); // 스노르델을 다시 찾아가기
    }
}
