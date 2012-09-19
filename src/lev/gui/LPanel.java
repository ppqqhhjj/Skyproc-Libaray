/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 *
 * @author Justin Swanson
 */
public class LPanel extends JPanel implements Scrollable {

    /**
     *
     */
    public LPanel () {
	setLayout(null);
	super.setSize(1,1);
	setOpaque(false);
	super.setVisible(true);
    }

    /**
     *
     * @param r
     */
    public LPanel (Rectangle r) {
	this();
	setBounds(r);
    }

    /**
     *
     * @param x
     * @param y
     */
    public LPanel (int x, int y){
	this();
	setLocation(x,y);
    }

    /**
     *
     * @param input
     */
    public void Add(Component input) {
	input.setVisible(true);
	add(input);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(300,300);
    }

    public void remeasure (Dimension size) {
	setSize(size);
    }
}