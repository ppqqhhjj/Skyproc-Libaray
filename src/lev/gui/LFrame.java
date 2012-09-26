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
    private static int marginX = 16;
    private static int marginY = 38;


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

    public void setRealSize(int x, int y) {
	setSize(x + marginX, y + marginY);
    }

    public void setRealSize(Dimension size) {
	setRealSize(size.width, size.height);
    }

    public int getRealWidth() {
	return getWidth() - marginX;
    }

    public int getRealHeight () {
	return getHeight() - marginY;
    }

    public Point defaultLocation() {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int y = 5;
	int x = screen.width / 2 - getWidth() / 2;
	if (x < 0) {
	    x = 0;
	}
	return new Point(x, y);
    }

    public Point centerScreen() {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int y = screen.height / 2 - getHeight() / 2;
	int x = screen.width / 2 - getWidth() / 2;
	if (x < 0) {
	    x = 0;
	}
	if (y < 0) {
	    y = 0;
	}
	return new Point(x, y);
    }

    public void remeasure () {
	background.setMaxSize(getRealWidth(), 0);
    }
}
