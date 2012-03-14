/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import lev.gui.resources.LFonts;

/**
 *
 * @author Justin Swanson
 */
public class LProgressBar extends LComponent {

    JProgressBar bar;
    LLabel footer;
    boolean centered = true;
    LCheckBox done = new LCheckBox("", LFonts.Typo3(1), Color.BLACK);

    public LProgressBar(int width, int height, final Font footer, final Color footerC) {
	bar = new JProgressBar(width, height);
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		addComponents(footer, footerC);
	    }
	});
    }

    final void addComponents(Font footerF, Color footerC) {
	footer = new LLabel("Finishing importing your mods.", footerF, footerC);

	bar.setSize(150, 15);
	bar.setStringPainted(true);
	bar.setVisible(true);

	setSize(bar.getWidth() + 100, bar.getHeight() + 65);
	bar.setLocation(getWidth() / 2 - bar.getWidth() / 2, getHeight() / 2 - bar.getHeight() / 2);
	footer.setLocation(getWidth() / 2 - footer.getWidth() / 2, getHeight() - footer.getHeight() - 2);

	add(bar);
	add(footer);
    }

    public void setCentered(boolean centered) {
	this.centered = centered;
    }

    public void setBarMax(final int max, final String reason) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue(0);
		bar.setMaximum(max);
		switchFooter(reason);
	    }
	});
    }

    public void incrementBarValue() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue((bar.getValue()) + 1);
	    }
	});
    }

    public void switchFooter(final String input_) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		footer.setText(input_);
		if (centered) {
		    footer.setLocation(bar.getX() + bar.getWidth() / 2 - footer.getWidth() / 2, footer.getY());
		} else {
		    footer.setLocation(bar.getX(), footer.getY());
		}
	    }
	});
    }

    public void setDoneListener(ChangeListener c) {
	done = new LCheckBox("", done.getFont(), Color.BLACK);
	done.addChangeListener(c);
    }

}