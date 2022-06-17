import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;

@SuppressWarnings("serial")
class MandelClientImpl extends UnicastRemoteObject implements MandelClient {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1056;
    public static final int DETAIL = 1024;
    private BufferedImage image;
    private JFrame frame;
    private String name;
    public int yStart, yStop, xStart, xStop;
    public int numberOfThreads;

    public MandelClientImpl(String name) throws RemoteException {
        this.name = name;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        JLabel label = new JLabel(new ImageIcon(image));
        frame = new JFrame("Mandelbrot");
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public String getName() throws RemoteException {
        return name;
    }

    public void setRGB(int x, int y, int color) throws RemoteException {
        image.setRGB(x, y, color);
    }

    public void updateGUI(MandelServer server) {
        try {
            numberOfThreads = server.returnNumberOfThreads();
            // TODO: client calculates x and y then call server.setStartStop() and
            // server.calculateRGB()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
