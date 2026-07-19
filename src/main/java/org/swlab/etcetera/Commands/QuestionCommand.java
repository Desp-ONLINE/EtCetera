package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastComponentVelocityListener;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import com.mongodb.client.MongoCollection;
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
import org.swlab.etcetera.EtCetera;

import java.util.Date;

public class QuestionCommand implements CommandExecutor {

    private static final long COOLDOWN_SECONDS = 30;

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
        String context = mergeContext(args);
        sendQuestionMessage(player, context);
        addCooldown(player);
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ColorManager.format("§f/질문 <할말> #FFF276 - 모두에게 질문 글을 작성할 수 있습니다."));
        player.sendMessage(ColorManager.format("§7§o할말에 < > 기호를 사용하면 해당 기호 안의 메시지의 색이 강조 표시 됩니다."));
    }

    private void sendQuestionMessage(Player player, String context) {
        String symbol = "🤔";
        String color = "#76C7FF";
        String title = "[질문이요!]";
        String highlightColor = EtCetera.getInstance().getConfig().getString("questionHighlightColor", color);
        String highlighted = highlightContext(stripColorCodes(context), highlightColor);
        String format = ColorManager.format("&f " + symbol + " " + color + " " + title + " " + player.getName() + "§f: " + highlighted);
        String divideLine = ColorManager.format(color + "§m                                                                                        §f");
        broadcastClickableQuestionMessage(divideLine, format, player.getName());
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

    private void broadcastClickableQuestionMessage(String divideLine, String format, String senderName) {
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
        for (String arg : args) {
            context.append(arg).append(" ");
        }
        return context.toString();
    }

    public long getCooldown(Player player) {
        MongoCollection<Document> questionCooldown = DatabaseRegister.getInstance().getMongoDatabase().getCollection("QuestionCooldown");
        Document document = questionCooldown.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) return 0;

        long nowTime = System.currentTimeMillis();
        long latestQuestionNotice = document.getDate("latestQuestionNotice").getTime();
        long elapsedSeconds = (nowTime - latestQuestionNotice) / 1000;

        long remaining = COOLDOWN_SECONDS - elapsedSeconds;
        return Math.max(remaining, 0); // 음수 방지
    }

    public void addCooldown(Player player) {
        MongoCollection<Document> questionCooldown = DatabaseRegister.getInstance().getMongoDatabase().getCollection("QuestionCooldown");
        Document document = questionCooldown.find(new Document("uuid", player.getUniqueId().toString())).first();
        if (document == null) {
            document = new Document("uuid", player.getUniqueId().toString());
            document.append("latestQuestionNotice", new Date());
            questionCooldown.insertOne(document);
        } else {
            document.replace("latestQuestionNotice", new Date());
            questionCooldown.replaceOne(new Document("uuid", player.getUniqueId().toString()), document);
        }
    }
}
