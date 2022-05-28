import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import org.w3c.dom.events.MouseEvent;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
// import java.awt.event.KeyListener;
// import java.awt.event.KeyEvent;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
import java.awt.event.*;

public class Standalone extends JPanel{

    static int IMG_WIDTH = 1280;
    static int IMG_HEIGHT = 720;

    private static JFrame frame;
    private static JLabel label;

    double rangeReal = 3.5;
    double rangeImaginary = 2;
    double centerReal = -0.75;
    double centerImaginary = 0;
    int maxIterations = 50;

    
    
    KeyListener keyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_W) {
                centerImaginary -= 0.05 * rangeImaginary;
                display(createImage());
            } else if (event.getKeyCode() == KeyEvent.VK_A) {
                centerReal -= 0.05 * rangeReal;
                display(createImage());
            } else if (event.getKeyCode() == KeyEvent.VK_S) {
                centerImaginary += 0.05 * rangeImaginary;
                display(createImage());
            } else if (event.getKeyCode() == KeyEvent.VK_D) {
                centerReal += 0.05 * rangeReal;
                display(createImage());
            } else if (event.getKeyCode() == KeyEvent.VK_UP) {
                maxIterations += Math.ceil(maxIterations * 0.05);
                display(createImage());
            } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println(maxIterations);
                if (maxIterations - maxIterations * 0.05 > 1)
                    maxIterations -= maxIterations * 0.05;
                display(createImage());
            }else if (event.getKeyCode() == KeyEvent.VK_I) {
                rangeReal -= 0.05 * rangeReal;
                rangeImaginary -= 0.05 * rangeImaginary;
                display(createImage());
            }else if (event.getKeyCode() == KeyEvent.VK_O){
                rangeReal += 0.05 * rangeReal;
                rangeImaginary += 0.05 * rangeImaginary;
                display(createImage());                
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
        }

        @Override
        public void keyTyped(KeyEvent event) {
        }
    };


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

                int colorValue = 255 * iteration / maxIterations;
                Color pixelColor = new Color(255 - colorValue, 255 - colorValue, 255 - colorValue);
                img.setRGB(x, y, pixelColor.getRGB());
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
            // frame.addMouseListener(mouseListener);

        } else
            label.setIcon(new ImageIcon(image));
    }

    public static void main(String[] args) {

        Standalone t = new Standalone();
        BufferedImage img = t.createImage();
        t.display(img);
    }

}