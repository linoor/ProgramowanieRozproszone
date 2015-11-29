import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private int maxThreads = 0;
    private final AtomicInteger threadsUsed = new AtomicInteger(1);

    private Runnable observer;

    private AtomicBoolean exitFound = new AtomicBoolean(false);
    private final AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);

    private AtomicInteger threadsStarted = new AtomicInteger(1);
    private AtomicInteger threadsFinished = new AtomicInteger(0);

    private final Object exitLock = new Object();
    private final Object threadsNumLock = new Object();

    @Override
    public void setMaxThreads(int i) {
        if (maxThreads == 0) {
            maxThreads = i;
        }
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface entrance) {
        new Explorer(entrance).explore();
        threadsUsed.decrementAndGet();
        threadsFinished.incrementAndGet();
        while (true) {
            if (threadsFinished.get() == threadsStarted.get() && threadsUsed.get() == 0) {
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
        synchronized (exitFound) {
            return exitFound.get();
        }
    }

    @Override
    public double getShortestDistanceToExit() {
        synchronized (shortestDistanceSoFar) {
            return shortestDistanceSoFar.get();
        }
    }

    private class Explorer implements Runnable {

        private RoomInterface room;

        public Explorer(RoomInterface room) {
            this.room = room;
        }

        public void explore() {
            if (!exitFound.get()) {
                synchronized (exitFound) {
                    if (room.isExit()) exitFound.set(true);
                }
            }

            if (room.isExit()) {
                synchronized (shortestDistanceSoFar) {
                    double dist = room.getDistanceFromStart();
                    if (dist < getShortestDistanceToExit()) {
                        shortestDistanceSoFar.set(dist);
                    }
                }
                return;
            }

            if (room.getDistanceFromStart() >= getShortestDistanceToExit()) {
                return;
            }

            if (room.corridors() == null) {
                return;
            }

            Arrays.stream(room.corridors()).forEach(roomToExplore -> {
                if (threadsUsed.incrementAndGet() > maxThreads) {
                    threadsUsed.decrementAndGet();
                    new Explorer(roomToExplore).explore();
                } else {
                    new Thread(new Explorer(roomToExplore)).start();
                }
            });
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
