import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private Runnable observer;
    private final AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);
    private ExecutorService executor;

    @Override
    public void setMaxThreads(int i) {
        executor = Executors.newFixedThreadPool(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface mi) {
    }

    @Override
    public void registerObserver(Runnable code) {
       observer = code;
    }

    @Override
    public boolean exitFound() {
        return false;
    }

    @Override
    public double getShortestDistanceToExit() {
        return shortestDistanceSoFar.get();
    }
}
