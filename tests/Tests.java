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
          1, 3, 1, 2
        });
        system.addTask(new Task(0, 3 , 1, true));
        system.addTask(new Task(0, 2 , 2, true));
        system.addTask(new Task(2, 3, 3, false));
        try {
            Thread.sleep(2000);
            system.addTask(new Task(1, 2 , 4, true));
            Thread.sleep(500);
            system.addTask(new Task(0, 3 , 5, false));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        system.waitForFinish();
    }
}
