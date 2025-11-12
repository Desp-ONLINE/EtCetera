package org.swlab.etcetera.Commands;

import com.binggre.binggreapi.utils.serializers.ItemStackSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class AdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        if(!player.isOp()){
            return false;
        }
        String d = "H4sIAAAAAAAA/9VX3WvbVhS/ibPU9b4LozC2IdQ1gyYrtpMmrVeyaZbamPijteyx4BlxY107Wmwrk67XmhAoJQtZG0pghIWRti60uGVPa1Kawp68lz1uj3vYoK+R/D/sXtuR7WS214KXThby1dU553d+55x7OdcBgB30KjI4mlGyKKHBJPZcUlUZZSV9Rv0apR3AhmHKDt4KBEK+iBAQJX4qyAV8XskfCgs9J2JsqZhkh+iT3Azze6FUTDBGYdl4eJ8xCxvltQ0PQ764nM6q1K7sMEOnn95YJn9jO5uPdh4+MAr3KrKDruHjzcJjHzJEpPz9snlznTFWrhDDQ4z5ZMu4WjDubJhri3TeXFolo53HV8ybW8b1uyebTRDfnt64zZiL2+Vr2+Uf7u9s/kzBLpeKo5XbXSqOlIqnS0W+ilSDoYbLt9YtRxhyxdxxxny0QQQYo7hsXF1nBl2n9klYnJhBt/M4G+9viKE46fP7JZ4LcOeFT06AymUHb1jf6VPy8eBdEkjK5rsN89Y948mi1MjZawP9PMzAFKqqv2qpkwQJ4Muzx8586nK6nOMxY3PLvHaXqdqIM2ePDY/Q33h5ZYWMT7nPjXDjxoPtyvjMqHOckq6/LK1aQgTVUiaBZgbGBtSYN24Db1vY3qgYCQWkQIgXKMMIB8AH39jBm5aAEPROcMGICHpj8cb5CmdRiIBDk1yQ84c4O3BMiJHwVJVOYZ4VMcSsh6WvJNqh8yJWtTzric2zTcylX37SsUYE/7sAsAvxhcPgCIexpkznMAqospJUkKY7SGZ67KAvCDMIvJbJqApGGd0jo4Sat4HDoTmkQayoWZrBl0FfNEqSDkDfF698/Gs+89Fv1/PvS5Pv/CmS5Fq2K6beqy/XFMoiTUmchBjDxKykzyEk94N+LqPmshjsXjZw1ApSWLgY9YUFXvILnwl+8lG2gyPNeYhMXRCAI8z5eOkCF46IxNcJRUbn0jClE4VeG3jdm9OxmiFMUZqHGNI0O8AhWdHn0jBfo2y7Pc9idJlmjWSMDDRYyVdtbm9eiIw6nczpCYiRzHqSMK2jIVbBMK0krNdcVkZamtCvS5B0K7MIz2hqLjVjzSbUtErLoFYFxPi0mt5VWhiyvCBF0CXgaqG0BCYV1y3gSrW2BKbl3T1ksmZaIy+tHhDw2uLBxJruEgdTXnRfqqNgLdegm9Jgvrpp9flVDdnJmnaAb9su1u6QuDRDdsQ9FOIL4Me2rtA2g92H3WyyIRB725HWqhrxu0Gx2rS0hyHOvkBx+6ND3FrTkaE2K8GvcrAxALQ9e4ZAN7RxrbWqpVdXqnV7/6co621dec5etW24Xiz+f3Wosv2N9rMV0T+1393YQEdH3SOn+f38Pu/Ar/URoFMSo//K8p6jQ2ezjc0/PZcBT4wdeL5DUxz0gJe8tHfsAX8DuCR+SB4OAAA=";
        ItemStack deserialize = ItemStackSerializer.deserialize(d);
        player.sendMessage(deserialize.toString());
        return true;
    }
}