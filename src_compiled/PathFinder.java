import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private Runnable observer;
    private final AtomicReference<Double> shortestDistanceSoFar = new AtomicReference<>(Double.MAX_VALUE);
    private final AtomicBoolean exitFound = new AtomicBoolean(false);
    private ExecutorService executor;

    @Override
    public void setMaxThreads(int i) {
        executor = Executors.newFixedThreadPool(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface entrance) {
        executor.submit(new CorridorExplorer(entrance));
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        observer.run();
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

    private class CorridorExplorer implements Runnable {

        private RoomInterface room;

        public CorridorExplorer(RoomInterface room) {
           this.room = room;
        }

        @Override
        public void run() {
            boolean exit = false;
            synchronized (exitFound) {
                if (room.isExit()) {
                    exit = true;
                    exitFound.set(true);
                    double dist = room.getDistanceFromStart();
                    if (dist < getShortestDistanceToExit()) {
                        shortestDistanceSoFar.set(dist);
                    }
                }
            }

            synchronized (exitFound) {
                if (room.getDistanceFromStart() >= getShortestDistanceToExit() || exit) {
                    return;
                }
            }

            for (RoomInterface newRoom : room.corridors()) {
               executor.submit(new CorridorExplorer(newRoom));
            }
        }
    }
}
