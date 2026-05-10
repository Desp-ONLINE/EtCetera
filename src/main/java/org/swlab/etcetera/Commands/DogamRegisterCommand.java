package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.api.MailAPI;
import com.binggre.mmomail.objects.Mail;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.Dto.DogamRegisterDTO;
import org.swlab.etcetera.Dto.DogamRegisterDTO.CertificateInfo;
import org.swlab.etcetera.Dto.DogamRegisterDTO.MMOItemRef;
import org.swlab.etcetera.Dto.DogamRegisterDTO.RequirementGroup;
import org.swlab.etcetera.Repositories.DogamRegisterRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DogamRegisterCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsageMessage(player);
            return true;
        }

        switch (args[0]) {
            case "발급":
                if (args.length < 2) {
                    sendUsageMessage(player);
                    return true;
                }
                StringBuilder nameBuilder = new StringBuilder(args[1]);
                for (int i = 2; i < args.length; i++) {
                    nameBuilder.append(" ").append(args[i]);
                }
                handleIssue(player, nameBuilder.toString());
                return true;
            case "리로드":
                handleReload(player);
                return true;
            default:
                sendUsageMessage(player);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("발급");
            if (player.isOp()) {
                options.add("리로드");
            }
            return filterPrefix(options, args[0]);
        }

        if (args.length == 2 && args[0].equals("발급")) {
            return filterPrefix(DogamRegisterRepository.getInstance().getAllNames(), args[1]);
        }

        return Collections.emptyList();
    }

    private List<String> filterPrefix(List<String> options, String typed) {
        if (typed == null || typed.isEmpty()) {
            return options;
        }
        String lower = typed.toLowerCase();
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(lower)) {
                result.add(option);
            }
        }
        return result;
    }

    private void handleIssue(Player player, String dogamName) {
        DogamRegisterDTO dto = DogamRegisterRepository.getInstance().getByName(dogamName);
        if (dto == null) {
            player.sendMessage(ColorManager.format("§c[도감등록증] §f'" + dogamName + "' 도감 정보를 찾을 수 없습니다."));
            return;
        }

        List<RequirementGroup> requirements = dto.getRequirements();
        if (requirements == null || requirements.isEmpty()) {
            player.sendMessage(ColorManager.format("§c[도감등록증] §f해당 도감의 등록 조건이 설정되어 있지 않습니다."));
            return;
        }

        ItemStack[] storage = player.getInventory().getStorageContents();

        for (RequirementGroup group : requirements) {
            int owned = countOwned(storage, group);
            if (owned < group.getAmount()) {
                sendMissingMessage(player, dogamName, group, owned);
                return;
            }
        }

        for (RequirementGroup group : requirements) {
            if (!group.isConsume()) {
                continue;
            }
            consumeFromInventory(storage, group, group.getAmount());
        }
        player.getInventory().setStorageContents(storage);

        CertificateInfo cert = dto.getCertificate();
        if (cert == null || cert.getType() == null || cert.getId() == null) {
            player.sendMessage(ColorManager.format("§c[도감등록증] §f발급할 등록증 정보가 잘못 설정되어 있습니다. 관리자에게 문의해주세요."));
            return;
        }

        ItemStack certificate = MMOItems.plugin.getItem(cert.getType(), cert.getId());
        if (certificate == null) {
            player.sendMessage(ColorManager.format("§c[도감등록증] §f발급할 등록증 아이템을 찾을 수 없습니다. 관리자에게 문의해주세요."));
            return;
        }
        certificate.setAmount(Math.max(1, cert.getAmount()));

        List<ItemStack> mailItems = new ArrayList<>();
        mailItems.add(certificate);

        MailAPI mailAPI = MMOMail.getInstance().getMailAPI();
        Mail mail = mailAPI.createMail("시스템", "§f" + dogamName + " 도감등록증입니다.", 0.0, mailItems);
        mailAPI.sendMail(player.getName(), mail);

        DogamRegisterRepository.getInstance().recordIssue(player, dogamName);

        player.sendMessage("");
        player.sendMessage(ColorManager.format("§a[도감등록증] §f'" + dogamName + "' 도감등록증이 메일함으로 발송되었습니다."));
        player.sendMessage("");
    }

    private void handleReload(Player player) {
        if (!player.isOp()) {
            return;
        }
        DogamRegisterRepository.getInstance().loadData();
        int count = DogamRegisterRepository.getInstance().getLoadedCount();
        player.sendMessage(ColorManager.format("§a[도감등록증] §f도감 데이터를 다시 불러왔습니다. (§e" + count + "§f종)"));
    }

    private int countOwned(ItemStack[] storage, RequirementGroup group) {
        int total = 0;
        for (@Nullable ItemStack item : storage) {
            if (item == null) {
                continue;
            }
            if (matchesAny(item, group)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private void consumeFromInventory(ItemStack[] storage, RequirementGroup group, int amountToConsume) {
        int remaining = amountToConsume;
        for (int i = 0; i < storage.length && remaining > 0; i++) {
            ItemStack item = storage[i];
            if (item == null) {
                continue;
            }
            if (!matchesAny(item, group)) {
                continue;
            }
            int take = Math.min(item.getAmount(), remaining);
            int newAmount = item.getAmount() - take;
            remaining -= take;
            if (newAmount <= 0) {
                storage[i] = null;
            } else {
                item.setAmount(newAmount);
            }
        }
    }

    private boolean matchesAny(ItemStack item, RequirementGroup group) {
        String id = MMOItems.getID(item);
        if (id == null) {
            return false;
        }
        String type = MMOItems.getTypeName(item);
        for (MMOItemRef ref : group.getAlternatives()) {
            if (ref.getId() == null) {
                continue;
            }
            if (!matchesId(id, ref.getId(), group.isConsume())) {
                continue;
            }
            if (ref.getType() == null || type == null || ref.getType().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesId(String inventoryId, String configuredId, boolean consume) {
        if (consume) {
            return configuredId.equals(inventoryId);
        }
        String prefix = "합성무기_" + configuredId;
        if (!inventoryId.startsWith(prefix)) {
            return false;
        }
        String suffix = inventoryId.substring(prefix.length());
        if (suffix.isEmpty()) {
            return false;
        }
        for (int i = 0; i < suffix.length(); i++) {
            if (!Character.isDigit(suffix.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void sendMissingMessage(Player player, String dogamName, RequirementGroup group, int owned) {
        StringBuilder altNames = new StringBuilder();
        List<MMOItemRef> alternatives = group.getAlternatives();
        for (int i = 0; i < alternatives.size(); i++) {
            if (i > 0) {
                altNames.append("§f 또는 §e");
            }
            altNames.append(getDisplayName(alternatives.get(i), group.isConsume()));
        }
        player.sendMessage("");
        player.sendMessage(ColorManager.format("§c[도감등록증] §f'" + dogamName + "' 발급 조건을 충족하지 못했습니다."));
        player.sendMessage(ColorManager.format("§f  필요: §e" + altNames + " §f§7" + group.getAmount() + "개 §f(보유 §c" + owned + "개§f)"));
        player.sendMessage("");
    }

    private String getDisplayName(MMOItemRef ref, boolean consume) {
        String type = ref.getType();
        String id = ref.getId();
        if (id == null) {
            return "?";
        }
        if (type == null) {
            return id;
        }
        String lookupId = consume ? id : "합성무기_" + id + "0";
        ItemStack lookup = MMOItems.plugin.getItem(type, lookupId);
        if (lookup == null && !consume) {
            lookup = MMOItems.plugin.getItem(type, "합성무기_" + id);
        }
        if (lookup != null && lookup.hasItemMeta() && lookup.getItemMeta().hasDisplayName()) {
            return lookup.getItemMeta().getDisplayName();
        }
        return id;
    }

    private void sendUsageMessage(Player player) {
        player.sendMessage("");
        player.sendMessage("§7§o  /도감등록증 발급 <아이템이름> - 해당 도감등록증을 발급합니다.");
        if (player.isOp()) {
            player.sendMessage("§7§o  /도감등록증 리로드 - 도감 정의 데이터를 DB에서 다시 불러옵니다. (OP 전용)");
        }
        player.sendMessage("");
    }
}
