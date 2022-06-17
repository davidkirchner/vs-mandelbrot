import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MandelClient extends Remote {
    public void setRGB(int x, int y, int color) throws RemoteException;

    public String getName() throws RemoteException;

    public void updateGUI(MandelServer server);
}
