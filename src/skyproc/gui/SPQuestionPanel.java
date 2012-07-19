/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import lev.gui.LSaveFile;
import lev.gui.LTextPane;

/**
 *
 * @author Justin Swanson
 */
public abstract class SPQuestionPanel extends SPSettingPanel {
    protected LTextPane question;

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     */
    public SPQuestionPanel(SPMainMenuPanel parent_,String title, Color headerColor) {
	super(parent_, title, headerColor);
    }

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     * @param saveFile_
     */
    public SPQuestionPanel(SPMainMenuPanel parent_,String title, Color headerColor, LSaveFile saveFile_) {
	super(parent_, title, headerColor, saveFile_);
    }

    @Override
    protected void initialize() {
	super.initialize();

	question = new LTextPane(settingsPanel.getWidth() - 20, 20, SUMGUI.light);
	question.setEditable(false);
	setPlacement(question);
	Add(question);

	alignRight();
    }

    public void setQuestionFont (Font f) {
	question.setFont(f);
    }

    public void setQuestionCentered() {
	question.setCentered();
    }

    public void setQuestionColor(Color c) {
	question.setForeground(c);
    }

    public void setQuestionText(String t) {
	question.setText(t);
	question.setSize(question.getWidth(), (int) question.getPreferredSize().getHeight());
	updateLast(question);
    }
}