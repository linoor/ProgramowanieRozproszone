import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linoor on 10/24/15.
 */
public class Room implements RoomInterface {

    private final AtomicBoolean isExit = new AtomicBoolean(false);
    private List<RoomInterface> corridors = new ArrayList<>();

    @Override
    public boolean isExit() {
        return isExit.get();
    }

    @Override
    public double getDistanceFromStart() {
        return 0;
    }

    @Override
    public RoomInterface[] corridors() {
        return (RoomInterface[]) corridors.toArray();
    }

    public void addCorridor(RoomInterface newRoom) {
        corridors.add(newRoom);
    }
}
