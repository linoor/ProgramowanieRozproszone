import java.rmi.Remote;
import java.rmi.RemoteException;


public interface DebugInterface extends Remote {
	String getBoardState() throws RemoteException;
	String getBoardState( String password ) throws RemoteException;	
}
