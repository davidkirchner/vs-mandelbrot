import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MandelServer extends Remote {
    // public boolean addClient(MandelClient client) throws RemoteException;

    public void setDetail(int width, int height, int detail) throws RemoteException;

    public void setImageProperties(double top, double left, double zoom) throws RemoteException;

    public void calculateRGB(int yStart, int yStop, int xStart, int xStop) throws RemoteException;

    public int[][] returnColor() throws RemoteException;

    public void startCalculatingRGB() throws RemoteException;

    public boolean isFinish() throws RemoteException;

    public void initSlavServers() throws RemoteException;
}
