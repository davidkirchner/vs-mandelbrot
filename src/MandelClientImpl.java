import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
class MandelClientImpl extends UnicastRemoteObject implements MandelClient {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    public static final int DETAIL = 800;
    public static double top = -1.0;
    public static double left = -2.0;
    public static double zoom = 1.0 / 512.0;
    private BufferedImage image;
    private JFrame frame;
    public int yStart, yStop, xStart, xStop;
    public static boolean mouseClick = false;
    private ExecutorService pool;
    private int iter = 0;

    private Timer timer;

    public MandelClientImpl(MandelServer server) throws RemoteException {
        pool = Executors.newCachedThreadPool();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // Client sends image's width, height and detail to server. This
        // is necessary to set the size of int[][] bild, which contains
        // the color information of the mandelbrot image and for the
        // calculation of RGB Values.
        server.setDetail(WIDTH, HEIGHT, DETAIL);
        frame = new JFrame("Mandelbrot");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        JLabel label = new JLabel(new ImageIcon(image));
        label.addMouseListener(new MouseAdapter() {
            // When client clicks on a point, this will
            // calculate the value of top, left and zoom therefore
            // client needs to send tasks to server.
            @Override
            public void mouseReleased(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON1) {
                    left = (event.getX() - WIDTH / 4.0) * zoom + left;
                    top = (event.getY() - HEIGHT / 4.0) * zoom + top;
                    zoom = zoom / 2.0;
                    System.out.println("left: " + left + " top:" + top + " zoom: " + zoom);
                } else {
                    left = (event.getX() - WIDTH) * zoom + left;
                    top = (event.getY() - HEIGHT) * zoom + top;
                    zoom = zoom * 2.0;
                }
                try {
                    sendTasks(server);
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });
        frame.add(label);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION) {
                    pool.shutdown();
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
    }



    public void sendTasks(MandelServer server) throws RemoteException {
        // We send the server the coord and the zoom values.
        server.setImageProperties(top, left, zoom);
        // Now let the master server do it job.
        server.startCalculatingRGB();
        // Let's wait for the result.
        while (server.isFinish())
            ;
        // Server sends the result and client passes the result to ThreadPool
        // Threads read the array and set the RGB
        setRGB(server.returnColor());
        System.out.printf("Iteration: %d\r", iter);
        System.out.println("left: " + left + " top:" + top + " zoom: " + zoom);
        iter++;
    }

    public void setZoomDestination(MandelServer server, double _left, double _top, double _zoom, int animationSteps) throws RemoteException {
        timer = new Timer();
        timer.schedule(new CountUpTimer(server, _left, _top, _zoom, animationSteps), 300, 300);
    }

    class CountUpTimer extends TimerTask{
        private double destLeft, destTop, destZoom;
        private int animationSteps;
        private double leftSteps, topSteps, zoomSteps;
        private MandelServer server;
        int counter = 0;


        CountUpTimer(MandelServer _server, double destinationLeft, double destinationTop, double destinationZoom, int steps){
            destLeft = destinationLeft;
            destTop = destinationTop;
            destZoom = destinationZoom;
            animationSteps = steps;

            leftSteps = destLeft / animationSteps;
            topSteps = destTop / animationSteps;
            zoomSteps = destZoom / animationSteps;

            left = 0;
            top = 0;
            zoom = 0;
            server = _server;
        }

        public void run(){
            if(counter < animationSteps){
                left += leftSteps;
                top += topSteps;
                zoom += zoomSteps;

                try {
                    sendTasks(server);
                } catch (RemoteException re) {
                    re.printStackTrace();
                }

                counter++;
            } else 
                timer.cancel();
        }
    }

    // TODO: support multiple clients
    // public String getName() throws RemoteException {
    // return name;
    // }

    // public void setRGB(int[][] bild) throws RemoteException {
    // class Task implements Runnable {}
    // for (int i = 0; i < bild.length; i++) {
    // for (int j = 0; j < bild[i].length; j++) {
    // image.setRGB(i, j, bild[i][j]);
    // }
    // }
    // frame.repaint();
    // }
    public void setRGB(int[][] bild) throws RemoteException {
        IntStream.range(0, bild.length).forEach((i) -> {
            Runnable task = () -> {
                for (int j = 0; j < bild[i].length; j++) {
                    image.setRGB(i, j, bild[i][j]);
                }
            };
            pool.execute(task);
        });
        frame.repaint();
    }
}
