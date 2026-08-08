package org.swlab.etcetera.Training.objects;

import com.binggre.binggreapi.objects.items.SerializedLocation;
import com.binggre.mongolibraryplugin.base.MongoData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Training.TrainingManager;

@Getter
public class TrainingRoom implements MongoData<Integer> {

    public static TrainingRoom findEmpty() {
        for (TrainingRoom value : TrainingManager.getInstance().getRoomRepository().values()) {
            if (!value.isActive) {
                return value;
            }
        }
        return null;
    }

    private final int id;
    private final SerializedLocation location;
    private final SerializedLocation entityLocation;

    @Setter
    private transient boolean isActive;
    private transient TrainingController controller;

    public TrainingRoom(RoomCreator creator) {
        id = creator.getRoomId();
        location = new SerializedLocation(creator.getLocation());
        entityLocation = new SerializedLocation(creator.getEntityLocation());
    }

    public void join(Player player) {
        isActive = true;
        controller = new TrainingController(this, player);
        controller.start();
    }

    public void quit() {
        isActive = false;
        controller.stop();
        controller = null;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
