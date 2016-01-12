import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class Start {
    public static void main(String[] args) throws MalformedURLException,
            RemoteException, NotBoundException {
        GameInterface gi = (GameInterface) Naming.lookup("GAME");

        System.out.println("Game interface proxy " + gi);

        long id = gi.register(args[0]);
        System.out.println("My ID = " + id);

        System.out.println("Wait for start...");
        gi.waitForStart(id);

        Ship ship = new UpAndDownShip(id, gi, 0);

        while (true) {
            try {
                int ships = gi.getNumberOfAvaiablewarships(id);
                System.out.println("Dostepne statki: " + ships);
                ship.step();
                if (ships == 0)
                    System.exit(0);
                Thread.sleep( 1000 );
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }
}
