/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.SwingUtilities;

/**
 *
 * @author Justin Swanson
 */
public class LComponent extends Container {

    /**
     *
     * @param c
     */
    protected void Add(Component c) {
	c.setVisible(true);
	add(c);
    }

    /**
     * Centers the calling component inside container c, at the height y. Does
     * not take c's X coords into account, as its assumed the calling component
     * is added to the container c.
     *
     * @param c Component to center to in the x direction.
     * @param y Height to be placed at.
     */
    public void centerIn(final Component c, final int y) {
	final Component thisC = this;
	setLocation(c.getWidth() / 2 - thisC.getWidth() / 2, y);
    }

    /**
     * Centers calling component to the horizontal center of component c.
     *
     * @param c Component to center on horizontally
     * @param y The Y position to be placed at.
     */
    public void centerOn(final Component c, final int y) {
	final Component thisC = this;
	setLocation(c.getX() + c.getWidth() / 2 - thisC.getWidth() / 2, y);
    }

    /**
     * Centers calling component to the vertical center of component c.
     *
     * @param x The X position to be placed at.
     * @param c Component to center on vertically.
     */
    public void centerOn(final int x, final Component c) {
	final Component thisC = this;
	setLocation(x, c.getY() + c.getHeight() / 2 - thisC.getHeight() / 2);
    }
    
    public void putUnder (final Component c, final int x, final int yOffset) {
	setLocation(x, c.getY() + c.getHeight() + yOffset);
    }
}
