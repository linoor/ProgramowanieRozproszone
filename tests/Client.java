import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by linoor on 12/12/15.
 */
public class Client {

    static LinkExchangeSystem exchangeSystem;

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "LINKEXCHANGE";
            exchangeSystem = LinkExchangeSystemHelper.narrow(ncRef.resolve_str(name));

            IntHolder userId = new IntHolder();
            exchangeSystem.register("linoor", userId);
            assert userId.value != -1;
            System.out.println("UserId equals: " + userId.value);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
