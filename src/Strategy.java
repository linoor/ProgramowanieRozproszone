import java.rmi.RemoteException;

/**
 * Created by linoor on 1/10/16.
 */
public interface Strategy {
    public void strategyStep() throws RemoteException;
}
