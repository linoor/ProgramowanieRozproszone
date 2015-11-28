import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private final AtomicInteger maxThreads = new AtomicInteger(0);
    private final AtomicInteger threadsUsed = new AtomicInteger(0);

    private Runnable observer;

    private AtomicBoolean exitFound = new AtomicBoolean(false);
    private AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);

    private final Object lock = new Object();

    @Override
    public void setMaxThreads(int i) {
        maxThreads.set(i);
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
        return exitFound.get();
    }

    @Override
    public double getShortestDistanceToExit() {
        return shortestDistanceSoFar.get();
    }

    private class Explorer implements Runnable {

        private RoomInterface room;

        public Explorer(RoomInterface room) {
            this.room = room;
        }

        public void explore() {
            synchronized (lock) {
                if (room.isExit()) {
                    exitFound.set(true);
                    double dist = room.getDistanceFromStart();
                    if (dist < shortestDistanceSoFar.get()) {
                        shortestDistanceSoFar.set(dist);
                    }
                }
            }

            synchronized (lock) {
                if (room.isExit() || room.corridors() == null || room.getDistanceFromStart() > shortestDistanceSoFar.get()) {
                    return;
                }
            }

            synchronized (threadsUsed) {
                Arrays.stream(room.corridors()).forEach(roomToExplore -> {
                    Explorer newRoomExplorer = new Explorer(roomToExplore);
                    // if we have enough threads then we explore in a new thread - if not then we explore in the same thread
                    if (threadsUsed.get() < maxThreads.get()) {
                        threadsUsed.incrementAndGet();
                        new Thread(newRoomExplorer).start();
                    } else {
                        newRoomExplorer.explore();
                    }
                });
            }
        }

        @Override
        public void run() {
            explore();
        }
    }
}
