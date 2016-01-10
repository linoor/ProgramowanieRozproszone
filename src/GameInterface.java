import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
	// wartosc ponizszych stalych mogas ulec zmienie, nazwy nie!
	public final int HIGHT = 30;
	public final int WIDTH = 10;

	public final long DELAY = 900; // opoznienie bazowe dla realizacji metod
									// move, fire,
									// turn

	public final long FIRE_DELAY_HALF = (long) (0.65 * DELAY);
	public final long MOVE_DELAY_HALF = (long) (0.4 * DELAY);
	public final long TURN_DELAY_HALF = (long) (0.45 * DELAY);

	/**
	 * Odleglosc detekcji okretu stojacego
	 */
	public final double Rv = Math.sqrt(2 * 4 * 4);

	/**
	 * Odleglosc detekcji okretu bedacego w ruchu
	 */
	public final double Rm = Math.sqrt(2 * 5 * 5);

	/**
	 * Odleglosc detekcji okretu, ktory strzela
	 */
	public final double Rs = Math.sqrt(2 * 7 * 7);

	/**
	 * Typ wyliczeniowy dostepnych kursow okretu
	 * 
	 * @author oramus
	 */
	public enum Course implements Serializable {
		NORTH {
			public Position next(Position p) {
				return new Position(p.getCol(), p.getRow() + 1);
			}

			@Override
			public Course afterTurnToLeft() {
				return WEST;
			}
			
			@Override
			public Course afterTurnToRight() {
				return EAST;
			}

			public String toString() {
				return "^";
			}
		},
		SOUTH {
			public Position next(Position p) {
				return new Position(p.getCol(), p.getRow() - 1);
			}

			@Override
			public Course afterTurnToLeft() {
				return EAST;
			}

			@Override
			public Course afterTurnToRight() {
				return WEST;
			}

			public String toString() {
				return "v";
			}

		},
		EAST {
			public Position next(Position p) {
				return new Position(p.getCol() + 1, p.getRow());
			}

			@Override
			public Course afterTurnToLeft() {
				return NORTH;
			}

			@Override
			public Course afterTurnToRight() {
				return SOUTH;
			}

			public String toString() {
				return ">";
			}
		},
		WEST {
			public Position next(Position p) {
				return new Position(p.getCol() - 1, p.getRow());
			}

			@Override
			public Course afterTurnToLeft() {
				return SOUTH;
			}

			@Override
			public Course afterTurnToRight() {
				return NORTH;
			}

			public String toString() {
				return "<";
			}
		};
		/**
		 * Metoda zwraca kolejne polozenie okretu poruszajacego sie danym kursem
		 * 
		 * @param p
		 *            aktualna pozycja okretu
		 * @return kolejna pozycja okretu poruszajacego sie danym kursem
		 */
		abstract public Position next(Position p);

		/**
		 * Zwraca nazwe kierunku kursu
		 * @return nazwa kierunku
		 */
		public String fullCourseName() {
			return name();
		}
		
		abstract public Course afterTurnToRight();

		abstract public Course afterTurnToLeft();
	}

	/**
	 * Klasa do produkcji niezmienniczych obiektow reprezentujacych polozenie na
	 * planszy. Przedefiniowano equals.
	 * 
	 * @author oramus
	 */
	public class Position implements Serializable {
		private static final long serialVersionUID = -5006829363311504738L;
		final private int row;
		final private int col;

		public Position(int col, int row) {
			this.row = row;
			this.col = col;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Position))
				return false;
			Position ps = (Position) obj;

			return (ps.row == row) && (ps.col == col);
		}

		@Override
		public int hashCode() {
			return 1024 * (1 + row) + col;
		}
		
		@Override
		public String toString() {
			return "[" + col + "," + row + "]";
		}
	}

	class PositionAndCourse implements Serializable {
		private static final long serialVersionUID = -7756007375536743336L;
		private final Position pos;
		private final Course crs;

		public PositionAndCourse(Position pos, Course crs) {
			this.pos = pos;
			this.crs = crs;
		}

		public Position getPosition() {
			return pos;
		}

		public Course getCourse() {
			return crs;
		}

	}

	/**
	 * Rejestruje gracza i przekazuje mu unikalny, losowy numer identyfikacyjny
	 * 
	 * @param playerName
	 *            nazwa gracza
	 * @return numer identyfikacyjny gracza
	 */
	long register(String playerName) throws RemoteException;

	/**
	 * Wstrzymuje gracza do chwili dolaczenie do gry przeciwnika i rozpoczecia
	 * rozgrywki.
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 */
	void waitForStart(long playerID) throws RemoteException;

	/**
	 * Zwraca liczbe dostepnych dla gracza okretow
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @return liczba dostepnych jednostek
	 * @throws RemoteException
	 */
	int getNumberOfAvaiablewarships(long playerID) throws RemoteException;

	/**
	 * Zwraca kurs okretu
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @return kurs
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	Course getCourse(long playerID, int warshipID) throws RemoteException;

	/**
	 * Zwraca polozenie jednostki na planszy
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @return miejsce, w ktorym znajduje sie na planszy jednostka
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	Position getPosition(long playerID, int warshipID) throws RemoteException;

	/**
	 * Pozwala sprawdzic, czy jednostka o danym numerze nie zostala zatopiona
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @return true - okret sprawny, false - wrak
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	boolean isAlive(long playerID, int warshipID) throws RemoteException;

	/**
	 * Zmiana kursu - 90 stopni w lewo
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	void turnLeft(long playerID, int warshipID) throws RemoteException;

	/**
	 * Zmiana kursu - 90 stopni w prawo
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	void turnRight(long playerID, int warshipID) throws RemoteException;

	/**
	 * Zmiana polozenia jednostki o jedno pole
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	void move(long playerID, int warshipID) throws RemoteException;

	/**
	 * Oddanie strzalu na wskazana pozycje.
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @param target
	 *            pozycja celu
	 * @return true - jednostka przeciwnika trafiona, false - pudlo
	 * @throws RemoteException
	 *             - polecenie nie moglo zostac zrealizowane np. ze wzgledu na
	 *             zatopienie jednostki
	 */
	boolean fire(long playerID, int warshipID, Position target)
			throws RemoteException;

	/**
	 * Metoda zwraca liczbe wiadomosci do odbioru o wykrytych okretach
	 * przeciwnika. Metoda nieblokujaca.
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @return liczba wiadomosci do odbioru
	 * @throws RemoteException
	 */
	int messageQueueSize(long playerID, int warshipID) throws RemoteException;

	/**
	 * Metoda zwraca polozenie i kurs wykrytego okretu przeciwnika. Nieodebrane
	 * wiadomosci sa przechowywane przez serwis. Wiadomosc nie zawiera
	 * informacji o czasie nadania - nie mozna wiec sprawdzic, jak bardzo jest
	 * stara inaczej niz odbierajac wiadomosci na biezaco.
	 * 
	 * @param playerID
	 *            numer identyfikacyjny gracza
	 * @param warshipID
	 *            numer jednostki
	 * @return poloznie i kurs wykrytej jednostki przeciwnika
	 * @throws RemoteException
	 */
	PositionAndCourse getMessage(long playerID, int warshipID)
			throws RemoteException;

}
