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

    Queue<GameInterface.PositionAndCourse> detectedShips = new LinkedList<GameInterface.PositionAndCourse>();

    public BaseShip(long playerId, GameInterface gi, int warshipId) {
        this.playerId = playerId;
        this.gi = gi;
        this.warshipId = warshipId;
    }

    public void waitUntilCourseIs(String course) throws RemoteException {
        while (true) {
            GameInterface.Course tmpCourse = gi.getCourse(playerId, warshipId);
            if (tmpCourse.fullCourseName().equals(course)) {
                break;
            }
        }
    }

    /**
     *
     * @return true if move successful
     * @throws RemoteException
     */
    public synchronized boolean goUp() throws RemoteException {
        GameInterface.Position position = gi.getPosition(playerId, warshipId);
        GameInterface.Course course     = gi.getCourse(playerId, warshipId);

        // check that you won't get outside the board
        if (position.getRow() >= (GameInterface.HIGHT-1)) {
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
            waitUntilCourseIs("NORTH");
            gi.move(playerId, warshipId);
        } else if (courseName.equals("EAST")) {
            gi.turnLeft(playerId, warshipId);
            waitUntilCourseIs("NORTH");
            gi.move(playerId, warshipId);

        } else if (courseName.equals("NORTH")) {
            gi.move(playerId, warshipId);
        } else if (courseName.equals("SOUTH")) {
            gi.turnLeft(playerId, warshipId);
            gi.turnLeft(playerId, warshipId);
            waitUntilCourseIs("NORTH");
            gi.move(playerId, warshipId);
        }

        return true;
    }

    public void goRight() {

    }

    public void goLeft() {

    }

    public synchronized boolean goDown() throws RemoteException {
        GameInterface.Position position = gi.getPosition(playerId, warshipId);
        GameInterface.Course course     = gi.getCourse(playerId, warshipId);

        // check that you won't get outside the board
        if (position.getRow() <= 0) {
            System.out.println(String.format("The ship %d can't go any further down", warshipId));
            return false;
        }

        // check that you won't bump into other ship
        // stop if there is another ship near above you
        for (int i = 0; i < gi.getNumberOfAvaiablewarships(playerId); i++) {
            GameInterface.Position otherShipPosition = gi.getPosition(playerId, i);
            if (otherShipPosition.getCol() == position.getCol() &&
                otherShipPosition.getRow() - position.getRow() > -2 &&
                otherShipPosition.getRow() - position.getRow() < 0) {
                return false;
            }
        }

        // change the course to go down
        String courseName = course.fullCourseName();
        if (courseName.equals("WEST")) {
            gi.turnLeft(playerId, warshipId);
            waitUntilCourseIs("SOUTH");
            gi.move(playerId, warshipId);
        } else if (courseName.equals("EAST")) {
            gi.turnRight(playerId, warshipId);
            waitUntilCourseIs("SOUTH");
            gi.move(playerId, warshipId);
        } else if (courseName.equals("NORTH")) {
            gi.turnLeft(playerId, warshipId);
            gi.turnLeft(playerId, warshipId);
            waitUntilCourseIs("SOUTH");
            gi.move(playerId, warshipId);
        } else if (courseName.equals("SOUTH")) {
            gi.move(playerId, warshipId);
        }

        return true;    }

    /**
     *
     * @return null if there is no ship nearby
     * positio and course if there is a ship nearby
     * @throws RemoteException
     */
    public GameInterface.PositionAndCourse thereIsShipNearby() throws RemoteException {
        if (gi.messageQueueSize(playerId, warshipId) > 0) {
            GameInterface.PositionAndCourse positionAndCourse = gi.getMessage(playerId, warshipId);
            // TODO check that it's close enough
            return positionAndCourse;
        }
        return null;
    }

    /**
     * fire method wrapper
     * @param target
     * @throws RemoteException
     */
    public void fire(GameInterface.Position target) throws RemoteException {
        // check for friendly fire
        for (int i = 0; i < gi.getNumberOfAvaiablewarships(playerId); i++) {
            GameInterface.Position friendlyShip = gi.getPosition(playerId, i);
            if (friendlyShip.getRow() == target.getRow() &&
                friendlyShip.getCol() == target.getCol()) {
                return;
            }
        }
        gi.fire(playerId, warshipId, target);
    }
}
