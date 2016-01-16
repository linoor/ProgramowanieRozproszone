import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class UpAndDownShip extends BaseShip implements Ship {

    boolean goingUp = true;

    public UpAndDownShip(long playerId, GameInterface gi, int warshipId) throws RemoteException {
        super(playerId, gi, warshipId);
    }

    public synchronized void step() throws RemoteException {
        printErrorMessage(1, "before get position");
        GameInterface.Position currentPosition = gi.getPosition(super.playerId, super.warshipId);
        printErrorMessage(1, "after get position");
        goUp();
        printErrorMessage(1, "after go up");

        GameInterface.PositionAndCourse positionAndCourse = isThereShipNearby();
        if (positionAndCourse != null) {
            try {
//                fire(positionAndCourse.getPosition());
                Thread.sleep(700);
//                fire(positionAndCourse.getPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAlive() throws RemoteException {
        return gi.isAlive(playerId, warshipId);
    }

    public void printErrorMessage(int ship, String message) {
        if (warshipId == ship) {
            System.out.println(message);
        }
    }
}
