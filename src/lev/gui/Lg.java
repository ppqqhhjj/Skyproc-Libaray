/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * GUI related nifty functions
 * @author Justin Swanson
 */
public class Lg {

    /**
     *
     * @param originalImage Image to resize
     * @param size size to convert to
     * @return Resized image
     */
    static public BufferedImage resizeImage(BufferedImage originalImage, Dimension size) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(size.width, size.height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, size.width, size.height, null);
        g.dispose();

        return resizedImage;
    }

    /**
     *
     * @param originalImage Image to resize
     * @param size size to convert to
     * @return Resized image
     */
    static public BufferedImage resizeImageWithHint(BufferedImage originalImage, Dimension size) {
	if (originalImage.getWidth() == size.width && originalImage.getHeight() == size.height) {
	    return originalImage;
	}

        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(size.width, size.height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, size.width, size.height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    /**
     * This function returns the minimum dimensions to fit inside (maxx,maxy) while
     * retaining the aspect ratio of the original (x,y)
     * @param x Original width
     * @param y Original height
     * @param maxX Max width
     * @param maxY Max height
     * @return New dimensions fitting inside limits, while retaining aspect ratio.
     */
    static public Dimension calcSize(double x, double y, int maxX, int maxY) {
	if (maxX == 0) {
	    maxX = Integer.MAX_VALUE;
	}
	if (maxY == 0) {
	    maxY = Integer.MAX_VALUE;
	}
        double xMod = 1.0 * maxX / x;
        double yMod = 1.0 * maxY / y;
        double mod = (xMod <= yMod) ? xMod : yMod;
        if (mod < 1) {
            x = x * mod;
            y = y * mod;
        }
        return new Dimension((int) x, (int) y);
    }

    static public Dimension calcSize(double x, double y, int minX, int minY, int maxX, int maxY) {
	Dimension max = calcSize(x,y,maxX,maxY);
	if (minX == 0 && minY == 0) {
	    return max;
	}
	double xMod = 1.0 * minX / x;
	double yMod = 1.0 * minY / y;

        return new Dimension((int) x, (int) y);
    }
}
