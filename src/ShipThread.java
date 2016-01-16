import java.rmi.RemoteException;

/**
 * Created by linoor on 1/16/16.
 */
public class ShipThread implements Runnable {

    private Ship ship;

    public ShipThread(Ship ship) {
        this.ship = ship;
    }

    public void run() {
        while (true)  {
            try {
                ship.step();
            } catch (RemoteException e) {
                System.out.println("There has been an error when trying to do a step");
            }
        }
    }
}

