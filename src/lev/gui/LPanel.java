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

    public LPanel () {
	setLayout(null);
	setSize(1,1);
	setOpaque(false);
	setVisible(true);
    }

    public LPanel (Rectangle r) {
	this();
	setBounds(r);
    }

    public LPanel (int x, int y){
	this();
	setLocation(x,y);
    }

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

}