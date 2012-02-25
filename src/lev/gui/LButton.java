/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 *
 * @author Justin Swanson
 */
public class LButton extends LComponent {

    JButton button;

    public LButton (String title) {
//        super(title);
	button = new JButton();
	button.setText(title);
	button.setMargin(new Insets(0,2,0,2));
	button.setSize(button.getPreferredSize());
	button.setFocusable(false);
	button.setVisible(true);
	button.setLocation(0,0);
	super.setSize(button.getSize());
	setVisible(true);
	add(button);
    }

    public LButton (String title, Dimension size) {
        this(title);
        setSize(size);
    }

    public LButton (String title, Dimension size, Point location) {
	this(title);
	setSize(size);
	setLocation(location);
    }

    public LButton (String title, Point location) {
	this(title);
	setLocation(location);
    }

    @Override
    public final void setSize (Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public final void setSize (int x, int y) {
	button.setSize(x,y);
	super.setSize(x,y);
    }

    public void addActionListener(ActionListener l) {
	button.addActionListener(l);
    }

    public void setActionCommand (String s) {
        button.setActionCommand(s);
    }

    public JButton getSource () {
        return button;
    }

    @Override
    public boolean requestFocusInWindow () {
	return button.requestFocusInWindow();
    }

//    @Override
//    public void addHelpHandler() {
//        button.addActionListener(new HelpActionHandler());
//    }

    @Override
    public synchronized void addMouseListener(MouseListener arg0) {
	button.addMouseListener(arg0);
    }

    public String getText() {
        return button.getText();
    }

    public void setText(String in) {
        button.setText(in);
    }
}
