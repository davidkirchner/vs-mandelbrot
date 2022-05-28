import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.*;
import java.awt.*;

interface Mandelbrot extends Remote {
    public void setCoord(double xmin, double xmax, double ymin, double ymax, double cr, double ci, double zoomRate)
            throws RemoteException;

    public void updateCoord() throws RemoteException;

    public void calculateRGB() throws RemoteException;

    public void p() throws RemoteException;

    public void v() throws RemoteException;
}

class MandelbrotImpl extends UnicastRemoteObject implements Mandelbrot {
    private double xmin, xmax, ymin, ymax;
    private double cr, ci;
    private double zoomRate;
    private Color[][] c;
    private int value;

    public MandelbrotImpl() throws RemoteException {
    }

    public synchronized void p() throws RemoteException {
        while (value == 0) {
            try {
                wait();
            } catch (InterruptedException ie) {
                System.out.println(ie);
            }
        }
        value--;
    }

    public synchronized void v() throws RemoteException {
        value++;
        notify();
    }

    public synchronized void setCoord(double xmin, double xmax, double ymin, double ymax, double cr, double ci,
            double zoomRate) throws RemoteException {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.cr = cr;
        this.ci = ci;
        this.zoomRate = zoomRate;
    }

    public synchronized void updateCoord() throws RemoteException {
        double xdim = xmax - xmin;
        double ydim = ymax - ymin;
        xmin = cr - xdim / 2 / zoomRate;
        xmax = cr + xdim / 2 / zoomRate;
        ymin = ci - ydim / 2 / zoomRate;

    }

    public void calculateRGB() throws RemoteException {
    }

    class Task implements Runnable {
        private int yStart, yStop;

        public Task(int yStart, int yStop) {
            this.yStart = yStart;
            this.yStop = yStop;
        }

        public void run() {
        }
    }

    class ApfelWithThreadpool {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }
}

public class MandelServer {
    public static void main(String[] args) {
        try {
            MandelbrotImpl server = new MandelbrotImpl();
            Naming.rebind(args[0], server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
