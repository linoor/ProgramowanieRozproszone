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
    private AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);

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
           if (room.isExit()) {
               exitFound.set(true);
           }

            if (room.isExit()) {
                double dist = room.getDistanceFromStart();
                if (dist < shortestDistanceSoFar.get()) {
                    shortestDistanceSoFar.set(dist);
                }
            }

            if (room.getDistanceFromStart() >= shortestDistanceSoFar.get()) {
                return;
            }

            if (room.isExit()) {
                return;
            }

            if (room.corridors() == null) {
                return;
            }

            for (RoomInterface roomToExplore : room.corridors()) {
                    if (threadsUsed.get() < maxThreads) {
                        threadsUsed.incrementAndGet();
                        new Thread(new Explorer(roomToExplore)).start();
                    } else {
                        new Explorer(roomToExplore).explore();
                    }
            }
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
