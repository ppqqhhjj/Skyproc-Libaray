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
    
    public LTextField (String title_, Font font, Color shade) {
        super(title_, font, shade);
        field = new JTextField();
        add(field);
	setSize(275, 22);
	titleLabel.addShadow();
	field.setVisible(true);
	setVisible(true);
    }
    
    @Override
    final public void setSize(int x, int y) {
        super.setSize(x, y);
        field.setSize(x - titleLabel.getWidth() - 10, y);
	field.setLocation(titleLabel.getWidth() + 10, 0);
    }
    
    public void addActionListener(ActionListener a) {
        field.addActionListener(a);
    }

    @Override
    public void addUpdateHandlers() {
	field.addActionListener(new UpdateHandler());
    }
    
    public void setText(String s) {
	field.setText(s);
    }
    
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

    @Override
    public void addHelpHandler(boolean hoverListener) {
	field.addFocusListener(new HelpFocusHandler());
	if (hoverListener) {
	    field.addMouseListener(new HelpMouseHandler());
	}
    }

}
