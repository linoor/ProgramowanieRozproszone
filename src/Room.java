import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linoor on 10/24/15.
 */
public class Room implements RoomInterface {

    private final AtomicBoolean isExit = new AtomicBoolean(false);
    private Map<RoomInterface, Integer> corridorsAndLenghts = new HashMap<>();
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
        Set<RoomInterface> corridors = corridorsAndLenghts.keySet();
        if (corridors.size() == 0) {
            return null;
        }

        return (RoomInterface[]) corridors.toArray();
    }

    public void addCorridor(RoomInterface newRoom) {
        corridorsAndLenghts.put(newRoom, 1);
    }

    public void setDistance(Room room, int distance) throws Exception {
        if (!corridorsAndLenghts.containsKey(room)) {
            throw new Exception("Can't set the distance, the key does not exist.");
        }

        corridorsAndLenghts.put(room, distance);
    }
}
