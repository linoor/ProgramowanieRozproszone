import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class Ship {

    protected GameInterface gi;
    protected long playerId;
    protected int warshipId;

    public Ship(long playerId, GameInterface gi, int warshipId) {
        this.playerId = playerId;
        this.gi = gi;
        this.warshipId = warshipId;
    }

    /**
     *
     * @return true if move successful
     * @throws RemoteException
     */
    public boolean goUp() throws RemoteException {
        GameInterface.PositionAndCourse positionAndCourse = gi.getMessage(playerId, warshipId);
        if (positionAndCourse.getPosition().getCol() == gi.HIGHT-1) {
            System.out.println(String.format("The ship %d can't go any further up", warshipId));
            return false;
        }

        if (positionAndCourse.getCourse().)

        return true;
    }

    public void goRight() {

    }

    public void goLeft() {

    }

    public void goDown() {

    }
}
