import java.util.DoubleSummaryStatistics;

/**
 * Created by linoor on 10/23/15.
 */
class PathFinder implements PathFinderInterface {
    @Override
    public void setMaxThreads(int i) {

    }

    @Override
    public void entranceToTheLabyrinth(RoomInterface mi) {

    }

    @Override
    public void registerObserver(Runnable code) {

    }

    @Override
    public boolean exitFound() {
        return false;
    }

    @Override
    public double getShortestDistanceToExit() {
        if (!exitFound()) {
            return Double.MAX_VALUE;
        }
        return 0;
    }
}
