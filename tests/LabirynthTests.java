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
        Room entrance = new Room("asd", 0.0);
        assertEquals(0.0, entrance.getDistanceFromStart());
    }

    @Test
    public void testSimpleLabirynth() {
        Room first = new Room("first", 0.0);
        Room two = new Room("two", 1.0);
        Room three = new Room("three", 1.0);
        Room four = new Room("four", 1.0);
        Room five = new Room("five", 2.0);
        Room six = new Room("six", 3.0);
        Room seven = new Room("seven", 4.0);
        Room eight = new Room("eight", 8.0);

        first.addCorridor(two);
        first.addCorridor(three);
        first.addCorridor(four);

        three.addCorridor(eight);

        four.addCorridor(five);

        five.addCorridor(six);

        six.addCorridor(seven);

        seven.setExit();

        PathFinderInterface pathFinder = new PathFinder();
        pathFinder.entranceToTheLabyrinth(first);
        assertEquals(4.0, pathFinder.getShortestDistanceToExit());
    }
}
