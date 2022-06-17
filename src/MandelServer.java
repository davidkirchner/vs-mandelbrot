import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MandelServer extends Remote {
    public boolean addClient(MandelClient client) throws RemoteException;

    public void setDetail(int detail) throws RemoteException;

    public void setStartStop(int yStart, int yStop, int xStart, int xStop) throws RemoteException;

    public void calculateRGB() throws RemoteException;

    public void SemaphoreImpl(int value) throws RemoteException;

    public void p() throws RemoteException;

    public void v() throws RemoteException;

    public int returnNumberOfThreads() throws RemoteException;
}
