package org.swlab.etcetera.Listener;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.Inventory.VoteRewards;

import java.util.Arrays;
import java.util.List;

public class VoteListener implements Listener {
    @EventHandler
    public void onVote(VotifierEvent e){
        Vote vote = e.getVote();
        String username = vote.getUsername();
        VoteRewards voteRewards = new VoteRewards();
        Inventory inventory = voteRewards.getInventory();
        @Nullable ItemStack[] contents = inventory.getContents();
        List<ItemStack> rewards = Arrays.stream(contents).toList();
        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
        Mail mail = mailAPI.createMail(username, "sadg", 0, rewards);
        mailAPI.sendMail(username, mail);
    }
}
