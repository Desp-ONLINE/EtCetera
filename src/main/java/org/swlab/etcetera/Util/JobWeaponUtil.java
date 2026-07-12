package org.swlab.etcetera.Util;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.players.PlayerQuestDatasImplementation;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * 직업무기 MMOItems ID 파싱/생성 유틸.
 * ID 형식 : 직업무기_<차수 1자리><직업이름><강화등급 숫자>  (Type = SWORD)
 * 예) 직업무기_2크루세이더0 → 2차 크루세이더 +0
 */
public final class JobWeaponUtil {

    public static final String WEAPON_TYPE = "SWORD";
    public static final String ID_PREFIX = "직업무기_";

    public static final List<String> JOB_NAMES = List.of(
            "드레드노트", "크루세이더", "파우스트", "오베론", "인페르노",
            "제피르", "루인드", "판", "페이탈", "퀘이사"
    );

    /** 6차 이상 무기 변환에 필요한 직업별 6차 전직 퀘스트 ID */
    public static final Map<String, Integer> TIER6_QUEST_IDS = Map.of(
            "드레드노트", 90070, "크루세이더", 90071, "파우스트", 90072,
            "오베론", 90073, "인페르노", 90074, "제피르", 90075,
            "루인드", 90076, "판", 90077, "페이탈", 90078, "퀘이사", 90079
    );

    private static final char QUEST_REQUIRED_TIER = '6';

    private JobWeaponUtil() {
    }

    public record JobWeapon(char tier, String job, String enhance) {
    }

    /**
     * 아이템이 직업무기라면 차수/직업/강화 등급을 파싱해 반환하고, 아니면 null 을 반환한다.
     */
    public static JobWeapon parse(ItemStack item) {
        String id = MMOItems.getID(item);
        if (id == null || !id.startsWith(ID_PREFIX)) {
            return null;
        }
        String typeName = MMOItems.getTypeName(item);
        if (typeName == null || !typeName.equals(WEAPON_TYPE)) {
            return null;
        }

        String rest = id.substring(ID_PREFIX.length());
        if (rest.length() < 2 || !Character.isDigit(rest.charAt(0))) {
            return null;
        }
        char tier = rest.charAt(0);

        // 뒤에서부터 이어지는 숫자가 강화 등급, 그 사이가 직업 이름
        int jobEnd = rest.length();
        while (jobEnd > 1 && Character.isDigit(rest.charAt(jobEnd - 1))) {
            jobEnd--;
        }
        String job = rest.substring(1, jobEnd);
        String enhance = rest.substring(jobEnd);
        if (enhance.isEmpty() || !JOB_NAMES.contains(job)) {
            return null;
        }
        return new JobWeapon(tier, job, enhance);
    }

    public static String buildId(char tier, String job, String enhance) {
        return ID_PREFIX + tier + job + enhance;
    }

    /**
     * 해당 차수의 무기 변환에 6차 전직 퀘스트 클리어가 필요한지 여부.
     */
    public static boolean requiresTier6Quest(char tier) {
        return tier >= QUEST_REQUIRED_TIER;
    }

    /**
     * 플레이어가 해당 직업의 6차 전직 퀘스트를 클리어했는지 확인한다.
     */
    public static boolean hasFinishedTier6Quest(Player player, String job) {
        Integer questId = TIER6_QUEST_IDS.get(job);
        if (questId == null) {
            return false;
        }
        Quest quest = BeautyQuests.getInstance().getAPI().getQuestsManager().getQuest(questId);
        if (quest == null) {
            return false;
        }
        PlayerQuestDatasImplementation questDatas =
                BeautyQuests.getInstance().getPlayersManager().getAccount(player).getQuestDatas(quest);
        return questDatas != null && questDatas.isFinished();
    }
}
