/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.util.Map;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public class LNumericSetting extends LUserSetting<Integer> {

    LSpinner setting;

    public LNumericSetting(String text, int size, int min, Integer max, int step, Color c, Enum s, LHelpPanel help) {
        super(text, new Font("Serif", Font.BOLD, size), c);
        setting = new LSpinner(text, min, min, max, step, Integer.toString(max).length());
        int spacing = 10;
        titleLabel.addShadow();
        if (titleLabel.getHeight() < setting.getHeight()) {
            titleLabel.setLocation(titleLabel.getX(), setting.getHeight() / 2 - titleLabel.getHeight() / 2);
            setSize(1, setting.getHeight());
        } else {
            setting.setLocation(setting.getX(), titleLabel.getHeight() / 2 - setting.getHeight() / 2);
            setSize(1, titleLabel.getHeight());
        }
        setting.setLocation(titleLabel.getWidth() + spacing, setting.getY());

        Add(setting);
        Add(titleLabel);

        addUpdateHandlers();
        setSize(titleLabel.getWidth() + setting.getWidth() + spacing, getHeight());
        setLocation(getX() - getWidth(), getY());
        setVisible(true);
    }

    public void setValue(String s) {
        setValue(Integer.parseInt(s));
    }

    public void setValue(int i) {
        setting.setValue(i);
    }

    public void setValue(double d) {
        setValue(Double.toString(d));
    }

    @Override
    public void tie(Enum s, LSaveFile save_, LHelpPanel help_) {
	super.tie(s, save_, help_);
	setting.tie(s, save_, help_);
    }

    @Override
    public void tie(Enum s, LSaveFile save_) {
	super.tie(s, save_);
	setting.tie(s, save_);
    }

    @Override
    public Integer getValue() {
        return (Integer) setting.getValue();
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
        if (isTied()) {
            return setting.revertTo(m);
        }
	return true;
    }

    @Override
    public void addHelpHandler() {
        setting.addFocusListener(new HelpFocusHandler());
    }

    @Override
    public final void addUpdateHandlers() {
	setting.addChangeListener(new UpdateChangeHandler());
    }

    @Override
    public synchronized void addFocusListener(FocusListener arg0) {
	setting.addFocusListener(arg0);
    }

    public void addChangeListener(ChangeListener c) {
        setting.addChangeListener(c);
    }

    public void setColor(Color c) {
        titleLabel.setForeground(c);
    }

    @Override
    public void highlightChanged() {
	setting.highlightChanged();
    }

    @Override
    public void clearHighlight() {
	setting.clearHighlight();
    }
}