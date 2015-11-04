import org.junit.Test;

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
    public void testSimpleLabirynth() {
        Room first = new Room();
        Room two = new Room();
        Room three = new Room();
        Room four = new Room();
        Room five = new Room();
        Room six = new Room();
        Room seven = new Room();

        first.addCorridor(two);
        first.addCorridor(three);
        first.addCorridor(four);

        four.addCorridor(seven);
        four.addCorridor(five);
        // set way from four to seven equal to 4
        try {
            four.setDistance(seven, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        five.addCorridor(six);

        six.addCorridor(seven);

        PathFinderInterface pathFinder = new PathFinder();
        pathFinder.entranceToTheLabyrinth(first);
        assertEquals(4, pathFinder.getShortestDistanceToExit());
    }
}
