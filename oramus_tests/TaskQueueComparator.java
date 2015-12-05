import java.util.Comparator;

/**
 * Created by linoor on 11/16/15.
 */
public class TaskQueueComparator implements Comparator<TaskInterface> {

    @Override
    public int compare(TaskInterface o1, TaskInterface o2) {
        if (o1.getTaskID() < o2.getTaskID()) {
            return -1;
        }
        if (o1.getTaskID() > o2.getTaskID()) {
            return 1;
        }
        return 0;
    }
}
