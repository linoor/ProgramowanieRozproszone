import org.junit.Test;

/**
 * Created by linoor on 11/13/15.
 */
public class Tests {

    @Test
    public void testQueueSystem() {
        System system = new System();
        system.setNumberOfQueues(4);
        system.setThreadsLimit(new int[] {
          2, 3, 1, 2
        });
        system.addTask(new Task(0, 3 , 2, true));
        system.addTask(new Task(0, 2 , 1, true));
        system.addTask(new Task(1, 1 , 3, false));
//        system.finish();
    }
}
