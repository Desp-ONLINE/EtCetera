package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.desp.upgrade.dto.UpgradeData;
import org.desp.upgrade.event.UpgradeDestroyEvent;
import org.desp.upgrade.event.UpgradeSuccessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Upgrade 플러그인 강화 이벤트 처리.
 * 퀘스트 증표 지급·초월 방송은 QuestExpansion에서 이전(2026-09-03).
 */
public class UpgradeListener implements Listener {

    private static final List<String> JOBS = List.of(
            "인페르노", "크루세이더", "파우스트", "오베론", "제피르",
            "루인드", "판", "페이탈", "퀘이사", "드레드노트");

    /** 첫 무기 강화 성공(1강) — 퀘스트 증표 지급 대상 */
    private static final List<String> FIRST_UPGRADE_WEAPONS = JOBS.stream()
            .map(job -> "직업무기_1" + job + "1").toList();

    /** 초월 무기 획득 — 전 서버 방송 + 초월 권한 부여 */
    private static final List<String> CHOWOL_WEAPONS = JOBS.stream()
            .map(job -> "직업무기_6" + job + "0").toList();

    private static final String HEPHAESTUS = " #FFD866헤파이스토스 #495057│ #F1F3F5";

    @EventHandler
    public void onUpgrade(UpgradeSuccessEvent e) {
        Player player = e.getPlayer();
        String afterWeapon = e.getUpgradeData().getAfterWeapon();

        if (afterWeapon.equals("합성무기_초월자의대검0")) {
            String excaliberDominanceMessage = ColorManager.format("#3C53BA        " + player.getName() + " §f님께서 #33387C&n초#35488D&n월#38579F&n자#3A67B0&n의 #3A6AB3&n대#385DA5&n검#375198&n, #33387C&n엑#394B9D&n스#3E5DBE&n칼#4470DE&n리#4982FF&n버 §f의 주인이 되었습니다.");
            sendBroadcast(excaliberDominanceMessage);
        }

        if (FIRST_UPGRADE_WEAPONS.contains(afterWeapon)) {
            giveQuestToken(player, "퀘스트_강화의증표",
                    "첫 번째 강화 성공을 축하하네. 이 증표를 제미나이에게 가져다 주면 퀘스트를 성공한 것으로 확인해 줄 것이네.");
        }

        if (afterWeapon.equals("방어구_모험가1")) {
            giveQuestToken(player, "퀘스트_갑옷강화의증표",
                    "갑옷 강화 성공을 축하하네. 이 증표를 제미나이에게 가져다 주면 퀘스트를 성공한 것으로 확인해 줄 것이네.");
        }

        if (CHOWOL_WEAPONS.contains(afterWeapon)) {
            String jobName = afterWeapon.replace("직업무기_6", "").replace("0", "");
            String message = ColorManager.format("§f      " + player.getName() + " §7님께서 §6" + jobName + " §7(으)로서의 #5E76FF자신의 한계§7를 넘어섰습니다.");
            sendBroadcast("", message, "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set chowol." + jobName);
        }
    }

    private void giveQuestToken(Player player, String mmoItemId, String line) {
        ItemStack token = MMOItems.plugin.getItem("MISCELLANEOUS", mmoItemId);
        player.sendMessage(ColorManager.format(HEPHAESTUS + line));
        if (token == null) {
            Bukkit.getLogger().warning("[EtCetera] MMOItems MISCELLANEOUS." + mmoItemId + " 아이템을 찾을 수 없어 증표를 지급하지 못했습니다: " + player.getName());
            return;
        }
        player.getInventory().addItem(token).values()
                .forEach(rest -> player.getWorld().dropItem(player.getLocation(), rest));
    }

    /** 앞뒤로 빈 줄을 하나씩 두고 방송한다. */
    public void sendBroadcast(String msg) {
        sendBroadcast("", msg, "");
    }

    /** 주어진 줄들을 그대로(빈 줄 포함) 이 서버와 벨로시티 전체에 방송한다. 첫·마지막 줄도 빈 줄로 감싼다. */
    private void sendBroadcast(String... lines) {
        List<String> all = new ArrayList<>();
        all.add("");
        all.addAll(Arrays.asList(lines));
        all.add("");
        for (String line : all) {
            Bukkit.broadcastMessage(line);
            VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, line);
        }
    }

    @EventHandler
    public void onDestroyed(UpgradeDestroyEvent e) {
        Player player = e.getPlayer();
        UpgradeData upgradeData = e.getUpgradeData();
        String afterWeapon = upgradeData.getAfterWeapon();
        if (afterWeapon.startsWith("주간반지_")) {
            String substring = afterWeapon.substring(0, afterWeapon.length() - 1);
            // 아르카디엘만 기본 반지 ID가 "주간반지_아르카디엘_연마1" 로 지어져 있음
            if (afterWeapon.startsWith("주간반지_아르카디엘_")) {
                substring = "주간반지_아르카디엘_연마1";
            }
            ItemStack ringWeek = MMOItems.plugin.getItem("RING_WEEK", substring);

            Mail mail = MMOMail.getInstance().getMailAPI().createMail("시스템", "연마 파괴에 대한 반지 보상입니다.", 0, new ArrayList<>(Arrays.asList(ringWeek)));
            MMOMail.getInstance().getMailAPI().sendMail(player.getName(), mail);
        }
    }
}
