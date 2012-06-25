/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A customized JPanel that has a background image.
 * @author Justin Swanson
 */
public class LImagePane extends JPanel {

    private BufferedImage img;
    int IMG_WIDTH = 0;
    int IMG_HEIGHT = 0;

    /**
     * Creates an image pane with no set image.
     */
    public LImagePane() {
        setLayout(null);
	setOpaque(false);
    }

    /**
     *
     * @param img
     * @throws IOException
     */
    public LImagePane(File img) throws IOException {
        this();
        setImage(ImageIO.read(img));
    }

    /**
     *
     * @param img
     * @throws IOException
     */
    public LImagePane(String img) throws IOException {
        this();
        setImage(ImageIO.read(new File(img)));
    }

    /**
     *
     * @param img
     */
    public LImagePane(BufferedImage img) {
        this();
        setImage(img);
    }

    /**
     *
     * @param url
     * @throws IOException
     */
    public LImagePane(URL url) throws IOException {
        this();
        setImage(url);
    }

    /**
     *
     * @param originalImage
     */
    final public void setImage(BufferedImage originalImage) {
	if (originalImage == null) {
	    return;
	}
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
        revalidate();
	repaint();
    }

    /**
     *
     * @param in
     * @throws IOException
     */
    final public void setImage(File in) throws IOException {
        setImage(ImageIO.read(in));
    }

    /**
     *
     * @param url
     * @throws IOException
     */
    final public void setImage(URL url) throws IOException {
	setImage(ImageIO.read(url));
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setMaxSize(int x, int y) {
        IMG_WIDTH = x;
        IMG_HEIGHT = y;
        setImage(img);
    }

    Dimension calcSize(double x, double y) {
	return Lg.calcSize(x, y, IMG_WIDTH, IMG_HEIGHT);
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
    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }


}
