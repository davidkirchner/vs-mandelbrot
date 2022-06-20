import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MandelServerImpl extends UnicastRemoteObject implements MandelServer {
    private int detail;
    private double top;
    private double left;
    private double zoom;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int[] PALETTE = { 0x00421E0F, 0x0019071A, 0x0009012F, 0x00040449, 0x00000764, 0x000C2C8A,
            0x001852B1, 0x00397DD1, 0x0086B5E5, 0x00D3ECF8, 0x00F1E9BF, 0x00F8C95F, 0x00FFAA00, 0x00CC8000, 0x00995700,
            0x006A3403, };
    // private ArrayList<MandelClient> allClients;
    private int[][] bild;
    private ThreadPoolExecutor pool;

    public MandelServerImpl() throws RemoteException {
        // Unbounded queues. Using an unbounded queue (for example a LinkedBlockingQueue
        // without a predefined capacity) will cause new tasks to wait in the queue when
        // all corePoolSize threads are busy. Thus, no more than corePoolSize threads
        // will ever be created. (And the value of the maximumPoolSize therefore doesn't
        // have any effect.) This may be appropriate when each task is completely
        // independent of others, so tasks cannot affect each others execution; for
        // example, in a web page server. While this style of queuing can be useful in
        // smoothing out transient bursts of requests, it admits the possibility of
        // unbounded work queue growth when commands continue to arrive on average
        // faster than they can be processed.
        //
        // LinkedBlockingQueue is a FIFO queue, new elements are inserted at the
        // tail of the queue. Any attempt to retrieve a task out of the queue,
        // can be seen safe as it will not return empty.
        // allClients = new ArrayList<MandelClient>();
        pool = new ThreadPoolExecutor(NUMBER_OF_THREADS, NUMBER_OF_THREADS, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }

    // TODO: support multiple clients
    // public synchronized boolean addClient(MandelClient client) throws
    // RemoteException {
    // String name = client.getName();
    // for (Iterator<MandelClient> iter = allClients.iterator(); iter.hasNext();) {
    // MandelClient mc = iter.next();
    // try {
    // if (mc.getName().equals(name)) {
    // return false;
    // }
    // } catch (RemoteException exc) {
    // iter.remove();
    // }
    // }
    // allClients.add(client);
    // return true;

    // }

    public void setDetail(int width, int height, int detail) throws RemoteException {
        this.detail = detail;
        this.bild = new int[width][height];
    }

    public void setImageProperties(double top, double left, double zoom) throws RemoteException {
        this.top = top;
        this.left = left;
        this.zoom = zoom;
    }

    public synchronized int[][] returnColor() throws RemoteException {
        return this.bild;
    }

    public synchronized boolean isFinish() throws RemoteException {
        // Method getActiveCount returns the approximate number of active threads
        // Whenn all tasks are done, this method will unblock client and he can
        // update image (setRGB).
        if (pool.getActiveCount() == 0) {
            return false;
        }
        return true;
    }

    public void calculateRGB(int yStart, int yStop, int xStart, int xStop) throws RemoteException {
        Task task = new Task(yStart, yStop, xStart, xStop);
        pool.execute(task);
    }

    class Task implements Runnable {
        private int yStart;
        private int yStop;
        private int xStart;
        private int xStop;

        public Task(int yStart, int yStop, int xStart, int xStop) {
            this.yStart = yStart;
            this.yStop = yStop;
            this.xStart = xStart;
            this.xStop = xStop;
        }

        public void run() {
            for (int y = yStart; y < yStop; y++) {
                double ci = y * zoom + top;
                for (int x = xStart; x < xStop; x++) {
                    double cr = x * zoom + left;
                    double zr = 0.0;
                    double zi = 0.0;
                    int color = 0x000000;
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
                    bild[x][y] = color;
                }
            }
        }
    }
}
