import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.lang.Math;

public class Standalone extends JPanel {

    static int IMG_WIDTH = 1280;
    static int IMG_HEIGHT = 720;

    private static JFrame frame;
    private static JLabel label;

    double rangeReal = 3.5;
    double rangeImaginary = 2;
    double centerReal = -0.75;
    double centerImaginary = 0;
    int maxIterations = 50;
    Color colorPalette[];

    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent event) {
            // key W
            if (event.getKeyCode() == KeyEvent.VK_W) {
                centerImaginary -= 0.05 * rangeImaginary;
                display(createImage());
                // key A
            } else if (event.getKeyCode() == KeyEvent.VK_A) {
                centerReal -= 0.05 * rangeReal;
                display(createImage());
                // key S
            } else if (event.getKeyCode() == KeyEvent.VK_S) {
                centerImaginary += 0.05 * rangeImaginary;
                display(createImage());
                // key D
            } else if (event.getKeyCode() == KeyEvent.VK_D) {
                centerReal += 0.05 * rangeReal;
                display(createImage());
                // key I
            } else if (event.getKeyCode() == KeyEvent.VK_I) {
                rangeReal -= 0.05 * rangeReal;
                rangeImaginary -= 0.05 * rangeImaginary;
                display(createImage());
                // key O
            } else if (event.getKeyCode() == KeyEvent.VK_O) {
                rangeReal += 0.05 * rangeReal;
                rangeImaginary += 0.05 * rangeImaginary;
                display(createImage());
                // key Arrow up
            } else if (event.getKeyCode() == KeyEvent.VK_UP) {
                maxIterations += Math.ceil(maxIterations * 0.05);
                updateColorPalette(maxIterations);
                display(createImage());
                // key Arrow down
            } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println(maxIterations);
                if (maxIterations - maxIterations * 0.05 > 1) {
                    maxIterations -= maxIterations * 0.05;
                    updateColorPalette(maxIterations);
                    display(createImage());
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
        }

        @Override
        public void keyTyped(KeyEvent event) {
        }
    };

    public Standalone() {
        updateColorPalette(maxIterations);
    }

    public void updateColorPalette(int n) {
        Color end = new Color(0, 0, 0);
        Color middle = new Color(233, 107, 0);
        Color start = new Color(0, 51, 99);

        colorPalette = new Color[n];
        colorPalette[0] = start;
        colorPalette[n - 1] = end;

        double diffRedStartMiddle = (double) (middle.getRed() - start.getRed()) / (n/2);
        double diffGreenStartMiddle = (double) (middle.getGreen() - start.getGreen()) / (n/2);
        double diffBlueStartMiddle = (double) (middle.getBlue() - start.getBlue()) / (n/2);

        double diffRedMiddleEnd = (double) (end.getRed() - middle.getRed()) / (n/2);
        double diffGreenMiddleEnd = (double) (end.getGreen() - middle.getGreen()) / (n/2);
        double diffBlueMiddleEnd = (double) (end.getBlue() - middle.getBlue()) / (n/2);

        System.out.println("diffRMiddleEnd" + diffRedMiddleEnd);
        System.out.println("diffGMiddleEnd" + diffGreenMiddleEnd);
        System.out.println("diffBMiddleEnd" + diffBlueMiddleEnd);

        for (int i = 0; i < n; i++) {
            Color c;
            if (i < n / 2) {
                c = new Color(
                        (int) Math.ceil(start.getRed() + i * diffRedStartMiddle),
                        (int) Math.ceil(start.getGreen() + i * diffGreenStartMiddle),
                        (int) Math.ceil(start.getBlue() + i * diffBlueStartMiddle));
            } else {
                c = new Color(
                        (int) Math.ceil(middle.getRed() + (i-n/2) * diffRedMiddleEnd),
                        (int) Math.ceil(middle.getGreen() + (i-n/2) * diffGreenMiddleEnd),
                        (int) Math.ceil(middle.getBlue() + (i-n/2) * diffBlueMiddleEnd));
            }
            colorPalette[i] = c;
            System.out.println("i: " + i);
            System.out.println("r: " + c.getRed() + "  g: " + c.getGreen() + "  b: " + c.getBlue());

        }
    }

    public BufferedImage createImage() {

        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);

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

                while (zReal * zReal + zImaginary * zImaginary <= 4 && iteration < maxIterations) {

                    double tmp = zReal * zReal - zImaginary * zImaginary + cReal;
                    zImaginary = 2 * zReal * zImaginary + cImaginary;
                    zReal = tmp;
                    iteration += 1;
                }

                // int colorValue = 255 * iteration / maxIterations;
                // Color pixelColor = new Color(255 - colorValue, 255 - colorValue, 255 -
                // colorValue);
                // img.setRGB(x, y, pixelColor.getRGB());
                img.setRGB(x, y, colorPalette[iteration - 1].getRGB());
            }
        }
        return img;
    }

    public void display(BufferedImage image) {
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
            frame.addKeyListener(keyListener);
        } else
            label.setIcon(new ImageIcon(image));
    }

    public static void main(String[] args) {
        Standalone t = new Standalone();
        BufferedImage img = t.createImage();
        t.display(img);
    }

}