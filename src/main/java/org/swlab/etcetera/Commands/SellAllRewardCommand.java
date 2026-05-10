package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.EconomyManager;
import com.binggre.binggreapi.utils.NumberUtil;
import net.Indyuce.mmoitems.MMOItems;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.IDEPass.api.IDEPassAPI;
import org.desp.IDEPass.dto.IDEPassUserDataDto;
import org.dople.dataSync.inventory.InventorySyncListener;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;

public class SellAllRewardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;

        if (InventorySyncListener.isDataLoading(player)) {
            player.sendMessage("§c데이터가 로드중입니다.");
            return false;
        }

        IDEPassUserDataDto idePass = IDEPassAPI.getPlayer(player.getUniqueId().toString());

        if (!EtCetera.getChannelType().equals("lobby") && !EtCetera.getChannelType().equals("dungeon")) {
            player.sendMessage("§c 로비나 던전에서만 이용하실 수 있는 명령어 입니다.");
            return false;
        }

        if (!idePass.isActivate()) {
            player.sendMessage("§c 이데 패스가 활성화 되어 있어야 사용하실 수 있습니다.");
            return false;
        }

        ItemStack[] storageContents = player.getInventory().getStorageContents();
        double sellPrice = 0.0;

        player.sendMessage("");
        for (ItemStack itemStack : storageContents) {
            String id = MMOItems.getID(itemStack);
            if (!id.contains("전리품")) {
                continue;
            }
            double price = ShopGuiPlusApi.getItemStackPriceSell(itemStack);
            sellPrice += price;
            player.sendMessage(
                    ColorManager.format("#FBFFBC 판매 대상 아이템: " + itemStack.getItemMeta().getDisplayName())
                            + "§f x" + itemStack.getAmount()
                            + " §7§o( " + price + " 골드 )"
            );
            itemStack.setAmount(0);
        }

        EconomyManager.addMoney(player, sellPrice);

        player.sendMessage("");
        player.sendMessage(ColorManager.format(
                "#FFF285  [ 전리품 일괄 판매 ] §f인벤토리의 전리품을 모두 판매하여 §6"
                        + NumberUtil.applyComma(sellPrice)
                        + " §f만큼의 골드를 획득하였습니다."
        ));
        player.sendMessage("");

        System.out.println(player.getName() + " 님께서 전리품 일괄 판매: " + sellPrice);
        return false;
    }
}
