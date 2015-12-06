import java.util.*;
import java.util.concurrent.ExecutorService;
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
        waitingQueues.get(task.getFirstQueue()).add(task);
        System.out.println(waitingQueues);
    }

    private class QueueManager implements Runnable {

        private ExecutorService executor = Executors.newFixedThreadPool(1);
        private int queueNum;

        public QueueManager(int queueNum) {
            this.queueNum = queueNum;
        }

        public void setThreadLimit(int limit) {
            System.out.println("thread limit set");
            executor = Executors.newFixedThreadPool(limit);
        }

        @Override
        public void run() {
            while (true) {
                synchronized (waitingQueues.get(queueNum)) {
                    TaskInterface taskToRun = waitingQueues.get(queueNum).poll();
                    if (taskToRun == null ) {
                        continue;
                    }
                    executor.submit(() -> {
                        System.out.println(String.format("Running task nr %d from queue %d to queue %d", taskToRun.getTaskID(),
                                queueNum,
                                queueNum + 1));
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
