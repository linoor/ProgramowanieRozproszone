import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class PMO_AllShips {
	private Collection< PMO_Ship > allShips;
	
	public PMO_AllShips() {
		allShips = new ArrayList<PMO_Ship>();
	}
	
	public void add( Collection<PMO_Ship> shipsToAdd ) {
		allShips.addAll( shipsToAdd );
	}
	
	private String separator() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "   ---");
		for ( int c = 0; c < GameInterface.WIDTH; c++ ) {
			sb.append( "--");
		}		
		sb.append("\n");
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sep = separator();
		
		Map<GameInterface.Position, PMO_Ship> pos = new HashMap<>( allShips.size() );
		
		for ( PMO_Ship s : allShips ) {
			pos.put( s.getUniversalPosition(), s );
		}
		
		sb.append( sep );
		GameInterface.Position p;
		for ( int r = GameInterface.HIGHT - 1; r >= 0; r-- ) {
			sb.append( String.format( "%2d", r ) + " |" );
			for ( int c = 0; c < GameInterface.WIDTH; c++ ) {
				p = new GameInterface.Position( c, r );
				if ( pos.containsKey( p ) ) {
					sb.append( " " + pos.get(p).toUniversalString());
				} else { 
					sb.append( " .");
				}
			}
			sb.append(" |\n");
		}
		sb.append( sep );
		return sb.toString();
	}
	
	public void show() {
		System.out.println( toString() );
	}
	
	public Collection<PMO_Ship> find( GameInterface.Position pos ) {
		Collection<PMO_Ship> ships = new ArrayList<>();
		
		for ( PMO_Ship sh : allShips ) {
			if ( sh.isAlive() && sh.getUniversalPosition().equals( pos ) ) {
				ships.add( sh );
			}
		}
		
		return ships;
	}
	
}
