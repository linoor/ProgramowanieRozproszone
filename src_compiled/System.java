import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by linoor on 11/13/15.
 */
public class System implements SystemInterface {

    private QueueManager[] queuesManagers;

    private List<PriorityQueue<TaskInterface>> tasksWaiting;
    private List<PriorityQueue<TaskInterface>> tasksInProgress;
    private List<TaskInterface> tasksFinished;

    private Map<Integer, Integer> orderOfTasks = new HashMap<>();
    private int lastTaskId = -1;

    public void finish() {
        Arrays.stream(queuesManagers).forEach(QueueManager::awaitTermination);
    }

    @Override
    public void setNumberOfQueues(int queuesNum) {
        queuesManagers = new QueueManager[queuesNum];
        tasksWaiting = new ArrayList<>(queuesNum);
        tasksInProgress = new ArrayList<>(queuesNum);
        tasksFinished = new ArrayList<>(queuesNum);
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

    private int getPreviousTaskId(TaskInterface task) {
        return orderOfTasks.get(task.getTaskID());
    }

    private class QueueManager implements Runnable {
        private int queueNum;

        private ExecutorService queueExecutors = Executors.newSingleThreadExecutor();

        public QueueManager(int queueNum) {
            this.queueNum = queueNum;
        }

        public void setExecutors(int numOfThreads) {
            queueExecutors = Executors.newFixedThreadPool(numOfThreads);
        }

        public void awaitTermination() {
            try {
                queueExecutors.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void print(int taskNum, String message) {
            java.lang.System.out.println(
                    ColorPrint.getColoredString(queueNum, String.format("Queue %d ", queueNum))
                            + ColorPrint.getColoredString(4+taskNum, String.format("Task %d: ", taskNum))
                            + message);
        }

        @Override
        public void run() {
            while (true) {
                final TaskInterface[] taskToRun = {null};
                synchronized (tasksWaiting.get(queueNum)) {
                    synchronized (tasksInProgress.get(queueNum)) {
                        if (tasksWaiting.get(queueNum).peek() != null) {
                            taskToRun[0] = tasksWaiting.get(queueNum).remove();
                            tasksInProgress.get(queueNum).add(taskToRun[0]);
                        }
                    }
                }
                if (taskToRun[0] != null) {
                    queueExecutors.submit(() -> {

                        if (!checkThatYouCanRunTask(taskToRun[0])) return;

                        print(taskToRun[0].getTaskID(), "working!");
                        TaskInterface result = taskToRun[0].work(queueNum);
                        tasksInProgress.get(queueNum).remove(taskToRun[0]);
                        if (taskToRun[0].getLastQueue() != queueNum) {
                            print(taskToRun[0].getTaskID(), String.format("moved from %d to %d", queueNum, queueNum + 1));
                            tasksWaiting.get(queueNum + 1).add(result);
                        } else {
                            print(taskToRun[0].getTaskID(), "FINISHED!");
                            tasksFinished.add(taskToRun[0]);
                        }
                    });
                }
            }
        }

        private boolean checkThatYouCanRunTask(TaskInterface taskToRun) {
            if (queueNum == taskToRun.getLastQueue()) {
                int previousTaskId = getPreviousTaskId(taskToRun);
                boolean previousTaskFinished = tasksFinished
                        .stream()
                        .map(TaskInterface::getTaskID)
                        .anyMatch(id -> id.equals(previousTaskId));
                if (!(previousTaskId == -1 || previousTaskFinished)) {
                    tasksInProgress.get(queueNum).remove(taskToRun);
                    tasksWaiting.get(queueNum).add(taskToRun);
                    return false;
                }
            }
            return true;
        }
    }
}
