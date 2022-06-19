import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MandelClient extends Remote {
    public void setRGB(int[][] bild) throws RemoteException, InterruptedException;

    public String getName() throws RemoteException;

}
