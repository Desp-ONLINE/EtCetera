package org.swlab.etcetera.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CombatPowerUtil;

import java.text.NumberFormat;

/**
 * 전투력 플레이스홀더. (식별자: cp)
 *
 * <ul>
 *   <li>%cp_total% : 억/만 단위 한글 표기 (예: 1억 2345만 6789)</li>
 *   <li>%cp_comma% : 천 단위 콤마 (예: 123,456,789)</li>
 *   <li>%cp_raw%   : 콤마 없는 정수값 (예: 123456789)</li>
 * </ul>
 */
public class CombatPowerPlaceholder extends PlaceholderExpansion {

    private final EtCetera etCetera;

    public CombatPowerPlaceholder(EtCetera etCetera) {
        this.etCetera = etCetera;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dople";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "0";
        }

        // 레벨은 오프라인 데이터로도 조회 가능 (전투력 계산과 무관하므로 먼저 처리)
        PlayerData playerData = PlayerData.get(player.getUniqueId());
        int level = playerData.getLevel();
        if (identifier.equalsIgnoreCase("level")) {
            return String.valueOf(level);
        }

        // 전투력은 온라인 스탯맵(MMOPlayerData)이 필요하므로 접속 중일 때만 계산
        Player online = player.getPlayer();
        long combatPower = online == null ? 0L : CombatPowerUtil.calculate(online);

        switch (identifier.toLowerCase()) {
            case "raw":
                return String.valueOf(combatPower);
            case "comma":
                return NumberFormat.getInstance().format(combatPower);
            case "total":
            default:
                return CombatPowerUtil.toKoreanUnit(combatPower);
        }
    }
}
