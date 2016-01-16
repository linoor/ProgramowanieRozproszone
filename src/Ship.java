import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public interface Ship {
    public void step() throws RemoteException;
    public boolean isAlive() throws RemoteException;
}
