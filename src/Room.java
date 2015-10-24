import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linoor on 10/24/15.
 */
public class Room implements RoomInterface {

    private final AtomicBoolean isExit = new AtomicBoolean(false);
    private List<RoomInterface> corridors = new ArrayList<>();
    private double distanceFromStart = 0.0;

    @Override
    public boolean isExit() {
        return isExit.get();
    }

    @Override
    public double getDistanceFromStart() {
        synchronized (this) {
            return distanceFromStart;
        }
    }

    @Override
    public RoomInterface[] corridors() {
        return (RoomInterface[]) corridors.toArray();
    }

    public void addCorridor(RoomInterface newRoom) {
        corridors.add(newRoom);
    }
}
