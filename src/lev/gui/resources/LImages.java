/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui.resources;

import java.net.URL;

/**
 * Collection of embedded images to be used in GUIs.
 * @author Justin Swanson
 */
public class LImages {
    /**
     *
     * @return The multipurpose background image used in DefaultGUI.
     */
    public static URL multipurpose() {
	return LImages.class.getResource("multipurpose.png");
    }

    /**
     * Returns an arrow graphic pointing left or right.
     * @param leftArrow
     * @return
     */
    public static URL arrow(boolean leftArrow) {
	if (leftArrow)
	    return LImages.class.getResource("ArrowLeft.png");
	else
	    return LImages.class.getResource("ArrowRight.png");
    }
}
