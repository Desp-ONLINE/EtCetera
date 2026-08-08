package org.swlab.etcetera.Training.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Training.TrainingManager;
import org.swlab.etcetera.Training.repository.TrainingRoomRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RoomCreator {

    public static final Map<UUID, RoomCreator> creators = new HashMap<>();

    private final UUID uuid;
    private final int roomId;

    @Setter
    private Location location;
    @Setter
    private Location entityLocation;

    public RoomCreator(Player player, int roomId) {
        this.uuid = player.getUniqueId();
        this.roomId = roomId;
    }

    public boolean isRegistered() {
        return creators.containsKey(uuid);
    }

    public void register() {
        creators.put(uuid, this);
    }

    public void unregister() {
        creators.remove(uuid);
    }

    public void create() {
        TrainingRoomRepository repository = TrainingManager.getInstance().getRoomRepository();
        TrainingRoom trainingRoom = new TrainingRoom(this);

        repository.putIn(trainingRoom);
        repository.save(trainingRoom);
    }

    public static RoomCreator get(Player player, int id) {
        return creators.getOrDefault(player.getUniqueId(), new RoomCreator(player, id));
    }
}
