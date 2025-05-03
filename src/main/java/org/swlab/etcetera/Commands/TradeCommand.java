package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import com.binggre.velocitysocketclient.listener.BroadcastStringVelocityListener;
import com.mongodb.client.MongoCollection;
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
            String format = ColorManager.format("§f/장사글 [판매/구매/구인] <할말> #FFF276 - 아이템 판매/구매 또는 구인 글을 작성할 수 있습니다.");
            player.sendMessage(format);
            return false;
        }
        if (getCooldown(player) > 0) {
            player.sendMessage("§c 아직 쿨타임이 " + getCooldown(player) + "초 남았습니다.");
            return false;
        }
        String divideLine = "";
        String format = "";
        switch (args[0]) {
            case "구매":
                format = ColorManager.format("&f Ϟ #98FF76 [구매해요!] " + sender.getName() + "§f: " + mergeContext(args));
                divideLine = ColorManager.format("#98FF76§m                                                                                        §f");
                Bukkit.broadcastMessage(divideLine);
                Bukkit.broadcastMessage(format);
                Bukkit.broadcastMessage(divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, format);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                addCooldown(player);
                return true;
            case "판매":
                format = ColorManager.format("&f ϙ #FFEC76 [판매해요!] " + sender.getName() + "§f: " + mergeContext(args));
                divideLine = ColorManager.format("#FFEC76§m                                                                                        §f");
                Bukkit.broadcastMessage(divideLine);
                Bukkit.broadcastMessage(format);
                Bukkit.broadcastMessage(divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, format);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                addCooldown(player);
                return true;
            case "구인":
                format = ColorManager.format("&f ϗ #7D9A99 [사람구해요!] " + sender.getName() + "§f: " + mergeContext(args));
                divideLine = ColorManager.format("#7D9A99§m                                                                                        §f");
                Bukkit.broadcastMessage(divideLine);
                Bukkit.broadcastMessage(format);
                Bukkit.broadcastMessage(divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, format);
                VelocityClient.getInstance().getConnectClient().send(BroadcastStringVelocityListener.class, divideLine);
                addCooldown(player);
                return true;
            default:
                String commandnotice = ColorManager.format("§f/장사글 [판매/구매/구인] <할말> #FFF276 - 아이템 판매/구매 또는 구인 글을 작성할 수 있습니다.");
                player.sendMessage(commandnotice);
                return false;
        }
    }

    public String mergeContext(String[] args) {
        String context = "";
        for (String arg : args) {
            if (arg.equals("구매") || arg.equals("판매") || arg.equals("구인")) {
                continue;
            }
            context += arg + " ";
        }
        return context;
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
