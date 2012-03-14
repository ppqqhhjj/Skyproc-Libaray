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
public class LProgressBar extends LComponent implements LProgressBarInterface {

    JProgressBar bar;
    LLabel footer;
    boolean centered = true;
    LCheckBox done = new LCheckBox("", LFonts.Typo3(1), Color.BLACK);

    public LProgressBar(final int width, final int height, final Font footerF, final Color footerC) {
	bar = new JProgressBar(0, 100);
	bar.setSize(width, height);
	bar.setLocation(bar.getWidth() / 2, 0);
	bar.setStringPainted(true);
	bar.setVisible(true);

	footer = new LLabel(". . .", footerF, footerC);
	footer.setLocation(bar.getX() + bar.getWidth() / 2 - footer.getWidth() / 2, bar.getY() + bar.getHeight() + 10);

	setSize(bar.getWidth() * 2, footer.getY() + footer.getHeight());
	add(bar);
	add(footer);
	setVisible(true);
    }

    public void setFooterOffset(final int y) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		footer.setLocation(footer.getX(), bar.getY() + bar.getHeight() + y);
	    }
	});
    }

    public void setCentered(boolean centered) {
	this.centered = centered;
    }

    @Override
    public void setMax(final int max, final String reason) {
	setMax(max);
	setStatus(reason);
    }

    @Override
    public void incrementBar() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue((bar.getValue()) + 1);
	    }
	});
    }

    @Override
    public void setStatus(final String input_) {
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

    @Override
    public void setMax(final int in) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue(0);
		bar.setMaximum(in);
	    }
	});
    }

    @Override
    public void reset() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue(0);
	    }
	});
    }

    @Override
    public void setBar (final int in) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		bar.setValue(in);
	    }
	});
    }
}