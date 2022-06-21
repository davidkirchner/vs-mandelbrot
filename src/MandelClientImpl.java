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
    private String name;
    public int yStart, yStop, xStart, xStop;
    public static boolean mouseClick = false;
    private ExecutorService pool;
    private int iter = 0;

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
        server.setImageProperties(top, left, zoom);
        // A for-loop but using IntStream (Java 8)
        // for(int i = 0; i < MandelClientImpl.HEIGHT ; i += 10){}
        // Client divides the entire Mandelbrot image into multiple
        // smaller images (yStart, yStop, xStart, xStop).
        // Client sends the smaller image as a task to server.
        // Server uses ThreadPool with a fixed number of threads,
        // When all threads are busy, new task will be queued.
        IntStream.iterate(0, i -> i + 10).limit(HEIGHT / 10).parallel().forEach((i) -> {
            try {
                int j = i + 10;
                server.calculateRGB(i, j, 0, WIDTH);
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        });
        while (server.isFinish())
            ;
        setRGB(server.returnColor());
        System.out.printf("Iteration: %d.\r", iter);
        iter++;
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
