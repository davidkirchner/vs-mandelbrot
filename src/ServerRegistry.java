import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRegistry extends Remote {
    public void registry(Slave slave) throws RemoteException;

    public void unregistry(Slave slave) throws RemoteException;
}
