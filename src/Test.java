import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;

public class Test extends JPanel {

    static int IMG_WIDTH = 1920;
    static int IMG_HEIGHT = 1080;

    private static JFrame frame;
    private static JLabel label;

    public static BufferedImage createImage() {
        int maxIterations = 200;
        double middleR = -0.75;
        double middleI = 0;
        double rangeR = 3.5;
        double rangeI = 2;

        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // g.setPaint(new Color(255, 255, 255));
        // g.fillRect(0, 0, img.getWidth(), img.getHeight());

        for (int x = 0; x < IMG_WIDTH; x++) {
            for (int y = 0; y < IMG_HEIGHT; y++) {

                double xCoordinate = x;
                double yCoordinate = y;
                double xPercentage = xCoordinate / img.getWidth();
                double yPercentage = yCoordinate / img.getHeight();
                double cReal = xPercentage * rangeR + middleR - rangeR / 2;
                double cImaginary = yPercentage * rangeI + middleI - rangeI / 2;
                double zReal = 0;
                double zImaginary = 0;

                int iteration = 0;
                while (iteration < maxIterations && zReal * zReal + zImaginary * zImaginary <= 4) {

                    double tmp = zReal * zReal - zImaginary * zImaginary + cReal;
                    zImaginary = 2 * zReal * zImaginary + cImaginary;
                    zReal = tmp;
                    iteration++;
                }

                if (iteration >= maxIterations - 1) {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return img;
    }

    public static void display(BufferedImage image) {
        if (frame == null) {
            frame = new JFrame();
            frame.setTitle("stained_image");
            frame.setSize(100, 200);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        } else
            label.setIcon(new ImageIcon(image));
    }

    public static void main(String[] args) {

        BufferedImage img = createImage();
        display(img);
    }

}