/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author Justin Swanson
 */
public class LFrame extends JFrame {

    protected LImagePane background;

    public LFrame (String title) {
	super(title);
	setLayout(null);
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	background = new LImagePane();
	this.getContentPane().add(background);
    }

    @Override
    public void setBackground(Color c) {
	getContentPane().setBackground(c);
    }

    public Dimension getRealSize() {
	return new Dimension(getRealWidth(), getRealHeight());
    }

    public int getRealWidth() {
	return getWidth() - 16;
    }

    public int getRealHeight () {
	return getHeight() - 38;
    }

    public Point defaultLocation() {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int y = 5;
	int x = screen.width / 2 - getWidth() / 2;
	if (x < 5) {
	    x = 5;
	}
	return new Point(x, y);
    }

    public void remeasure () {
	background.setMaxSize(getRealWidth(), 0);
    }
}
