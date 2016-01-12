import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public class BaseShip {

    protected GameInterface gi;
    protected long playerId;
    protected int warshipId;

    public BaseShip(long playerId, GameInterface gi, int warshipId) {
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
        GameInterface.Position position = gi.getPosition(playerId, warshipId);
        GameInterface.Course course     = gi.getCourse(playerId, warshipId);

        // check that you won't get outside the board
        if (position.getCol() == (GameInterface.HIGHT-1)) {
            System.out.println(String.format("The ship %d can't go any further up", warshipId));
            return false;
        }

        // check that you won't bump into other ship
        // stop if there is another ship near above you
        for (int i = 0; i < gi.getNumberOfAvaiablewarships(playerId); i++) {
            GameInterface.Position otherShipPosition = gi.getPosition(playerId, i);
            if (otherShipPosition.getCol() == position.getCol() &&
                otherShipPosition.getRow() - position.getRow() < 2 &&
                otherShipPosition.getRow() - position.getRow() > 0) {
                return false;
            }
        }

        // change the course to go up
        String courseName = course.fullCourseName();
        if (courseName.equals("WEST")) {
            gi.turnRight(playerId, warshipId);

        } else if (courseName.equals("EAST")) {
            gi.turnLeft(playerId, warshipId);

        } else if (courseName.equals("NORTH")) {
        } else if (courseName.equals("SOUTH")) {
            gi.turnLeft(playerId, warshipId);
            gi.turnLeft(playerId, warshipId);

        }

        gi.move(playerId, warshipId);
        return true;
    }

    public void goRight() {

    }

    public void goLeft() {

    }

    public void goDown() {

    }
}
