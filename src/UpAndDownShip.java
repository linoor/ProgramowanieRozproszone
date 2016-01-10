import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class UpAndDownShip extends Ship implements Strategy {

    public UpAndDownShip(long playerId, GameInterface gi, int warshipId) {
        super(playerId, gi, warshipId);
    }

    public void strategyStep() throws RemoteException {
        GameInterface.Position currentPosition = gi.getPosition(super.playerId, super.warshipId);
        // check that you are on your own lane
        // go up
        // fire every two steps
        // go down
        // repeat
    }
}
