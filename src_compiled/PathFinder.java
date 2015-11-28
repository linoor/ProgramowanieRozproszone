import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private final AtomicInteger maxThreads = new AtomicInteger(0);
    private final AtomicInteger threadsUsed = new AtomicInteger(1);

    private Runnable observer;

    private AtomicBoolean exitFound = new AtomicBoolean(false);
    private AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);

    private AtomicInteger threadsStarted = new AtomicInteger(1);
    private AtomicInteger threadsFinished = new AtomicInteger(0);

    private final Object exitLock = new Object();
    private final Object threadsNumLock = new Object();

    @Override
    public void setMaxThreads(int i) {
        maxThreads.set(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface entrance) {
        new Explorer(entrance).explore();
        threadsUsed.decrementAndGet();
        threadsFinished.incrementAndGet();
        while (true) {
            if (threadsFinished.get() == threadsStarted.get()) {
                observer.run();
                return;
            }
        }
    }

    @Override
    public void registerObserver(Runnable code) {
        observer = code;
    }

    @Override
    public boolean exitFound() {
        return exitFound.get();
    }

    @Override
    public double getShortestDistanceToExit() {
        synchronized (exitLock) {
            return shortestDistanceSoFar.get();
        }
    }

    private class Explorer implements Runnable {

        private RoomInterface room;

        public Explorer(RoomInterface room) {
            this.room = room;
        }

        public void explore() {
        }

        @Override
        public void run() {
            threadsStarted.incrementAndGet();
            explore();
            threadsUsed.decrementAndGet();
            threadsFinished.incrementAndGet();
        }
    }
}
