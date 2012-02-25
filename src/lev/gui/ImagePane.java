/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levgui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Justin Swanson
 */
public class ImagePane extends JPanel {

    private BufferedImage img;
    int IMG_WIDTH = 0;
    int IMG_HEIGHT = 0;

    ImagePane() {
        setLayout(null);
    }

    public ImagePane(File img) throws IOException {
        this();
        setImage(ImageIO.read(img));
    }

    public ImagePane(String img) throws IOException {
        this();
        setImage(ImageIO.read(new File(img)));
    }

    public ImagePane(BufferedImage img) {
        this();
        setImage(img);
    }

    public ImagePane(URL url) throws IOException {
        this();
        setImage(ImageIO.read(url));
    }

    final public void setImage(BufferedImage originalImage) {
        if (!(IMG_WIDTH == 0 && IMG_HEIGHT == 0)) {
            img = Lg.resizeImageWithHint(originalImage, calcSize(originalImage.getWidth(), originalImage.getHeight()));
        } else {
            img = originalImage;
        }
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        repaint();
    }

    final public void setImage(File in) throws IOException {
        setImage(ImageIO.read(in));
    }

    public void setMaxSize(int x, int y) {
        IMG_WIDTH = x;
        IMG_HEIGHT = y;
        setImage(img);
    }

    Dimension calcSize(double x, double y) {
        double xMod = 1.0 * IMG_WIDTH / x;
        double yMod = 1.0 * IMG_HEIGHT / y;
        double mod = (xMod <= yMod) ? xMod : yMod;
        if (mod < 1) {
            x = x * mod;
            y = y * mod;
        }
        return new Dimension((int) x, (int) y);
    }

//    public void rotateImg() {
//        Image rotatedImage = new BufferedImage(img.getHeight(null), img.getWidth(null), BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
//        g2d.rotate(Math.toRadians(90.0));
//        g2d.drawImage(img, 0, -rotatedImage.getWidth(null), null);
//        g2d.dispose();
//
//        img = new ImageIcon(rotatedImage).getImage();
//        setSize(new Dimension(img.getWidth(null), img.getHeight(null)));
//    }
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }


}
