/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.View;

/**
 *
 * @author Justin Swanson
 */
public class LEditorPane extends LComponent {

    JEditorPane text;

    LEditorPane() {
	text = new JEditorPane();
	text.setEditable(false);
	text.setVisible(true);
	setVisible(true);
	add(text);
    }

    public void addHyperLinkListener(HyperlinkListener h) {
	text.addHyperlinkListener(h);
    }

    public void setContentType(String s) {
	text.setContentType(s);
    }

    public String getText() {
	return text.getText();
    }

    public void setText(String s) {
	text.setText(s);
    }

    @Override
    public void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	text.setSize(x, y);
    }

    @Override
    public Dimension getPreferredSize() {
	return text.getPreferredSize();
    }

    public int getHeight(int width) {
	View v = text.getUI().getRootView(text);
	v.setSize(width, Integer.MAX_VALUE);
	int preferredHeight = (int) v.getPreferredSpan(View.Y_AXIS);
	return preferredHeight;
    }
}