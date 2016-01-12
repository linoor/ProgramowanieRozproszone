import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by linoor on 1/10/16.
 */
public class BaseShip {

    protected GameInterface gi;
    protected long playerId;
    protected int warshipId;

    protected int myColumn;
    protected int myRow;

    Queue<GameInterface.PositionAndCourse> detectedShips = new LinkedList<GameInterface.PositionAndCourse>();

    public BaseShip(long playerId, GameInterface gi, int warshipId) {
        this.playerId = playerId;
        this.gi = gi;
        this.warshipId = warshipId;

        GameInterface.Position position = null;
        try {
            position = gi.getPosition(playerId, warshipId);
            myColumn = position.getCol();
            myRow    = position.getRow();
        } catch (RemoteException e) {
            System.out.println("problem with getting position in the constructor");
            e.printStackTrace();
        }
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
        if (myColumn >= (GameInterface.HIGHT-1)) {
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

        myColumn += 1;
        gi.move(playerId, warshipId);
        return true;
    }

    public void goRight() {

    }

    public void goLeft() {

    }

    public void goDown() {

    }

    /**
     *
     * @return null if there is no ship nearby
     * positio and course if there is a ship nearby
     * @throws RemoteException
     */
    public GameInterface.PositionAndCourse thereIsShipNearby() throws RemoteException {
        System.out.println("before checking the message queue");
        if (gi.messageQueueSize(playerId, warshipId) > 0) {
            System.out.println("A SHIP HAS BEEN DETECTED");
            GameInterface.PositionAndCourse positionAndCourse = gi.getMessage(playerId, warshipId);
            // TODO check that it's close enough
            return positionAndCourse;
        }
        System.out.println("NO SHIP DETECTED");
        return null;
    }

    /**
     * fire method wrapper
     * @param target
     * @throws RemoteException
     */
    public void fire(GameInterface.Position target) throws RemoteException {
        gi.fire(playerId, warshipId, target);
    }
}
