import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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

    public MandelClientImpl(String name) throws RemoteException {
        this.name = name;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        JLabel label = new JLabel(new ImageIcon(image));
        label.addMouseListener(mouseListener());
        frame = new JFrame("Mandelbrot");
        frame.add(label);
        frame.pack();
        frame.setVisible(true);

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static MouseListener mouseListener() {
        return new MouseAdapter() {
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
                mouseClick = true;
            }
        };
    }

    public String getName() throws RemoteException {
        return name;
    }

    // public void setRGB(int[][] bild) throws RemoteException {
    // class Task implements Runnable {}
    // for (int i = 0; i < bild.length; i++) {
    // for (int j = 0; j < bild[i].length; j++) {
    // image.setRGB(i, j, bild[i][j]);
    // }
    // }
    // frame.repaint();
    // }

    public void setRGB(int[][] bild) throws RemoteException, InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        IntStream.range(0, bild.length).forEach((i) -> {
            Runnable task = () -> {
                for (int j = 0; j < bild[i].length; j++) {
                    image.setRGB(i, j, bild[i][j]);
                }
            };
            pool.execute(task);
        });
        frame.repaint();
        pool.shutdown();
    }
}