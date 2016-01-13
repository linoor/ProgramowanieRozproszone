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
        Ship ship2 = new UpAndDownShip(id, gi, 1);

        new Thread(new ShipThread(ship)).start();
        new Thread(new ShipThread(ship2)).start();

        // start all of the ship threads
        for (int i = 0; i < gi.getNumberOfAvaiablewarships(id); i++) {
            try {
                Thread.sleep(500);
                new Thread(new ShipThread(new UpAndDownShip(id, gi, i))).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            try {
                int ships = gi.getNumberOfAvaiablewarships(id);
                System.out.println("Dostepne statki: " + ships);
                if (ships == 0)
                    System.exit(0);
                Thread.sleep( 1000 );
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }
    }

    private static class ShipThread implements Runnable {

        private Ship ship;

        public ShipThread(Ship ship) {
           this.ship = ship;
        }

        public void run() {
            while (true)  {
                try {
                    ship.step();
                } catch (RemoteException e) {
                    System.out.println();
                    System.out.println(e.getMessage());
                    e.printStackTrace();
//                    break;
                    System.exit(0);
                }
            }
        }
    }
}
