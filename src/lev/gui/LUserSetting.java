/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public abstract class LUserSetting<T> extends LHelpComponent {

    public Enum saveTie;
    public LSaveFile save;
    public LLabel titleLabel;

    public LUserSetting(String text) {
	super(text);
    }

    public LUserSetting(String text, Font label, Color shade) {
	this(text);
	titleLabel = new LLabel(text, label, shade);
	add(titleLabel);
    }

    public void tie(Enum setting, LSaveFile saveFile, LHelpPanel help_, boolean hoverListener) {
	tie(setting, saveFile);
	help = help_;
	setHelpInfo(setting, saveFile);
	addHelpHandler(hoverListener);
    }

    public void tie(Enum setting, LSaveFile saveFile) {
	saveTie = setting;
	save = saveFile;
	save.tie(setting, this);
	revertTo(save.saveSettings);
	addUpdateHandlers();
    }

    public abstract void addUpdateHandlers();

    public abstract boolean revertTo(Map<Enum, Setting> m);

    public Boolean isTied() {
	return (saveTie != null);
    }

    public abstract T getValue();

    public void update() {
	if (isTied()) {
	    save.set(saveTie, getValue());
	}
    }

    public abstract void highlightChanged();

    public abstract void clearHighlight();

    protected class UpdateHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
	    update();
	}
    }

    protected class UpdateChangeHandler implements ChangeListener {

	@Override
	public void stateChanged(ChangeEvent event) {
	    update();
	}
    }

    protected class UpdateCaretHandler implements CaretListener {

	@Override
	public void caretUpdate(CaretEvent event) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    update();
		}
	    });

	}
    }

    @Override
    public String getName() {
	return title;
    }
}
