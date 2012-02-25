/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Justin Swanson
 */
public class LComponent extends Container {

    protected void Add(Component c) {
	c.setVisible(true);
	add(c);
    }

    public void centerIn (Component c, int y) {
        setLocation(c.getWidth() / 2 - this.getWidth() / 2, y);
    }

    public void centerOn (Component c, int y) {
        setLocation(c.getX() + c.getWidth() / 2 - this.getWidth() / 2, y);
    }

    public void centerOn (int x, Component c) {
        setLocation(x, c.getY() + c.getHeight() / 2 - this.getHeight() / 2);
    }
}
