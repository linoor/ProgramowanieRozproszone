import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

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

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "LINKEXCHANGE";
            exchangeSystem = LinkExchangeSystemHelper.narrow(ncRef.resolve_str(name));
            testSimpleRegister();
            testUserWithExistingNameReturnsMinusOne();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
