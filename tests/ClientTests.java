import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by linoor on 12/12/15.
 */
public class ClientTests {

    static LinkExchangeSystem exchangeSystem;

    @BeforeClass
    public static void setUp() {
        String[] args = new String[0];
        new Server().main(args);
    }

    @Test
    public void testRegisterClient() {
        try {
            // create and initialize the ORB
            String[] args = new String[0];
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "LINKEXCHANGE";
            exchangeSystem = LinkExchangeSystemHelper.narrow(ncRef.resolve_str(name));

            IntHolder userId = new IntHolder();
            exchangeSystem.register("linoor", userId);
            assertNotEquals(-1, userId.value);

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
