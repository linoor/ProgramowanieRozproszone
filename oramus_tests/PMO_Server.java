import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class PMO_Server implements GameInterface, DebugInterface {
	private Map<Long, String> usernames = new TreeMap<>();
	private Map<Long, PMO_Fleet> fleet = new TreeMap<>();
	private final int ROWS_TO_PLACE_FLEET = 8;
	private CyclicBarrier barrier = new CyclicBarrier(2);
	private BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();
	private Map<Long, List<ReentrantLock>> locks = new TreeMap<>();
	private Map<Long, Deque<PositionAndCourse>> messages = new TreeMap<>();
	private Map<Long, Long> opponents = new TreeMap<>();
	private PMO_AllShips allShips = new PMO_AllShips();
	private Thread mainWorkerThread;

	private Random rnd = new Random();

	private boolean senseToCountinue() {
		boolean result = true;

		for (PMO_Fleet fl : fleet.values()) {
			result &= fl.canContinueBattle();
		}

		return result;
	}

	private class Worker implements Runnable {
		@Override
		public void run() {
			Runnable task = null;
			while (true) {
				try {
					task = tasksQueue.take();
					PMO_SOUT.println("Task received " + task.toString());
					task.run();
				} catch (InterruptedException e) {
					if (!senseToCountinue()) {
						for (Map.Entry<Long, PMO_Fleet> plf : fleet.entrySet()) {
							if (plf.getValue().canContinueBattle()) {
								PMO_SOUT.println("Player "
										+ usernames.get(plf.getKey())
										+ " has won the game!");
							}
						}
						break;
					}
				}
			}
			PMO_SOUT.println("Main worker has quit.");
			System.exit(0);
		}
	}

	private class WatchDogAndSpotWorker extends TimerTask {
		@Override
		public void run() {
			boolean anySubmitted = false;
			long time = System.currentTimeMillis() - DELAY;

			for (Map.Entry<Long, PMO_Fleet> fl : fleet.entrySet()) {
				for (PMO_Ship ship : fl.getValue().getShips()) {
					// System.out
					// .println("Time " + time +
					// " WatchDog spotme test for player "
					// + usernames.get(fl.getKey()) + " getSpotTestTime() " +
					// ship.getSpotTestTime() );

					if (ship.isAlive() && (ship.getSpotTestTime() < time)) {
						anySubmitted = true;

						// System.out
						// .println("WatchDog submit spotme task for player "
						// + usernames.get(fl.getKey()));

						tasksQueue.add(new WorkerShipTask(fl.getKey(), ship) {
							@Override
							protected void task() {
								spotMe(Rv);
							}

							@Override
							public String toString() {
								return super.toString() + " spot";
							}
						});
					}
				}
			}

			if (!anySubmitted) {
				PMO_SOUT.println("WatchDog - try to interrupt main worker thread");
				mainWorkerThread.interrupt();
			}
		}
	}

	private void startWorkerThread() {
		mainWorkerThread = new Thread(new Worker());
		mainWorkerThread.setDaemon(true);
		mainWorkerThread.start();
	}

	private void startWatchDogThread() {

		Timer tm = new Timer(true);

		tm.schedule(new WatchDogAndSpotWorker(), 1000, DELAY);
	}

	private abstract class WorkerShipTask implements Runnable {

		private final long playerID;
		private final int warshipID;
		protected final PMO_Ship ship;

		public WorkerShipTask(long playerID, int warshipID) {
			this.playerID = playerID;
			this.warshipID = warshipID;
			ship = fleet.get(playerID).getShip(warshipID);
		}

		public WorkerShipTask(long playerID, PMO_Ship ship) {
			this.playerID = playerID;
			this.warshipID = -1;
			this.ship = ship;
		}

		private boolean shipCanDoIt() {
			return ship.isAlive();
		}

		protected void collisionTest() {
			Collection<PMO_Ship> sh = allShips
					.find(ship.getUniversalPosition());
			if (sh.size() > 1) {
				PMO_SOUT.println( "Doszlo do kolizji");
				for (Map.Entry<Long, PMO_Fleet> entry : fleet.entrySet()) {
					if (entry.getValue().countShipsAtPosition(
							ship.getUniversalPosition()) > 1) {
						// gracz zatopil wlasna jednostke
						PMO_SOUT.println("BLAD: Gracz "
								+ usernames.get(entry.getKey())
								+ " zderzyl sie z wlasna jednostka");
						for (PMO_Ship s : entry.getValue().getShips()) {
							if (s.getUniversalPosition().equals(
									ship.getUniversalPosition())) {
								s.setShipLostByOwnUnitImpact();
							}
						}
					}
				}
				for (PMO_Ship s : sh) {
					s.shipHasBeenDestroyed();
				}
			}
		}

		private GameInterface.PositionAndCourse createMessage() {
			return new GameInterface.PositionAndCourse(
					CoordinateSystem.REVERSE.convert(ship.getPosition()),
					CoordinateSystem.REVERSE.convert(ship.getCourse()));
		}

		protected boolean spotMe(double distance) {
			long opponent = opponents.get(playerID);

			Collection<PMO_Ship> oppShips = fleet.get(opponent).getShips();

			Position myPos = ship.getUniversalPosition();

			ship.updateSpotTest(System.currentTimeMillis());
			for (PMO_Ship shipL : oppShips) {
				if (PMO_DistanceHelper.distance(myPos,
						shipL.getUniversalPosition()) < distance) {
					System.out.println("spoted!!!! on position "
							+ CoordinateSystem.REVERSE.convert(
									ship.getPosition()).toString());
					messages.get(opponent).add(createMessage());
					return true;
				}
			}

			return false;
		}

		public String toString() {
			return "Task for player " + usernames.get(playerID) + " ship "
					+ warshipID;
		}

		@Override
		public void run() {
			if (shipCanDoIt())
				task();
		}

		abstract protected void task();
	}

	@Override
	synchronized public long register(String playerName) throws RemoteException {

		PMO_SOUT.println("User " + playerName);
		notifyAll(); // to obudzi watek main

		if (usernames.size() == 2)
			throw new RemoteException("Too many players");
		if (usernames.containsValue(playerName))
			throw new RemoteException("Username exists");
		long id;
		do {
			id = rnd.nextLong();
		} while (usernames.containsKey(id));

		PMO_SOUT.print("Generating fleet for a user ");

		fleet.put(id,
				new PMO_Fleet(ROWS_TO_PLACE_FLEET,
						CoordinateSystem.values()[usernames.size()]));
		PMO_SOUT.println("done");
		usernames.put(id, playerName);
		locks.put(id, new ArrayList<ReentrantLock>());
		for (int i = 0; i < WIDTH; i++) {
			locks.get(id).add(new ReentrantLock());
		}
		messages.put(id, new ConcurrentLinkedDeque<PositionAndCourse>());

		if (usernames.size() == 2) {
			List<Long> pl = new ArrayList<>(usernames.keySet());
			opponents.put(pl.get(0), pl.get(1));
			opponents.put(pl.get(1), pl.get(0));

			allShips.add(fleet.get(pl.get(0)).getShips());
			allShips.add(fleet.get(pl.get(1)).getShips());

			startWorkerThread();
			startWatchDogThread();
		}

		return id;
	}

	private void testCorrectPlayerID(long id) throws RemoteException {
		if (!usernames.containsKey(id))
			throw new RemoteException("ID unknown");
	}

	private void testCorrectWarshipID(int id) throws RemoteException {
		if ((id < 0) || (id >= WIDTH))
			throw new RemoteException("Wrong warship id");
	}

	@Override
	public void waitForStart(long playerID) throws RemoteException {
		testCorrectPlayerID(playerID);
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private void testIDs(long playerID, int warshipID) throws RemoteException {
		testCorrectPlayerID(playerID);
		testCorrectWarshipID(warshipID);
	}

	private void testIDsAndShip(long playerID, int warshipID)
			throws RemoteException {
		testIDs(playerID, warshipID);
		testIfShipAvailable(playerID, warshipID);
	}

	private void testIfShipAvailable(long playerID, int warshipID)
			throws RemoteException {
		if (!fleet.get(playerID).getShip(warshipID).isAlive())
			throw new RemoteException("This warship is only a wrack");
	}

	@Override
	public Course getCourse(long playerID, int warshipID)
			throws RemoteException {
		testIDsAndShip(playerID, warshipID);
		return fleet.get(playerID).getShip(warshipID).getCourse();
	}

	@Override
	public Position getPosition(long playerID, int warshipID)
			throws RemoteException {
		testIDsAndShip(playerID, warshipID);
		return fleet.get(playerID).getShip(warshipID).getUniversalPosition();
	}

	@Override
	public int getNumberOfAvaiablewarships(long playerID)
			throws RemoteException {
		testCorrectPlayerID(playerID);
		return fleet.get(playerID).countAvailableShips();
	}

	@Override
	public boolean isAlive(long playerID, int warshipID) throws RemoteException {
		testIDs(playerID, warshipID);
		return fleet.get(playerID).getShip(warshipID).isAlive();
	}

	private void lock(long playerID, int warshipID) {
		locks.get(playerID).get(warshipID).lock();
	}

	private void unlock(long playerID, int warshipID) {
		locks.get(playerID).get(warshipID).unlock();
	}

	private void sleep(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void testIDAndLockIfSuccessfull(long playerID, int warshipID)
			throws RemoteException {
		testIDsAndShip(playerID, warshipID);
		lock(playerID, warshipID);
	}

	private void sleepAndAddTask(long sleepTime, Runnable task) {
		sleep(sleepTime);
		tasksQueue.add(task);
		sleep(sleepTime);
	}

	@Override
	public void move(final long playerID, final int warshipID)
			throws RemoteException {

		testIDAndLockIfSuccessfull(playerID, warshipID);

		sleepAndAddTask(MOVE_DELAY_HALF,
				new WorkerShipTask(playerID, warshipID) {

					@Override
					public void task() {
						ship.move();
						collisionTest();
						spotMe(Rm);
					}

					public String toString() {
						return super.toString() + " move";
					}

				});

		unlock(playerID, warshipID);
	}

	@Override
	public void turnLeft(final long playerID, final int warshipID)
			throws RemoteException {
		testIDAndLockIfSuccessfull(playerID, warshipID);

		sleepAndAddTask(MOVE_DELAY_HALF,
				new WorkerShipTask(playerID, warshipID) {

					@Override
					protected void task() {
						ship.turnLeft();
						spotMe(Rv);
					}

					public String toString() {
						return super.toString() + " turn Left";
					}

				});

		unlock(playerID, warshipID);
	}

	@Override
	public void turnRight(final long playerID, final int warshipID)
			throws RemoteException {
		testIDAndLockIfSuccessfull(playerID, warshipID);

		sleepAndAddTask(MOVE_DELAY_HALF,
				new WorkerShipTask(playerID, warshipID) {

					@Override
					protected void task() {
						ship.turnRight();
						spotMe(Rv);
					}

					public String toString() {
						return super.toString() + " turn Right";
					}

				});

		unlock(playerID, warshipID);
	}

	@Override
	public boolean fire(final long playerID, final int warshipID,
			final Position target) throws RemoteException {
		testIDAndLockIfSuccessfull(playerID, warshipID);

		sleepAndAddTask(MOVE_DELAY_HALF,
				new WorkerShipTask(playerID, warshipID) {

					@Override
					protected void task() {

						long opponent = opponents.get(playerID);

						Collection<PMO_Ship> oppShips = fleet.get(opponent)
								.getShips();

						ship.setLastFireResult(false);
						for (PMO_Ship shipL : oppShips) {
							if (shipL.hasBeenHit(CoordinateSystem.REVERSE
									.convert(target))) {
								ship.setLastFireResult(true);
								PMO_SOUT.println(super.toString()
										+ " !!HIT HIT HIT!!");
								break;
							}
						}

						Collection<PMO_Ship> ownShips = fleet.get(playerID)
								.getShips();
						// ostrzelanie wlasnych jednostek!!!
						for (PMO_Ship shipL : ownShips) {
							if (shipL.hasBeenHit(target)) {
								ship.setLastFireResult(true);
								PMO_SOUT.println(super.toString()
										+ " !! OWN SHIP has been destroyed !!");
								shipL.setShipLostByFrendlyFire();
								break;
							}
						}
						spotMe(Rs);
					}

					public String toString() {
						return super.toString() + " fire!!!";
					}

				});

		unlock(playerID, warshipID);
		return fleet.get(playerID).getShip(warshipID).getLastFireResult();
	}

	@Override
	public PositionAndCourse getMessage(long playerID, int warshipID)
			throws RemoteException {
		testIDsAndShip(playerID, warshipID);
		return messages.get(playerID).poll();
	}

	@Override
	public int messageQueueSize(long playerID, int warshipID)
			throws RemoteException {
		testIDsAndShip(playerID, warshipID);
		return messages.get(playerID).size();
	}

	@Override
	public String getBoardState() throws RemoteException {
		return null;
	}

	@Override
	public String getBoardState(String password) throws RemoteException {
		if (password.equals("aplipapli"))
			return allShips.toString();
		return null;
	}
	
	private boolean test() {
		boolean result = true;
		
		for ( Map.Entry<Long, String > user : usernames.entrySet() ) {
			PMO_SOUT.println( "--------- Test dla gracza : " + user.getValue() + " ------------");
			for ( PMO_Ship ship : fleet.get( user.getKey() ).getShips() ) {
				result &= ship.test();
			}
		}
		
		return result;
	}

	public static void main(String[] args) throws RemoteException,
			InterruptedException {
		
		
		int PORT = 1099;
		Registry registry = java.rmi.registry.LocateRegistry
				.createRegistry(PORT);

		final PMO_Server srv = new PMO_Server();

		Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
			
			@Override
			public void run() {
				PMO_SOUT.println( "      ----------------------------------");
				PMO_SOUT.println( "      ---> Koniec pracy programu!!! <---");
				PMO_SOUT.println( "      ----------------------------------");
				if ( srv.test() ) {
					PMO_SOUT.println( "Nie wykryto bledow krytycznych w grze graczy");
				}
			}
		}));
		
		
		Remote remote = UnicastRemoteObject.exportObject(srv, 0);

		PMO_SOUT.print("Service registration ");
		registry.rebind("GAME", remote);
		registry.rebind("DEBUG", (DebugInterface) remote);
		PMO_SOUT.println(" - done");

		PMO_SOUT.print("Waiting for users ");
		synchronized (srv) {
			srv.wait();
		}
		PMO_SOUT.println(" - done");

		long sleepTime = 180000;

		PMO_SOUT.print("Going to sleep for " + sleepTime + " msec ");
		srv.sleep(sleepTime);
		PMO_SOUT.println(" - done");
		System.exit(0);
	}
}
