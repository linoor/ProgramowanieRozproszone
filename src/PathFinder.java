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
        long startTime = System.nanoTime();
        new CorridorExplorer(mi).explore();
        System.out.println("Elapsed time " + (double) (System.nanoTime() - startTime) / 1000000000.0 + " seconds");
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
    public synchronized double getShortestDistanceToExit() {
        return shortestDistanceSoFar;
    }

    private class CorridorExplorer implements Runnable {

        private RoomInterface room;

        public CorridorExplorer(RoomInterface roomInterface) {
            this.room = roomInterface;
        }

        public void explore() {
           System.out.println(room.toString());
           if (room.isExit()) {
               synchronized (exitFoundLock) {
                   exitFound.set(true);
                   double distanceFromStart = room.getDistanceFromStart();
                   System.out.println(String.format(
                           "Found an exit at room %s. The distance to the entrance is %slonger than the shortest distance found so far",
                           room.toString(),
                           distanceFromStart < shortestDistanceSoFar ? "" : "NOT "
                   ));
                   if (distanceFromStart < shortestDistanceSoFar) {
                       System.out.println(
                               "Found new shortest distance at room "
                                       + room.toString()
                                       +", new distance: "
                                       + distanceFromStart
                       );
                       shortestDistanceSoFar = distanceFromStart;
                   }
               }
           }
           if (room.corridors() == null ||
                   room.getDistanceFromStart() >= shortestDistanceSoFar) {
               return;
           }
           for (RoomInterface corridor : room.corridors()) {
               this.room = corridor;
               run();
           }
        }

        @Override
        public void run() {
            explore();
        }
    }
}
