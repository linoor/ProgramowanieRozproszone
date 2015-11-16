/**
 * Created by linoor on 11/13/15.
 */
public class Task implements  TaskInterface {

    final private int firstQueue;
    final private int lastQueue;

    final private int taskId;

    final private boolean keepOrder;

    public Task(int firstQueue, int lastQueue, int taskId, boolean keepOrder) {
        this.firstQueue = firstQueue;
        this.lastQueue = lastQueue;
        this.taskId = taskId;
        this.keepOrder = keepOrder;
    }

    @Override
    public int getFirstQueue() {
        return firstQueue;
    }

    @Override
    public int getLastQueue() {
        return lastQueue;
    }

    @Override
    public int getTaskID() {
        return taskId;
    }

    @Override
    public boolean keepOrder() {
        return keepOrder;
    }

    @Override
    public TaskInterface work(int queue) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        java.lang.System.out.println(String.format("Task %d is working in queue %d...", getTaskID(), queue));
        return this;
    }
}
