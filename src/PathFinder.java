import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {

    private static final AtomicInteger maxThreads = new AtomicInteger();
    private static final AtomicBoolean exitFound = new AtomicBoolean(false);
    private static double shortestDistanceSoFar = Double.MAX_VALUE;
    private static Runnable observer;
    private static Object exitFoundLock = new Object();

    @Override
    public void setMaxThreads(int i) {
        maxThreads.getAndSet(i);
    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface mi) {
        CorridorExplorer corridorExplorer = new CorridorExplorer();
        for (RoomInterface corridor : mi.corridors()) {
            corridorExplorer.explore(corridor);
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

    public void findExit() {
        exitFound.getAndSet(true);
    }

    public void bestPathFound() {
        observer.run();
    }

    @Override
    public synchronized double getShortestDistanceToExit() {
        return shortestDistanceSoFar;
    }

    private class CorridorExplorer {
        public void explore(RoomInterface room) {
           System.out.println(room.toString());
           if (room.isExit()) {
               synchronized (exitFoundLock) {
                   exitFound.set(true);
                   double distanceFromStart = room.getDistanceFromStart();
                   if (distanceFromStart < shortestDistanceSoFar) {
                       shortestDistanceSoFar = distanceFromStart;
                   }
               }
           }
           if (room.corridors() == null) {
               return;
           }
           for (RoomInterface corridor : room.corridors()) {
              explore(corridor);
           }
        }
    }
}
