/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JComboBox;
import lev.gui.LHelpComponent.HelpFocusHandler;

/**
 *
 * @author Justin Swanson
 */
public class LComboBox extends LUserSetting<Integer> {

    JComboBox box;
    String previous;

    /**
     *
     * @param title_
     */
    public LComboBox(String title_) {
	super(title_);
	box = new JComboBox();
	add(box);
	box.setVisible(true);
	setVisible(true);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	box.setSize(x, y);
    }

    /**
     *
     * @param a
     */
    public void addActionListener(ActionListener a) {
	box.addActionListener(a);
    }

    /**
     *
     * @return
     */
    public Object getSelectedItem() {
	return box.getSelectedItem();
    }

    /**
     *
     */
    public void removeAllItems() {
	box.removeAllItems();
    }

    /**
     * 
     * @param o
     */
    public void addItem(Object o) {
	box.addItem(o);
    }

    /**
     *
     * @param in
     */
    public void setSelectedIndex(int in) {
	if (box.getItemCount() <= in) {
	    box.setSelectedIndex(box.getItemCount() - 1);
	} else if (in < 0) {
	    box.setSelectedIndex(0);
	} else {
	    box.setSelectedIndex(in);
	}
    }

    /**
     *
     * @param o
     */
    public void switchTo(Object o) {
	for (int i = 0; i < box.getItemCount(); i++) {
	    if (box.getItemAt(i).toString().equals(o.toString())) {
		setSelectedIndex(i);
	    }
	}
    }

    /**
     *
     */
    public void savePrevious() {
	if (box.getSelectedItem() != null) {
	    previous = box.getSelectedItem().toString();
	} else {
	    previous = null;
	}
    }

    /**
     *
     */
    public void switchToPrevious() {
	if (previous != null) {
	    switchTo(previous);
	}
    }

    /**
     *
     * @param mouseListener
     */
    @Override
    public void addHelpHandler(boolean mouseListener) {
	box.addFocusListener(new HelpFocusHandler());
	if (mouseListener) {
	    box.addMouseListener(new HelpMouseHandler());
	}
    }

    @Override
    protected void addUpdateHandlers() {
	box.addActionListener(new UpdateHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    int cur = box.getSelectedIndex();
	    box.setSelectedIndex(m.get(saveTie).getInt());
	    if (cur != box.getSelectedIndex()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public Integer getValue() {
	return box.getSelectedIndex();
    }

    @Override
    public void highlightChanged() {
	box.setBackground(new Color(224, 121, 147));
    }

    @Override
    public void clearHighlight() {
	box.setBackground(null);
    }
}
