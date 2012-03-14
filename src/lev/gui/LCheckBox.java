/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lev.saving.Setting;

/**
 *
 * @author Justin Swanson
 */
public class LCheckBox extends LUserSetting<Boolean> {

    private JCheckBox cbox;

    ArrayList<LComponent> underlings = new ArrayList<LComponent>();

    public LCheckBox(String text, Font font, Color shade) {
	super(text, font, shade);
	int spacing = 5;

        titleLabel.addMouseListener(new ClickText());

	cbox = new JCheckBox();
	cbox.setSize(cbox.getPreferredSize());
	cbox.setOpaque(false);
	cbox.setLocation(titleLabel.getWidth() + spacing, 5);
	cbox.addChangeListener(new underlingChangeHandler());
	cbox.addActionListener(new underlingActionHandler());
	addUpdateHandlers();


	setSize(titleLabel.getWidth() + cbox.getWidth() + spacing, titleLabel.getHeight());
	setVisible(true);
	add(cbox);
    }

    public LCheckBox(String text, Font font, Color shade, Point location) {
	this(text, font, shade);
	setLocation(location);
    }

    public void addShadow() {
	titleLabel.addShadow();
    }

    @Override
    public void setFocusable (boolean focusable) {
	super.setFocusable(focusable);
	cbox.setFocusable(focusable);
    }

    public void setOffset(int offset) {
	cbox.setLocation(cbox.getX(), offset);
    }

    public void setSelected(Boolean b) {
	cbox.setSelected(b);
    }

    public Boolean isSelected() {
	return cbox.isSelected();
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    boolean cur = cbox.isSelected();
	    cbox.setSelected(m.get(saveTie).getBool());
	    if (cur != cbox.isSelected())
		return false;
	}
	return true;
    }

    public void addActionListener(ActionListener l) {
	cbox.addActionListener(l);
    }

    public void addChangeListener(ChangeListener l) {
	cbox.addChangeListener(l);
    }

    @Override
    public Boolean getValue() {
	return cbox.isSelected();
    }

    public void addAsUnderling(LComponent c) {
	underlings.add(c);
	visibilityUpdate();
    }

    private void visibilityUpdate() {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		for (LComponent c : underlings) {
		    c.setVisible(isSelected());
		}
	    }
	});

    }

    @Override
    public void highlightChanged() {
    }

    @Override
    public void clearHighlight() {
    }

    private class underlingChangeHandler implements ChangeListener {

	@Override
	public void stateChanged(ChangeEvent event) {
	    visibilityUpdate();
	}
    }

    private class underlingActionHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
	    visibilityUpdate();
	}
    }

    @Override
    public void addHelpHandler () {
	cbox.addFocusListener(new HelpFocusHandler());
    }

    @Override
    public final void addUpdateHandlers () {
	cbox.addActionListener(new UpdateHandler());
	cbox.addChangeListener(new UpdateChangeHandler());
    }

    public void removeChangeListener (ChangeListener c) {
	cbox.removeChangeListener(c);
    }

    public void setColor (Color c) {
        titleLabel.setForeground(c);
    }

    public class ClickText implements MouseListener {

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    cbox.requestFocusInWindow();
	    if (cbox.isSelected()) {
		cbox.setSelected(false);
	    } else {
		cbox.setSelected(true);
	    }
	}
    }
}
