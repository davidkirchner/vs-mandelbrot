import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MandelServerImpl extends UnicastRemoteObject implements MandelServer {
    private int detail;
    private int yStart, yStop, xStart, xStop;
    private double cr, ci;
    private int value, color;
    private static final double TOP = -1.0;
    private static final double LEFT = -2.0;
    private static final double ZOOM = 1.0 / 256.0;
    private static int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int[] PALETTE = { 0x00421E0F, 0x0019071A, 0x0009012F, 0x00040449, 0x00000764, 0x000C2C8A,
            0x001852B1, 0x00397DD1, 0x0086B5E5, 0x00D3ECF8, 0x00F1E9BF, 0x00F8C95F, 0x00FFAA00, 0x00CC8000, 0x00995700,
            0x006A3403, };
    private ArrayList<MandelClient> allClients;

    public MandelServerImpl() throws RemoteException {
        allClients = new ArrayList<MandelClient>();
    }

    public synchronized boolean addClient(MandelClient client) throws RemoteException {
        String name = client.getName();
        for (Iterator<MandelClient> iter = allClients.iterator(); iter.hasNext();) {
            MandelClient mc = iter.next();
            try {
                if (mc.getName().equals(name)) {
                    return false;
                }
            } catch (RemoteException exc) {
                iter.remove();
            }
        }
        allClients.add(client);
        return true;

    }

    public void setDetail(int detail) throws RemoteException {
        this.detail = detail;
    }

    public void setStartStop(int yStart, int yStop, int xStart, int xStop) throws RemoteException {
        this.yStart = yStart;
        this.yStop = yStop;
        this.xStart = xStart;
        this.xStop = xStop;
    }

    public int returnNumberOfThreads() throws RemoteException {
        return NUMBER_OF_THREADS;
    }

    public void SemaphoreImpl(int value) throws RemoteException {
        if (value < 0) {
            throw new IllegalArgumentException("Parameter < 0");
        }
        this.value = value;
    }

    public synchronized void p() throws RemoteException {
        while (value == 0) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }
        value--;
    }

    public synchronized void v() throws RemoteException {
        value++;
        notify();
    }

    public void calculateRGB() throws RemoteException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(NUMBER_OF_THREADS, NUMBER_OF_THREADS, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        Task task = new Task();
        pool.execute(task);
    }

    class Task implements Runnable {

        public Task() {
        }

        public void run() {
            for (int y = yStart; y < yStop; y++) {
                ci = y * ZOOM + TOP;
                for (int x = xStart; x < xStop; x++) {
                    cr = x * ZOOM + LEFT;
                    double zr = 0.0;
                    double zi = 0.0;
                    color = 0x000000;
                    for (int i = 0; i < detail; i++) {
                        double zrzr = zr * zr;
                        double zizi = zi * zi;
                        if (zrzr + zizi >= 4) {
                            color = PALETTE[i & 15];
                            break;
                        }
                        zi = 2.0 * zr * zi + ci;
                        zr = zrzr - zizi + cr;
                    }
                    for (Iterator<MandelClient> iter = allClients.iterator(); iter.hasNext();) {
                        try {
                            MandelClient client = iter.next();
                            client.setRGB(x, y, color);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                            iter.remove();
                        }
                    }
                }
            }
        }
    }
}
