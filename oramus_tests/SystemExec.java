import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by linoor on 11/13/15.
 */
class SystemExec implements SystemInterface {

    private List<QueueManager> queueManagers = new ArrayList<>();
    private List<Queue<TaskInterface>> waitingQueues = new ArrayList<>();

    @Override
    public void setNumberOfQueues(int queues) {
        for (int i = 0; i < queues; i++) {
            waitingQueues.add(new PriorityQueue<>(new TaskQueueComparator()));
            QueueManager newQueueManager = new QueueManager(i);
            queueManagers.add(newQueueManager);
            new Thread(newQueueManager).start();
        }
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
            waitingQueues.get(task.getFirstQueue()).add(task);
        }
        System.out.println(waitingQueues);
    }

    private class QueueManager implements Runnable {

        private Executor executor;
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
                if (executor == null) {
                    continue;
                }
                synchronized (waitingQueues.get(queueNum)) {
                    if (waitingQueues.get(queueNum).peek() != null) {
                        System.out.println("here");
                        TaskInterface taskToRun = waitingQueues.get(queueNum).remove();
                        System.out.println("got a task to run");
                        executor.execute(() -> {
                            System.out.println(String.format("Running task nr %d from queue %d to queue %d", taskToRun.getTaskID(),
                                    queueNum,
                                    queueNum+1));
                            TaskInterface newTask = taskToRun.work(queueNum);
                            final int newQueueNum = queueNum + 1;
                            if (newQueueNum < newTask.getLastQueue()) {
                                synchronized (waitingQueues.get(newQueueNum)) {
                                    waitingQueues.get(newQueueNum).add(newTask);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
