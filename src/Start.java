import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class Start {

    private static GameInterface gameInterface;
    private static long playerId;

    public static void main(String[] args) throws MalformedURLException,
            RemoteException, NotBoundException {
        GameInterface gi = (GameInterface) Naming.lookup("GAME");
        gameInterface = gi;

        System.out.println("Game interface proxy " + gi);

        long id = gi.register(args[0]);
        playerId = id;
        System.out.println("My ID = " + id);

        System.out.println("Wait for start...");
        gi.waitForStart(id);

        // start all of the ship threads
        for (int i = 0; i < gi.getNumberOfAvaiablewarships(id); i++) {
            new Thread(new ShipThread(new UpAndDownShip(id, gi, i))).start();
        }

        while (true) {
            try {
                int ships = gi.getNumberOfAvaiablewarships(id);
                System.out.println("Dostepne statki: " + ships);
                if (ships == 0)
                    System.exit(0);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Exception in the loop checking available ships");
            }
        }
    }
}
