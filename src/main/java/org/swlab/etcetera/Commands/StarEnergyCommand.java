package org.swlab.etcetera.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.Convinience.StarEnergyGui;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.StarEnergyRepository;


/**
 * /별의기운                          별의 기운 GUI (초월석 교환 / 레이드별 획득량)
 * /별의기운 지급|차감|설정 <닉네임> <수치>    (관리자) 오프라인 대상 가능
 * /별의기운 확인 <닉네임>                  (관리자)
 */
public class StarEnergyCommand implements CommandExecutor {

    private static final String PREFIX = "§b    [ 별의 기운 ]§f ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        StarEnergyRepository repository = StarEnergyRepository.getInstance();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                return false;
            }
            StarEnergyGui.openMain(player);
            return true;
        }

        if (!sender.hasPermission("*")) {
            sender.sendMessage("§c 권한이 없습니다.");
            return false;
        }

        switch (args[0]) {
            case "지급", "차감", "설정" -> {
                if (args.length < 3) {
                    sender.sendMessage("§c /별의기운 " + args[0] + " <닉네임> <수치>");
                    return false;
                }
                Integer amount = parseInt(sender, args[2]);
                if (amount == null || amount < 0) {
                    return false;
                }
                runForTarget(sender, args[1], (uuid, nickname) -> {
                    switch (args[0]) {
                        case "지급" -> {
                            int added = repository.addAmount(uuid, nickname, amount);
                            return nickname + "§f에게 §b" + added + "§f 지급" + (added < amount ? " §7(최대 보유량 초과로 " + (amount - added) + " 소멸)" : "");
                        }
                        case "차감" -> {
                            if (!repository.consumeAmount(uuid, nickname, amount)) {
                                return nickname + "§c 보유량이 부족합니다.";
                            }
                            return nickname + "§f에게서 §b" + amount + "§f 차감";
                        }
                        default -> {
                            repository.setAmount(uuid, nickname, amount);
                            return nickname + "§f 보유량 설정";
                        }
                    }
                });
            }
            case "확인" -> {
                if (args.length < 2) {
                    sender.sendMessage("§c /별의기운 확인 <닉네임>");
                    return false;
                }
                runForTarget(sender, args[1], (uuid, nickname) -> nickname + "§f 보유량 조회");
            }
            default -> sender.sendMessage("§c 지급 | 차감 | 설정 | 확인");
        }
        return true;
    }

    private interface TargetAction {
        String run(String uuid, String nickname);
    }

    // DB 접근은 비동기로 처리하고, 결과는 메인 스레드에서 출력한다.
    private void runForTarget(CommandSender sender, String targetName, TargetAction action) {
        Player online = Bukkit.getPlayerExact(targetName);
        String onlineUuid = online == null ? null : online.getUniqueId().toString();
        String onlineName = online == null ? null : online.getName();

        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> {
            StarEnergyRepository repository = StarEnergyRepository.getInstance();
            String uuid = onlineUuid != null ? onlineUuid : repository.findUuidByNickname(targetName);
            String nickname = onlineName != null ? onlineName : targetName;
            String message;
            if (uuid == null) {
                message = "§c 별의 기운 기록이 없는 유저입니다(오프라인): " + targetName;
            } else {
                message = PREFIX + "§6" + action.run(uuid, nickname) + " §7(현재 " + repository.getAmount(uuid) + "/" + StarEnergyRepository.MAX_AMOUNT + ")";
            }
            Bukkit.getScheduler().runTask(EtCetera.getInstance(), () -> sender.sendMessage(message));
        });
    }

    private Integer parseInt(CommandSender sender, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c 숫자를 입력해주세요: " + value);
            return null;
        }
    }
}
