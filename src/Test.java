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
        double rangeReal = 3.5;
        double rangeImaginary = 2;
        double centerReal = -0.75;
        double centerImaginary = 0;
        int maxIterations = 200;

        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        // Graphics2D g = img.createGraphics();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                double xCoordinate = x;
                double yCoordinate = y;
                double xRelative = xCoordinate / img.getWidth();
                double yRelative = yCoordinate / img.getHeight();
                double cReal = rangeReal * xRelative + centerReal - rangeReal / 2;
                double cImaginary = rangeImaginary * yRelative + centerImaginary - rangeImaginary / 2;
                
                double zImaginary = 0;
                double zReal = 0;
                int iteration = 0;

                while (zReal * zReal + zImaginary * zImaginary < 5 && iteration < maxIterations) {

                    double tmp = zReal * zReal - zImaginary * zImaginary + cReal;
                    zImaginary = 2 * zReal * zImaginary + cImaginary;
                    zReal = tmp;
                    iteration += 1;
                }

                if (iteration < maxIterations){
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }else{
                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    public static void display(BufferedImage image) {
        if (frame == null) {
            frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("Mandelbrot");
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