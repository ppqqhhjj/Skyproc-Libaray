/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JTextField;

/**
 *
 * @author Justin Swanson
 */
public class LTextField extends LUserSetting<String> {

    JTextField field;

    /**
     *
     * @param title_
     * @param font
     * @param shade
     */
    public LTextField(String title_, Font font, Color shade) {
	super(title_, font, shade);
	init();
    }

    public LTextField(String title_) {
	super(title_);
	init();
    }

    final void init() {
	field = new JTextField();
	add(field);
	setSize(275, 22);
	if (titleLabel != null) {
	    titleLabel.addShadow();
	}
	field.setVisible(true);
	setVisible(true);
    }

    @Override
    final public void setSize(int x, int y) {
	super.setSize(x, y);
	if (titleLabel != null) {
	    field.setSize(x - titleLabel.getWidth() - 10, y);
	    field.setLocation(titleLabel.getWidth() + 10, 0);
	} else {
	    field.setSize(x, y);
	}
    }

    /**
     *
     * @param a
     */
    public void addActionListener(ActionListener a) {
	field.addActionListener(a);
    }

    @Override
    protected void addUpdateHandlers() {
	field.addActionListener(new UpdateHandler());
    }

    /**
     *
     * @param s
     */
    public void setText(String s) {
	field.setText(s);
    }

    /**
     *
     * @return
     */
    public String getText() {
	return field.getText();
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    String cur = field.getText();
	    field.setText(m.get(saveTie).getStr());
	    if (!cur.equals(field.getText())) {
		return false;
	    }
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getValue() {
	return field.getText();
    }

    @Override
    public void highlightChanged() {
	field.setBackground(new Color(224, 121, 147));
    }

    @Override
    public void clearHighlight() {
	field.setBackground(Color.white);
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    public void addHelpHandler(boolean hoverListener) {
	field.addFocusListener(new HelpFocusHandler());
	if (hoverListener) {
	    field.addMouseListener(new HelpMouseHandler());
	}
    }
}
