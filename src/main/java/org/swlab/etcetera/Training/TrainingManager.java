package org.swlab.etcetera.Training;

import com.binggre.binggreapi.command.ArgumentWrapper;
import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Training.commands.AdminCommand;
import org.swlab.etcetera.Training.commands.UserCommand;
import org.swlab.etcetera.Training.listeners.CombatListener;
import org.swlab.etcetera.Training.listeners.PlayerListener;
import org.swlab.etcetera.Training.objects.TrainingRoom;
import org.swlab.etcetera.Training.placeholder.TrainingPlaceholder;
import org.swlab.etcetera.Training.ranking.DamageRankingManager;
import org.swlab.etcetera.Training.repository.TrainingRoomRepository;

import java.util.HashMap;

@Getter
public final class TrainingManager {

    @Getter
    private static TrainingManager instance;

    private TrainingRoomRepository roomRepository;
    private DamageRankingManager rankingManager;

    private TrainingManager() {
    }

    /**
     * 훈련장은 로비 채널에서만 동작한다.
     * 그 외 채널에서는 명령어에 안내 메시지만 붙이고 저장소/리스너는 올리지 않는다.
     */
    public static void enable(EtCetera plugin) {
        if (!EtCetera.getChannelType().equals("lobby")) {
            registerLobbyOnlyNotice(plugin);
            return;
        }
        instance = new TrainingManager();
        instance.init(plugin);
    }

    public static void disable() {
        if (instance == null) {
            return;
        }
        instance.shutdown();
        instance = null;
    }

    private void init(EtCetera plugin) {
        roomRepository = new TrainingRoomRepository(plugin, "Training", "Room", new HashMap<>());
        roomRepository.init();

        rankingManager = new DamageRankingManager(plugin);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TrainingPlaceholder(this).register();
        }

        executeCommand(plugin, new UserCommand());
        executeCommand(plugin, new AdminCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CombatListener(), plugin);
    }

    private void shutdown() {
        roomRepository.values().stream().filter(TrainingRoom::isActive).forEach(TrainingRoom::quit);
        if (rankingManager != null) {
            rankingManager.shutdown();
        }
    }

    /**
     * BinggrePlugin#executeCommand 와 동일한 등록 절차.
     * 해당 메서드는 protected 라 BinggrePlugin 을 상속하지 않는 EtCetera 에서는 직접 호출할 수 없다.
     */
    private static void executeCommand(JavaPlugin plugin, BetterCommand command) {
        PluginCommand pluginCommand = plugin.getCommand(command.getCommand());
        pluginCommand.setExecutor(command);
        if (command.isSingleCommand()) {
            return;
        }
        for (CommandArgument argument : command.getArguments()) {
            ArgumentWrapper wrapper = new ArgumentWrapper(argument);
            command.getArgsMap().put(wrapper.getArg(), wrapper);
        }
    }

    private static void registerLobbyOnlyNotice(EtCetera plugin) {
        CommandExecutor notice = (sender, command, label, args) -> {
            sender.sendMessage("§c 로비에서만 이용할 수 있는 명령어입니다.");
            return true;
        };
        plugin.getCommand("훈련").setExecutor(notice);
        plugin.getCommand("훈련관리").setExecutor(notice);
    }
}
