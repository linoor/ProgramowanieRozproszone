import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 * Created by linoor on 12/12/15.
 */
public class Server {
    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            LinkExchangeImpl linkExchangeImpl = new LinkExchangeImpl();
            linkExchangeImpl.setOrb(orb);

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(linkExchangeImpl);
            LinkExchangeSystem href = LinkExchangeSystemHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "LINKEXCHANGE";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);

            System.out.println("Server running");

            orb.run();
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            e.printStackTrace();
        }
    }
}
