import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class UpAndDownShip extends BaseShip implements Ship {

    boolean goingUp = true;

    public UpAndDownShip(long playerId, GameInterface gi, int warshipId) {
        super(playerId, gi, warshipId);
    }

    public synchronized void step() throws RemoteException {
        if (!gi.isAlive(playerId, warshipId)) {
           return;
        }
        GameInterface.Position currentPosition = gi.getPosition(super.playerId, super.warshipId);
        goUp();

        GameInterface.PositionAndCourse positionAndCourse = thereIsShipNearby();
        if (positionAndCourse != null) {
//                fire(positionAndCourse.getPosition());
        }
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (positionAndCourse != null) {
//                fire(positionAndCourse.getPosition());
        }
    }
}
