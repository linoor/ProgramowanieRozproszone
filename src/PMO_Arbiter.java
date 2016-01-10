
public class PMO_Arbiter {
	public static boolean colInsideBoard( int i ) {
		if ( i < 0 ) return false;
		if ( i >= GameInterface.WIDTH ) return false;
		return true;
	}
	
	public static boolean rowInsideBoard( int i ) {
		if ( i < 0 ) return false;
		if ( i >= GameInterface.HIGHT ) return false;
		return true;
	}
	
	public static boolean positionInsideBoard( GameInterface.Position pos ) {
		return colInsideBoard( pos.getCol() ) && rowInsideBoard( pos.getRow() );
	}
	
}
