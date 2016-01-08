import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class PMO_Fleet {
	final private List< PMO_Ship > pMO_Ships = new ArrayList<>();
	private Random rnd;
	
	private boolean notToClose( GameInterface.Position p ) {
		for ( PMO_Ship sh : pMO_Ships ) {
			if ( PMO_DistanceHelper.distanceSQ( sh.getPosition(), p ) < 4 ) return false;
		}
		
		return true;
	}
	
	private void randomPlace( int rowsToUse, CoordinateSystem coSystem ) {
		int r, c;
		GameInterface.Position p;
		
		for ( int i = 0; i < GameInterface.WIDTH; i++ ) {
			
			do {
				r = rnd.nextInt( rowsToUse );
				c = rnd.nextInt( GameInterface.WIDTH );
				p = new GameInterface.Position( c, r ); // lokalne koordynaty
//				System.out.println( "r " + r + " c " + c );
			} while ( ! notToClose(p) );
			
			pMO_Ships.add( new PMO_Ship( p, GameInterface.Course.values()[ rnd.nextInt( 4 )], coSystem));			
	//		pMO_Ships.add( new PMO_Ship( p, GameInterface.Course.NORTH, coSystem));			
		}
	}
	
	public PMO_Ship getShip( int id ) {
		return pMO_Ships.get( id );
	}
	
	public Collection<PMO_Ship> getShips() {
		return pMO_Ships;
	}
	
	public int countAvailableShips() {
		int count  = 0;
		for ( PMO_Ship s : pMO_Ships ) {
			if ( s.isAlive() ) count++;
		}
		return count;
	}
	
	public int countShipsAtPosition( GameInterface.Position pos ) {
		int counter = 0;
		
		for ( PMO_Ship s : pMO_Ships ) {
			if ( s.isAlive() && pos.equals( s.getUniversalPosition() ) ) counter++;
		}		
		
		return counter;
	}
	
	public boolean canContinueBattle() {
		for ( PMO_Ship s : pMO_Ships )
			if ( s.isAlive() ) return true;
		return false;
	}
	
	public PMO_Fleet( int rowsToUse, CoordinateSystem coSystem ) {
		rnd = new Random();
		randomPlace( rowsToUse, coSystem);
	}
	
}
