import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;


/**
 * Created by linoor on 10/23/15.
 */
public class LabirynthTests {
    @Test
    public void shouldReturnMaxValWhenNoExitFound() {
        PathFinderInterface pathFinder = new PathFinder();
        assertEquals(false, pathFinder.exitFound());
        assertEquals(Double.MAX_VALUE,
                pathFinder.getShortestDistanceToExit());
    }

    @Test
    public void distanceFromStartShouldReturnZeroForTheEntrance() {
        Room entrance = new Room();
        assertEquals(0.0, entrance.getDistanceFromStart());
    }

    @Test
    public void testDistanceFromStartFromSecondRoom() {
        Room entrance = new Room();
        Room secondRoom = new Room();
        entrance.addCorridor(secondRoom);

        PathFinderInterface pathFinder = new PathFinder();
        pathFinder.entranceToTheLabyrinth(entrance);

        assertEquals(1.0, secondRoom.getDistanceFromStart());
    }

    @Test
    public void testDistanceFromStartFromThirdRoom() {
        Room entrance = new Room();
        Room secondRoom = new Room();
        Room thirdRoom = new Room();
        entrance.addCorridor(secondRoom);
        secondRoom.addCorridor(thirdRoom);

        PathFinderInterface pathFinder = new PathFinder();
        pathFinder.entranceToTheLabyrinth(entrance);

        assertEquals(2.0, thirdRoom.getDistanceFromStart());
    }
}
