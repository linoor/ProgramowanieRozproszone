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

        System.out.println("before");
        position.getCol();
        System.out.println("after");
        // check that you won't get outside the board
        if (position.getCol() == (GameInterface.HIGHT-1)) {
            System.out.println(String.format("The ship %d can't go any further up", warshipId));
            return false;
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
