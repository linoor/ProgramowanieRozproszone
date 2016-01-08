public class PMO_DistanceHelper {
	/**
	 * Zwraca kwadrat odleglosc
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static int distanceSQ(GameInterface.Position p1,
			GameInterface.Position p2) {
		int dc = p1.getCol() - p2.getCol();
		int dr = p1.getRow() - p2.getRow();
		
		return dc*dc + dr*dr;
	}
	
	public static double distance( GameInterface.Position p1,
			GameInterface.Position p2) {
		return Math.sqrt( distanceSQ( p1, p2 ));
	}
}
