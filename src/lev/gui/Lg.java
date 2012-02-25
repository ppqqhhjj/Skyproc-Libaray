/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levgui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author Justin Swanson
 */
public class Lg {

    static public BufferedImage resizeImage(BufferedImage originalImage, Dimension size) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(size.width, size.height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, size.width, size.height, null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage resizeImageWithHint(BufferedImage originalImage, Dimension size) {
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

    static public Dimension calcSize(double x, double y, int maxX, int maxY) {
        double xMod = 1.0 * maxX / x;
        double yMod = 1.0 * maxY / y;
        double mod = (xMod <= yMod) ? xMod : yMod;
        if (mod < 1) {
            x = x * mod;
            y = y * mod;
        }
        return new Dimension((int) x, (int) y);
    }
}
