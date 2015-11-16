import java.util.HashMap;
import java.util.Map;

/**
 * Created by linoor on 11/13/15.
 */
public class System implements SystemInterface {

    private QueueManager[] queues;
    private int[]  threadLimits;
    private int[] threadUses;
    private Map<Integer, Integer> orderOfTasks = new HashMap<>();

    private int lastTaskId = -1;

    @Override
    public void setNumberOfQueues(int queuesNum) {
        for (int i = 0; i < queuesNum; i++) {
            queues[i] = new QueueManager(i);
            threadLimits[i] = 0;
            threadUses[i] = 0;
        }
    }

    @Override
    public void setThreadsLimit(int[] maximumThreadsPerQueue) {
        threadLimits = maximumThreadsPerQueue;
    }

    @Override
    public void addTask(TaskInterface task) {
        if (task.keepOrder()) {
            orderOfTasks.put(task.getTaskID(), lastTaskId);
            lastTaskId = task.getTaskID();
        }
    }

    private class QueueManager implements Runnable {

        private int queueNum;

        public QueueManager(int queueNum) {
            this.queueNum = queueNum;
        }

        @Override
        public void run() {
            if (threadUses[queueNum] < threadLimits[queueNum]) {
                // get appropriate task (prorieties for queues that are ordered
                // if it's the tasks' last work then check if the task before this one has finished
                // run the task if all is fine
            }
        }
    }
}
