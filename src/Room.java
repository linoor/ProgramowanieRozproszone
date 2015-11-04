import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linoor on 10/24/15.
 */
public class Room implements RoomInterface {

    private final AtomicBoolean isExit = new AtomicBoolean(false);
    private Map<RoomInterface, Integer> corridorsAndLengths = new HashMap<>();
    private double distanceFromStart = 0.0;

    private String name = "";

    public Room(String name, double distanceFromStart) {
        this.name = name;
        this.distanceFromStart = distanceFromStart;
    }

    public void setExit() {
        isExit.set(true);
    }

    @Override
    public boolean isExit() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        Set<RoomInterface> corridors = corridorsAndLengths.keySet();
        if (corridors.size() == 0) {
            return null;
        }

        RoomInterface[] result = corridors.toArray(new RoomInterface[corridors.size()]);
        return result;
    }

    public void addCorridor(RoomInterface newRoom) {
        corridorsAndLengths.put(newRoom, 1);
    }

    public void setDistance(Room room, int distance) throws Exception {
        if (!corridorsAndLengths.containsKey(room)) {
            throw new Exception("Can't set the distance, the key does not exist.");
        }

        corridorsAndLengths.put(room, distance);
    }

    @Override
    public String toString() {
        return name;
    }
}
