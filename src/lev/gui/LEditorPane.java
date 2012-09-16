/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.View;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Justin Swanson
 */
public class LEditorPane extends LComponent {

    JEditorPane pane;

    public LEditorPane() {
	pane = new JEditorPane();
	pane.setEditable(false);
	pane.setVisible(true);
	setVisible(true);
	add(pane);
    }

    public void addHyperLinkListener(HyperlinkListener h) {
	pane.addHyperlinkListener(h);
    }

    public void setContentType(String s) {
	pane.setContentType(s);
    }

    public String getText() {
	return pane.getText();
    }

    public void setText(String s) {
	pane.setText(s);
    }

    @Override
    public void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	pane.setSize(x, y);
    }

    public void setSize(int x) {
	setSize(x, getHeight(x));
    }

    @Override
    public Dimension getPreferredSize() {
	return pane.getPreferredSize();
    }

    public int getHeight(int width) {
	View v = pane.getUI().getRootView(pane);
	v.setSize(width, Integer.MAX_VALUE);
	int preferredHeight = (int) v.getPreferredSpan(View.Y_AXIS);
	return preferredHeight;
    }

    public int getWidth(int height) {
	View v = pane.getUI().getRootView(pane);
	v.setSize(Integer.MAX_VALUE, height);
	int preferredWidth = (int) v.getPreferredSpan(View.X_AXIS);
	return preferredWidth;
    }

    public void setOpaque(boolean on) {
	pane.setOpaque(on);
    }
}