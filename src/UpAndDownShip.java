import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class UpAndDownShip extends BaseShip implements Ship {

    public UpAndDownShip(long playerId, GameInterface gi, int warshipId) {
        super(playerId, gi, warshipId);
    }

    public void step() throws RemoteException {
        GameInterface.Position currentPosition = gi.getPosition(super.playerId, super.warshipId);
        // test - go up until the top of the board
        System.out.println(String.format("Ship %d: %d %d", warshipId, currentPosition.getRow(), currentPosition.getCol()));
        goUp();

        System.out.println("BEFORE detecting ship");
        GameInterface.PositionAndCourse positionAndCourse = thereIsShipNearby();
        System.out.println("after detecting ship");
        if (positionAndCourse != null) {
            fire(positionAndCourse.getPosition());
        }
        // check that you are on your own lane
        // go up
        // fire every two steps
        // go down
        // repeat
    }
}
