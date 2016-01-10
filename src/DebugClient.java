import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class DebugClient {
	public static void main(String[] args) throws MalformedURLException,
			RemoteException, NotBoundException {
		DebugInterface gi = (DebugInterface) Naming.lookup("DEBUG");
		
		while ( true ) {
			System.out.println( gi.getBoardState( "aplipapli" ) );
			try {	
				Thread.sleep( 1000 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
