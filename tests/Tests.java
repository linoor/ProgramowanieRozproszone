import org.junit.Test;

/**
 * Created by linoor on 11/13/15.
 */
public class Tests {

    @Test
    public void testQueueSystem() {
        SystemExec systemExec = new SystemExec();
        systemExec.setNumberOfQueues(4);
        systemExec.setThreadsLimit(new int[] {
          1, 3, 1, 2
        });
        systemExec.addTask(new Task(0, 3 , 1, true));
        systemExec.addTask(new Task(0, 2 , 2, true));
        systemExec.addTask(new Task(2, 3, 3, false));
        try {
            Thread.sleep(2000);
            systemExec.addTask(new Task(1, 2 , 4, true));
            Thread.sleep(500);
            systemExec.addTask(new Task(0, 3 , 5, false));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
