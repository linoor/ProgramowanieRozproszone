import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by linoor on 11/13/15.
 */
class SystemExec implements SystemInterface {

    private final List<QueueManager> queueManagers         = new ArrayList<>();
    private final List<Queue<TaskInterface>> waitingQueues = new ArrayList<>();
    private final Map<Integer, Integer> taskOrder = new HashMap<>();
    private final List<Integer> tasksFinished        = new ArrayList<>();

    private int lastTask = -1;

    @Override
    public void setNumberOfQueues(int queues) {
        for (int i = 0; i < queues; i++) {
            waitingQueues.add(new PriorityQueue<>(new TaskQueueComparator()));
            queueManagers.add(new QueueManager(i));
        }
        queueManagers.stream().forEach(manager -> new Thread(manager).start());
        assert queueManagers.size() == waitingQueues.size();
    }

    @Override
    public void setThreadsLimit(int[] maximumThreadsPerQueue) {
        assert maximumThreadsPerQueue.length == queueManagers.size();
        for (int i = 0; i < maximumThreadsPerQueue.length; i++) {
            queueManagers.get(i).setThreadLimit(maximumThreadsPerQueue[i]);
        }
    }

    @Override
    public void addTask(TaskInterface task) {
        synchronized (waitingQueues.get(task.getFirstQueue())) {
            setupOrder(task);
            waitingQueues.get(task.getFirstQueue()).add(task);
        }
    }

    private void setupOrder(TaskInterface task) {
        taskOrder.put(task.getTaskID(), lastTask);
        lastTask = task.getTaskID();
    }

    private class QueueManager implements Runnable {

        private ExecutorService executor = Executors.newFixedThreadPool(1);
        private int queueNum;

        public QueueManager(int queueNum) {
            this.queueNum = queueNum;
        }

        public void setThreadLimit(int limit) {
            executor = Executors.newFixedThreadPool(limit);
        }

        @Override
        public void run() {
            while (true) {
                synchronized (waitingQueues.get(queueNum)) {
                    TaskInterface task = waitingQueues.get(queueNum).poll();
                    if (task == null ) {
                        continue;
                    }
                    executor.submit(() -> {
                        // if previous task not yet finished, add the task back to the queue
                        synchronized (waitingQueues.get(queueNum)) {
                            if (task.getLastQueue() == queueNum && !previousTaskFinished(task)) {
                                waitingQueues.get(queueNum).add(task);
                                return;
                            }
                        }

                        TaskInterface returnedTask = workDatTask(task);
                        if (returnedTask.getLastQueue() == queueNum) {
                            finishIt(returnedTask);
                        } else {
                            putItInTheNextQueue(returnedTask);
                        }
                    });
                    }
            }
        }

        private boolean previousTaskFinished(TaskInterface task) {
            if (taskOrder.get(task.getTaskID()) == -1) {
                return true;
            }
            return tasksFinished.contains(taskOrder.get(task.getTaskID()));
        }

        private void finishIt(TaskInterface task) {
            tasksFinished.add(task.getTaskID());
        }

        private void putItInTheNextQueue(TaskInterface task) {
            synchronized (waitingQueues.get(queueNum+1)) {
                waitingQueues.get(queueNum+1).add(task);
            }
        }
    }
}
