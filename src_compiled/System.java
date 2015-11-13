import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by linoor on 11/13/15.
 */
public class System implements SystemInterface {

    private QueueManager[] queues;
    private int[]  threadLimits;
    private Map<Integer, Integer> orderOfTasks = new LinkedHashMap<>();

    @Override
    public void setNumberOfQueues(int queuesNum) {
        for (int i = 0; i < queuesNum; i++) {
            queues[i] = new QueueManager();
            threadLimits[i] = 0;
        }
    }

    @Override
    public void setThreadsLimit(int[] maximumThreadsPerQueue) {
        threadLimits = maximumThreadsPerQueue;
    }

    @Override
    public void addTask(TaskInterface task) {
    }

    private class QueueManager implements Runnable {

        @Override
        public void run() {

        }
    }
}
