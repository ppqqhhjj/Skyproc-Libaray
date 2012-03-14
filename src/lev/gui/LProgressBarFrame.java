/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import lev.gui.resources.LImages;

/**
 *
 * @author Justin Swanson
 */
public class LProgressBarFrame extends JFrame {

    LProgressBar bar;
    LLabel title;
    Dimension correctLocation = new Dimension(0, 0);
    Dimension GUIsize = new Dimension(250, 72);
    LImagePane backgroundPanel;
    JFrame guiRef;
    static int closeOp = JFrame.DO_NOTHING_ON_CLOSE;

    public LProgressBarFrame(final Font header, final Color headerC, final Font footer, final Color footerC) {
	bar = new LProgressBar(150, 15, footer, footerC);
	addComponents(header, headerC);
	moveToCorrectLocation();
	setDefaultCloseOperation(closeOp);
    }

    final void addComponents(Font header, Color headerC) {
	try {
	    backgroundPanel = new LImagePane(LImages.multipurpose());
	} catch (IOException ex) {
	}
	backgroundPanel.setVisible(true);
	add(backgroundPanel);

	setSize(GUIsize);
	setResizable(false);
	setLayout(null);

	title = new LLabel("Please wait...", header, headerC);
	setSize(250, 72);
	bar.setLocation(getWidth() / 2 - bar.getWidth() / 2, getHeight() / 2 - bar.getHeight() / 2);
	title.setLocation(getWidth() / 2 - title.getWidth() / 2, 2);

	setSize(250, 100);
	backgroundPanel.add(bar);
	backgroundPanel.add(title);
	setResizable(false);
	setVisible(true);
    }

    public static void setExitOnClose() {
	closeOp = JFrame.DISPOSE_ON_CLOSE;
    }

    public void setBarMax(final int max, final String reason) {
	bar.setBarMax(max, reason);
    }

    public void incrementBarValue() {
	bar.incrementBarValue();
    }

    public void setCorrectLocation(int x, int y) {
	correctLocation = new Dimension(x, y);
    }

    public final void moveToCorrectLocation() {
	Rectangle r = guiRef.getBounds();
	setLocation(r.x + correctLocation.width, r.y + correctLocation.height);
    }

    public void switchFooter(final String input_) {
	bar.switchFooter(input_);
    }

    public void open() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		moveToCorrectLocation();
		setVisible(true);
	    }
	});
    }

    public void open(ChangeListener c) {
	open();
	setDoneListener(c);
    }

    private void setDoneListener(ChangeListener c) {
	bar.setDoneListener(c);
    }

    public void close() {
	setVisible(false);
//	bar.done.setSelected(!bar.done.isSelected());
    }

    public void setGUIref(JFrame ref) {
	guiRef = ref;
    }
}