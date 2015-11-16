import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by linoor on 11/13/15.
 */
public class System implements SystemInterface {

    private QueueManager[] queuesManagers;

    private List<PriorityQueue<TaskInterface>> tasksWaiting;
    private List<PriorityQueue<TaskInterface>> tasksInProgress;

    private Map<Integer, Integer> orderOfTasks = new HashMap<>();
    private int lastTaskId = -1;

    public void finish() {
        Arrays.stream(queuesManagers).forEach(QueueManager::shutdown);
    }

    @Override
    public void setNumberOfQueues(int queuesNum) {
        queuesManagers = new QueueManager[queuesNum];
        tasksWaiting = new ArrayList<>(queuesNum);
        tasksInProgress = new ArrayList<>(queuesNum);
        for (int i = 0; i < queuesNum; i++) {
            queuesManagers[i] = new QueueManager(i);
            tasksWaiting.add(i, new PriorityQueue<>(new TaskQueueComparator()));
            tasksInProgress.add(i, new PriorityQueue<>(new TaskQueueComparator()));

            new Thread(queuesManagers[i]).start();
        }
    }

    @Override
    public void setThreadsLimit(int[] maximumThreadsPerQueue) {
        for (int i = 0; i < maximumThreadsPerQueue.length; i++) {
            queuesManagers[i].setExecutors(maximumThreadsPerQueue[i]);
        }
        Arrays.stream(queuesManagers).forEach(q -> java.lang.System.out.println(q.queueExecutors));
    }

    @Override
    public void addTask(TaskInterface task) {
        if (task.keepOrder()) {
            addTaskOrder(task);
        }
        tasksWaiting.get(task.getFirstQueue()).add(task);
    }

    private void addTaskOrder(TaskInterface task) {
        orderOfTasks.put(task.getTaskID(), lastTaskId);
        lastTaskId = task.getTaskID();
    }

    private class QueueManager implements Runnable {

        private int queueNum;

        private TaskInterface taskToRun;

        private ExecutorService queueExecutors = Executors.newSingleThreadExecutor();

        public QueueManager(int queueNum) {
            this.queueNum = queueNum;
        }

        public void setExecutors(int numOfThreads) {
            queueExecutors = Executors.newFixedThreadPool(numOfThreads);
        }

        public void shutdown() {
           queueExecutors.shutdown();
        }

        @Override
        public void run() {
            java.lang.System.out.println("Starting a new QueueManager");
            while (true) {
                synchronized (tasksWaiting.get(queueNum)) {
                    synchronized (tasksInProgress.get(queueNum)) {
                        if (tasksWaiting.get(queueNum).peek() != null) {
                            taskToRun = tasksWaiting.get(queueNum).remove();
                            tasksInProgress.get(queueNum).add(taskToRun);
                            java.lang.System.out.println(String.format("Task %d in progress!", taskToRun.getTaskID()));
                        }
                    }
                }
                if (taskToRun != null) {
                    queueExecutors.submit(() -> {
                        java.lang.System.out.println(String.format("Task %d working!", taskToRun.getTaskID()));
                        taskToRun.work(queueNum);
                        tasksInProgress.get(queueNum).remove(taskToRun);
                        if (taskToRun.getLastQueue() != queueNum) {
                            tasksWaiting.get(queueNum + 1).add(taskToRun);
                        } else {
                            java.lang.System.out.println(String.format("Task %d finished!", taskToRun.getTaskID()));
                        }
                    });
                }
            }
        }
    }
}
