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
    private final List<TaskInterface> tasksFinished        = new ArrayList<>();

    private int lastTask = -1;

    @Override
    public void setNumberOfQueues(int queues) {
        System.out.println("setting number of queues " + queues);
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
        System.out.println(waitingQueues);
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
            System.out.println("thread limit set");
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
								System.out.println(String.format("Running task nr %d from queue %d to queue %d, it should finish at the queue %d",
                                        task.getTaskID(),
                                        queueNum,
                                        queueNum + 1,
                                        task.getLastQueue()));
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

        private void finishIt(TaskInterface task) {
            tasksFinished.add(task);
            System.out.println("Task " + task.getTaskID() + " FINISHED");
        }

        private TaskInterface workDatTask(TaskInterface taskToRun) {
            return taskToRun.work(queueNum);
        }

        private void putItInTheNextQueue(TaskInterface task) {
            synchronized (waitingQueues.get(queueNum+1)) {
                waitingQueues.get(queueNum+1).add(task);
            }
        }
    }
}
