import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private final AtomicInteger maxThreads = new AtomicInteger();
    private final AtomicInteger threadsUsed = new AtomicInteger();
    private final AtomicBoolean exitFound = new AtomicBoolean(false);
    private double shortestDistanceSoFar = Double.MAX_VALUE;
    private Optional<Runnable> observer = Optional.empty();

    public PathFinder() {
        threadsUsed.set(0);
        maxThreads.set(0);
    }

    @Override
    public void setMaxThreads(int i) {
        maxThreads.set(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface mi) {
        new CorridorExplorer(mi).explore();
        while (threadsUsed.get() > 0) {}
        observer.ifPresent(Runnable::run);
    }

    @Override
    public void registerObserver(Runnable code) {
        observer = Optional.of(code);
    }

    @Override
    public boolean exitFound() {
        return exitFound.get();
    }

    @Override
    public double getShortestDistanceToExit() {
        synchronized (exitFound) {
            return shortestDistanceSoFar;
        }
    }

    private class CorridorExplorer implements Runnable {

        private RoomInterface room;

        public CorridorExplorer(RoomInterface roomInterface) {
            this.room = roomInterface;
        }

        public void explore() {
            synchronized (exitFound) {
                if (room.isExit()) {
                    exitFound.set(true);
                    double distanceFromStart = room.getDistanceFromStart();
                    if (distanceFromStart < shortestDistanceSoFar) {
                        shortestDistanceSoFar = distanceFromStart;
                    }
                }
            }
            synchronized (exitFound) {
                if (room.corridors() == null ||
                                room.getDistanceFromStart() >= shortestDistanceSoFar ||
                                room.isExit()) {
                    return;
                }
            }
            for (RoomInterface corridor : room.corridors()) {
                this.room = corridor;
                boolean useNewThread = false;
                synchronized (threadsUsed) {
                    if (threadsUsed.get() < maxThreads.get()) {
                        useNewThread = true;
                        threadsUsed.set(threadsUsed.get() + 1);
                    }
                }

                if (useNewThread) {
                    new Thread(new CorridorExplorer(corridor)).start();
                } else {
                    explore();
                }
            }
        }

        @Override
        public void run() {
            explore();
            synchronized (threadsUsed) {
                threadsUsed.set(threadsUsed.get()-1);
            }
        }
    }
}
