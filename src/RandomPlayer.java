import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RandomPlayer {
	public static void main(String[] args) throws MalformedURLException,
			RemoteException, NotBoundException {
		GameInterface gi = (GameInterface) Naming.lookup("GAME");

		System.out.println("Game interface proxy " + gi);

		long id = gi.register(args[0]);
		System.out.println("My ID = " + id);

		System.out.println("Wait for start...");
		gi.waitForStart(id);

		int shID = 0;
		while (true) {
			try {
				gi.turnRight(id, shID);
				if (gi.getCourse(id, shID) == GameInterface.Course.NORTH) {
					while (true) {
						gi.move(id, shID);
						// gi.turnRight(id, shID);
						// gi.turnRight(id, shID);
						// gi.turnRight(id, shID);
						// gi.turnRight(id, shID);

						while (gi.messageQueueSize(id, shID) > 0) {
							GameInterface.PositionAndCourse pc = gi.getMessage(
									id, shID);

							System.out.println("Strzelam w okret "
									+ pc.getPosition().toString() + " kurs "
									+ pc.getCourse().fullCourseName());

							gi.fire(id, shID, pc.getPosition());

						}
					}
				}
			} catch (Exception e) {
				int ships = gi.getNumberOfAvaiablewarships(id);

				shID++;

				System.out.println("Dostepne statki:          " + ships);

				try {
					System.out.println("Wiadomosci o przeciwniku: "
							+ gi.messageQueueSize(id, shID));
				} catch (Exception e2) {
					shID++;
				}

				if (ships == 0)
					System.exit(0);
			}
		}
	}
}
