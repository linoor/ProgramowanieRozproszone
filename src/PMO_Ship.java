import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Statek przechowuje pozycje i kierunek w lokalnych koordynatach.
 * Do rysowania/komunikacji z graczem nalezy pobierac dane
 * wg. danych uniwersalnych.
 * 
 * @author oramus
 *
 */
public class PMO_Ship {
	private CoordinateSystem coSystem;
	private AtomicBoolean inGame = new AtomicBoolean( true );
	private AtomicReference< GameInterface.Position > position;
	private AtomicReference< GameInterface.Course > course;
	private AtomicLong lastSpotTest;
	private AtomicBoolean lastFireResult = new AtomicBoolean( false );
	
	private AtomicBoolean shipLostByFrendlyFire = new AtomicBoolean( false );
	private AtomicBoolean shipLostByOwnUnitImpact = new AtomicBoolean( false );
	private AtomicBoolean shipLostByStepBeyondBoard = new AtomicBoolean( false );
	
	public PMO_Ship( GameInterface.Position position, GameInterface.Course course, CoordinateSystem coSystem ) {
		this.position = new AtomicReference<>( position );
		this.course = new AtomicReference<>( course );
		lastSpotTest = new AtomicLong();
		lastSpotTest.set( System.currentTimeMillis() );
		this.coSystem = coSystem;
	}
	
	boolean test() {
		if ( shipLostByFrendlyFire.get() ) {
			PMO_SOUT.println( "- Okret zatopiony przez friendly fire");
			return false;
		}
		if ( shipLostByOwnUnitImpact.get() ) {
			PMO_SOUT.println( "- Okret zatopiony w wyniku zderzenia z inna jednostka gracza");
			return false;
		}
		if ( shipLostByStepBeyondBoard.get() ) {
			PMO_SOUT.println( "- Okret utracony w wyniku wyjscia poza plansze do gry");
			return false;
		}
		return true;
	}
	
	
	void setShipLostByFrendlyFire() {
		PMO_SOUT.println( "setShipLostByFrendlyFire");
		shipLostByFrendlyFire.set( true );
	}
	
	void setShipLostByOwnUnitImpact() {
		PMO_SOUT.println( "setShipLostByOwnUnitImpact");
		shipLostByOwnUnitImpact.set( true );
	}
	
	public void setLastFireResult( boolean res ) {
		lastFireResult.set( res );
	}
	
	public boolean getLastFireResult() {
		return lastFireResult.get();
	}
	
	public void updateSpotTest( long t ) {
		lastSpotTest.set( t );
	}
	
	public CoordinateSystem getCoordinateSystem() {
		return coSystem;
	}
	
	public long getSpotTestTime() {
		return lastSpotTest.get();
	}
	
	public void turnLeft() {
		course.set( course.get().afterTurnToLeft() );
	}

	public void turnRight() {
		course.set( course.get().afterTurnToRight() );
	}

	public void move() {
		position.set( course.get().next( position.get() ) );
		
		if ( ! PMO_Arbiter.positionInsideBoard( position.get() ) ) {
			inGame.set( false );
			PMO_SOUT.println( "shipLostByStepBeyondBoard");
			shipLostByStepBeyondBoard.set( true );
		}
	}
	
	public GameInterface.Position getPosition() {
		return position.get();
	}
	
	public GameInterface.Course getCourse() {
		return course.get();
	}

	public GameInterface.Position getUniversalPosition() {
		return coSystem.convert( position.get() );
	}
	
	public GameInterface.Course getUniversalCourse() {
		return coSystem.convert( course.get() );
	}
	
	public boolean hasBeenHit( GameInterface.Position targetPosition ) {
		
		if ( !isAlive() ) return false; // statek zatopiony nie moze byc ponownie trafiony
		
//		System.out.println( "Target position  : " + targetPosition.toString() );
//		System.out.println( "Own    position  : " + position.get().toString() );		
		
		if ( targetPosition.equals( position.get() ) ) {
			inGame.set( false );
			return true;
		}
		return false;
	}
	
	public boolean isAlive() {
		return inGame.get();
	}
	
	public void shipHasBeenDestroyed() {
		inGame.set(false);
	}

	public String toString() {
		if ( ! isAlive() ) return "#";
		return course.toString();
	}
	
	public String toUniversalString() {
		if ( ! isAlive() ) return "#";
		return coSystem.convert( course.get() ).toString();
	}
}

 
