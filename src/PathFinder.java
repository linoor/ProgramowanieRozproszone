import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private final static AtomicInteger maxThreads = new AtomicInteger();
    private static final AtomicBoolean exitFound = new AtomicBoolean(false);

    @Override
    public void setMaxThreads(int i) {
        maxThreads.getAndSet(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface mi) {

    }

    @Override
    public void registerObserver(Runnable code) {

    }

    @Override
    public boolean exitFound() {
        return exitFound.get();
    }

    public void findExit() {
        exitFound.getAndSet(true);
    }

    @Override
    public double getShortestDistanceToExit() {
        if (!exitFound()) {
            return Double.MAX_VALUE;
        }
        return 0;
    }
}
