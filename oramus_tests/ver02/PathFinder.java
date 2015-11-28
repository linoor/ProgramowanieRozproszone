import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private static final AtomicInteger maxThreads = new AtomicInteger();
    private static final AtomicInteger threadsUsed = new AtomicInteger();
    private final AtomicBoolean exitFound = new AtomicBoolean(false);
    private double shortestDistanceSoFar = Double.MAX_VALUE;
    private Optional<Runnable> observer = Optional.empty();
    private Object exitFoundLock = new Object();
    private Object threadsUsedLock = new Object();

    public PathFinder() {
        threadsUsed.set(0);
        maxThreads.set(0);
    }

    @Override
    public void setMaxThreads(int i) {
        maxThreads.getAndSet(i);
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
        synchronized (exitFoundLock) {
            return shortestDistanceSoFar;
        }
    }

    private class CorridorExplorer implements Runnable {

        private RoomInterface room;

        public CorridorExplorer(RoomInterface roomInterface) {
            this.room = roomInterface;
        }

        public void explore() {
            synchronized (exitFoundLock) {
                if (room.isExit()) {
                    exitFound.set(true);
                    double distanceFromStart = room.getDistanceFromStart();
                    System.out.println("Found an exit at room " + room);
                    if (distanceFromStart < shortestDistanceSoFar) {
                        System.out.println(
                                "Found new shortest distance at room "
                                        + room.toString()
                                        + ", new distance: "
                                        + distanceFromStart
                        );
                        shortestDistanceSoFar = distanceFromStart;
                    }
                }
            }
            System.out.println(room.toString());
            if (room.corridors() == null ||
                    room.getDistanceFromStart() >= shortestDistanceSoFar ||
                    room.isExit()) {
                return;
            }
            for (RoomInterface corridor : room.corridors()) {
                this.room = corridor;
                boolean useNewThread = false;
                synchronized (threadsUsedLock) {
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
            System.out.println("USING A THREAD NUMBER OF THREADS " + threadsUsed.get());
            explore();
            threadsUsed.set(threadsUsed.get()-1);
        }
    }
}
