/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Justin Swanson
 */
public class LFonts {

    public static Font Typo3(float size) throws FontFormatException, IOException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("Typo3-Medium.ttf").openStream());
        return font.deriveFont(size);
    }

    public static Font Neuropol (float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("NEUROPOL.ttf").openStream());
        return font.deriveFont(Font.PLAIN, size);
    }

    public static Font OptimusPrinceps (float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("OptimusPrincepsSemiBold.ttf").openStream());
        return font.deriveFont(size);
    }

    public static Font Oleo (float size) throws FontFormatException, IOException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("OLEO.ttf").openStream());
        return font.deriveFont(Font.PLAIN, size);
    }
}
