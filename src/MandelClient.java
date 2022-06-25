import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.TimerTask;

public interface MandelClient extends Remote {
    public void setRGB(int[][] bild) throws RemoteException;

    public void sendTasks(MandelServer server) throws RemoteException;

    public void setZoomDestination(MandelServer server, double left, double top, double zoom, int animationSteps) throws RemoteException;

    // public String getName() throws RemoteException;

}
