package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastComponentVelocityListener;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.Date;

public class TradeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sendUsage(player);
            return false;
        }
        if (getCooldown(player) > 0) {
            player.sendMessage("§c 아직 쿨타임이 " + getCooldown(player) + "초 남았습니다.");
            return false;
        }
        switch (args[0]) {
            case "구매":
            case "판매":
            case "구인":
                String context = mergeContext(args);
                sendTradeMessage(player, args[0], context);
                saveLatestTrade(player, args[0], context);
                addCooldown(player);
                return true;
            case "최신":
                Document latest = getLatestTrade(player);
                if (latest == null) {
                    player.sendMessage("§c 최근에 작성한 장사글이 없습니다.");
                    return false;
                }
                sendTradeMessage(player, latest.getString("type"), latest.getString("message"));
                addCooldown(player);
                return true;
            default:
                sendUsage(player);
                return false;
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(ColorManager.format("§f/장사글 [판매/구매/구인] <할말> #FFF276 - 아이템 판매/구매 또는 구인 글을 작성할 수 있습니다."));
        player.sendMessage(ColorManager.format("§f/장사글 최신 #FFF276 - 가장 최근에 작성한 장사글을 다시 보냅니다."));
        player.sendMessage(ColorManager.format("§7§o할말에 < > 기호를 사용하면 해당 기호 안의 메시지의 색이 강조 표시 됩니다."));
    }

    private void sendTradeMessage(Player player, String type, String context) {
        String symbol;
        String color;
        String title;
        switch (type) {
            case "구매":
                symbol = "Ϟ";
                color = "#98FF76";
                title = "[구매해요!]";
                break;
            case "판매":
                symbol = "ϙ";
                color = "#FFEC76";
                title = "[판매해요!]";
                break;
            case "구인":
                symbol = "ϗ";
                color = "#7D9A99";
                title = "[사람구해요!]";
                break;
            default:
                return;
        }
        String highlighted = highlightContext(stripColorCodes(context), color);
        String format = ColorManager.format("&f " + symbol + " " + color + " " + title + " " + player.getName() + "§f: " + highlighted);
        String divideLine = ColorManager.format(color + "§m                                                                                        §f");
        broadcastClickableTradeMessage(divideLine, format, player.getName());
    }

    private String highlightContext(String context, String color) {
        return context.replaceAll("<([^<>]*)>", color + "$1§f");
    }

    private String stripColorCodes(String context) {
        String previous;
        do {
            previous = context;
            context = context
                    .replaceAll("(?i)&?#[0-9A-F]{6}", "")
                    .replaceAll("(?i)[§&][0-9A-FK-ORX]", "")
                    .replace("§", "");
        } while (!context.equals(previous));
        return context;
    }

    private void broadcastClickableTradeMessage(String divideLine, String format, String senderName) {
        TextComponent messageComponent = new TextComponent(TextComponent.fromLegacyText(format));
        messageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + senderName + " "));
        messageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§e클릭하여 귓속말 보내기")));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(divideLine);
            online.spigot().sendMessage(messageComponent);
            online.sendMessage(divideLine);
        }

        String messageJson = ComponentSerializer.toString(messageComponent);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
        VelocityClient.getInstance().getConnectClient().send(BroadcastComponentVelocityListener.class, messageJson);
        VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
    }

    public String mergeContext(String[] args) {
        StringBuilder context = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            context.append(args[i]).append(" ");
        }
        return context.toString();
    }

    private Document getLatestTrade(Player player) {
        MongoCollection<Document> latestTrade = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TradeLatest");
        return latestTrade.find(new Document("uuid", player.getUniqueId().toString())).first();
    }

    private void saveLatestTrade(Player player, String type, String message) {
        MongoCollection<Document> latestTrade = DatabaseRegister.getInstance().getMongoDatabase().getCollection("TradeLatest");
        Document document = new Document("uuid", player.getUniqueId().toString())
                .append("type", type)
                .append("message", message);
        latestTrade.replaceOne(new Document("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public long getCooldown(Player player) {
        DatabaseRegister databaseRegister = DatabaseRegister.getInstance();
        MongoCollection<Document> tradeCooldown = databaseRegister.getMongoDatabase().getCollection("TradeCooldown");
        Document document = tradeCooldown.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) return 0;

        long nowTime = System.currentTimeMillis();
        long latestTradeNotice = document.getDate("latestTradeNotice").getTime();
        long elapsedSeconds = (nowTime - latestTradeNotice) / 1000;

        long remaining = 60 - elapsedSeconds;
        return Math.max(remaining, 0); // 음수 방지
    }


    public void addCooldown(Player player) {
        DatabaseRegister databaseRegister = DatabaseRegister.getInstance();
        MongoCollection<Document> tradeCooldown = databaseRegister.getMongoDatabase().getCollection("TradeCooldown");
        Document document = tradeCooldown.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = new Document("uuid", player.getUniqueId().toString());
            document.append("latestTradeNotice", new Date());
            tradeCooldown.insertOne(document);
        } else {
            document.replace("latestTradeNotice", new Date());
            databaseRegister.getMongoDatabase().getCollection("TradeCooldown").replaceOne(new Document("uuid", player.getUniqueId().toString()), document);
        }
    }
}
