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
    String previous = "";

    public LComboBox (String title_) {
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

    public void addActionListener(ActionListener a) {
        box.addActionListener(a);
    }

    public Object getSelectedItem() {
        return box.getSelectedItem();
    }

    public void removeAllItems() {
        box.removeAllItems();
    }

    public void addItem (Object o) {
        box.addItem(o);
    }

    public void setSelectedIndex (int in) {
        if (box.getItemCount() <= in)
            box.setSelectedIndex(box.getItemCount() - 1);
        else if (in < 0)
            box.setSelectedIndex(0);
        else
            box.setSelectedIndex(in);
    }

    public void switchTo(Object o) {
        for (int i = 0 ; i < box.getItemCount() ; i++) {
            if (box.getItemAt(i).toString().equals(o.toString()))
                setSelectedIndex(i);
        }
    }

    public void savePrevious () {
        if (box.getSelectedItem() != null)
            previous = box.getSelectedItem().toString();
        else
            previous = "";
    }

    public void switchToPrevious () {
        switchTo(previous);
    }

    @Override
    public void addHelpHandler() {
        box.addFocusListener(new HelpFocusHandler());
    }

    @Override
    public void addUpdateHandlers() {
	box.addActionListener(new UpdateHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    int cur = box.getSelectedIndex();
 	    box.setSelectedIndex(m.get(saveTie).getInt());
	    if (cur != box.getSelectedIndex())
		return false;
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
