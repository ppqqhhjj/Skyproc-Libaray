/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of embedded fonts that can be used.
 *
 * @author Justin Swanson
 */
public class LFonts {

    /**
     *
     * @param size
     * @return
     */
    public static Font Typo3(float size) {
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("Typo3-Medium.ttf").openStream());
	    return font.deriveFont(size);
	} catch (IOException ex) {
	} catch (FontFormatException ex) {
	}
	return new Font("Serif", 3, 3);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font Neuropol(float size){
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("NEUROPOL.ttf").openStream());
	    return font.deriveFont(Font.PLAIN, size);
	} catch (IOException ex) {
	} catch (FontFormatException ex) {
	}
	return new Font("Serif", 3, 3);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font OptimusPrinceps(float size) {
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("OptimusPrincepsSemiBold.ttf").openStream());
	    return font.deriveFont(size);
	} catch (IOException ex) {
	} catch (FontFormatException ex) {
	}
	return new Font("Serif", 3, 3);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font Oleo(float size) {
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource("OLEO.ttf").openStream());
	    return font.deriveFont(Font.PLAIN, size);
	} catch (IOException ex) {
	} catch (FontFormatException ex) {
	}
	return new Font("Serif", 3, 3);
    }
}
