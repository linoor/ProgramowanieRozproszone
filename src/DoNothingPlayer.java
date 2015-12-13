import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class DoNothingPlayer {
	public static void main(String[] args) throws MalformedURLException,
			RemoteException, NotBoundException {
		GameInterface gi = (GameInterface) Naming.lookup("GAME");

		System.out.println("Game interface proxy " + gi);

		long id = gi.register(args[0]);
		System.out.println("My ID = " + id);

		System.out.println("Wait for start...");
		gi.waitForStart(id);

		while (true) {
			try {
				int ships = gi.getNumberOfAvaiablewarships(id);
				System.out.println("Dostepne statki:          " + ships);
				if (ships == 0)
					System.exit(0);
				Thread.sleep( 1000 );
			} catch (Exception e) {
				System.exit(0);
			}
		}
	}
}
