import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MandelClient extends Remote {
    public void setRGB(int[][] bild) throws RemoteException;

    public void sendTasks(MandelServer server) throws RemoteException;

    // public String getName() throws RemoteException;

}
