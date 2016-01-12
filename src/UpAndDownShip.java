import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class UpAndDownShip extends BaseShip implements Ship {

    boolean goingUp = true;

    public UpAndDownShip(long playerId, GameInterface gi, int warshipId) {
        super(playerId, gi, warshipId);
    }

    public void step() throws RemoteException {
        GameInterface.Position currentPosition = gi.getPosition(super.playerId, super.warshipId);
        // test - go up until the top of the board
        // change the direction when at the top
        if (goingUp) {
            goingUp = goUp();
        } else {
            goingUp = !goDown();
        }

        GameInterface.PositionAndCourse positionAndCourse = thereIsShipNearby();
        if (positionAndCourse != null) {
            fire(positionAndCourse.getPosition());
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (positionAndCourse != null) {
            fire(positionAndCourse.getPosition());
        }
        System.out.println("step");
        // TODO change lane
    }
}
