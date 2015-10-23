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
}
