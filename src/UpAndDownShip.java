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
        System.out.println("before go up");
        goUp();
        System.out.println("after go up");
        // check that you are on your own lane
        // go up
        // fire every two steps
        // go down
        // repeat
    }
}
