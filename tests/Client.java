import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linoor on 12/12/15.
 */
public class Client {

    static LinkExchangeSystem exchangeSystem;

    public static void testSimpleRegister() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        IntHolder userId = new IntHolder();
        exchangeSystem.register("linoor", userId);
        assert userId.value == 0;

        exchangeSystem.register("linoorek", userId);
        assert userId.value == 1;
        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testUserWithExistingNameReturnsMinusOne() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        IntHolder userId = new IntHolder();
        exchangeSystem.register("linoorsame", userId);
        assert userId.value != -1;
        exchangeSystem.register("linoorsame", userId);
        assert userId.value == -1;
        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testMultipleUsersRegisterAtTheSameTime() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        final CyclicBarrier gate = new CyclicBarrier(20+1);

        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    gate.await();

                    IntHolder userId = new IntHolder();
                    exchangeSystem.register("multipleregistertest" + index, userId);
                    assert userId.value != -1;
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        Arrays.stream(threads).forEach(Thread::start);

        try {
            gate.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testRegisterMultipleUsersWithSameName() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        final CyclicBarrier gate = new CyclicBarrier(20+1);

        final AtomicBoolean after0 = new AtomicBoolean(false);

        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    gate.await();

                    IntHolder userId = new IntHolder();
                    exchangeSystem.register("multipleuserswithsamename", userId);
                    synchronized (after0) {
                        if (userId.value == 0) {
                            after0.set(true);
                            return;
                        } else {
                            assert userId.value == -1;
                        }
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        Arrays.stream(threads).forEach(Thread::start);

        try {
            gate.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testAddLink() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        IntHolder userId = new IntHolder();
        exchangeSystem.register("linoor", userId);

        IntHolder linkId = new IntHolder();
        exchangeSystem.addLink(userId.value, "some link", linkId);
        assert linkId.value == 0;

        exchangeSystem.addLink(userId.value, "some other link", linkId);
        assert linkId.value == 1;

        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testAddLinkWithWrongUser() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        IntHolder userId = new IntHolder();
        exchangeSystem.register("linoor", userId);

        IntHolder linkId = new IntHolder();
        exchangeSystem.addLink(10, "some link", linkId);
        assert linkId.value == -1;

        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void testRemoveLinkIfExisted() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(String.format("**** %s ****", methodName));
        IntHolder userId = new IntHolder();
        exchangeSystem.register("linoor2", userId);

        IntHolder linkId = new IntHolder();
        exchangeSystem.addLink(userId.value, "some link", linkId);

        boolean ret = exchangeSystem.linkRemove(userId.value, linkId.value);
        assert ret;

        System.out.println(String.format("**** END %s ****", methodName));
    }

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "LINKEXCHANGE";
            exchangeSystem = LinkExchangeSystemHelper.narrow(ncRef.resolve_str(name));
//            testSimpleRegister();
//            testUserWithExistingNameReturnsMinusOne();
//            testMultipleUsersRegisterAtTheSameTime();
//            testRegisterMultipleUsersWithSameName();
            testAddLink();
//            testAddLinkWithWrongUser();
            testRemoveLinkIfExisted();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
