package org.swlab.etcetera.Listener;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent e){
        Vote vote = e.getVote();
        String username = vote.getUsername();
        Player player = Bukkit.getPlayer(username);
        player.sendMessage("§a  추천 보상이 정상 지급되었습니다! §7§o( /메일함 )");
        ItemStack voteCoin = MMOItems.plugin.getItem("MISCELLANEOUS", "기타_추천코인");
        ItemStack ruby = MMOItems.plugin.getItem("CONSUMABLE", "기타_루비");
        List<ItemStack> items = new ArrayList<>();
        items.add(voteCoin);
        items.add(ruby);
        MMOMail mmoMail = MMOMail.getInstance();
        Mail rewardMail = mmoMail.getMailAPI().createMail("시스템", "추천 보상입니다.", 0, items);
        mmoMail.getMailAPI().sendMail(username, rewardMail);
    }
}
