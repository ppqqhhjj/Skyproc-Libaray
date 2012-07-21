/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import lev.gui.LButton;
import lev.gui.LTextPane;

/**
 *
 * @author Justin Swanson
 */
public abstract class SPQuestionPanel extends SPSettingPanel {

    protected LTextPane question;
    protected LButton cancelButton;
    protected SPSettingPanel cancelPanel;
    protected LButton backButton;
    protected SPSettingPanel backPanel;
    protected LButton nextButton;
    protected SPSettingPanel nextPanel;

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     */
    public SPQuestionPanel(SPMainMenuPanel parent_, String title, Color headerColor,
	    SPSettingPanel cancel, SPSettingPanel back, SPSettingPanel next) {
	super(parent_, title, headerColor);
	this.cancelPanel = cancel;
	this.backPanel = back;
	this.nextPanel = next;
    }

    @Override
    protected void initialize() {
	super.initialize();

	question = new LTextPane(settingsPanel.getWidth() - 20, 20, SUMGUI.light);
	question.setEditable(false);
	last = new Point(last.x, header.getY() + header.getHeight() + 10 - spacing * 2);
	setPlacement(question);
	Add(question);

	if (cancelPanel != null) {
	    cancelButton = new LButton("Cancel");
	    cancelButton.setLocation(15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	    cancelButton.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
		    onCancel();
		    cancelPanel.open();
		}
	    });
	    Add(cancelButton);
	}

	if (backPanel != null) {
	    backButton = new LButton("Back");
	    backButton.setLocation(settingsPanel.getWidth() / 2 - backButton.getWidth() / 2, settingsPanel.getHeight() - backButton.getHeight() - 10);
	    backButton.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
		    onBack();
		    backPanel.open();
		}
	    });
	    Add(backButton);
	}

	if (nextPanel != null) {
	    nextButton = new LButton("Next");
	    nextButton.setLocation(settingsPanel.getWidth() - nextButton.getWidth() - 15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	    nextButton.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
		    onNext();
		    nextPanel.open();
		}
	    });
	    Add(nextButton);
	}
    }

    public void onCancel (){
    };

    public void onBack (){
    };

    public void onNext (){
    };

    public void setQuestionFont(Font f) {
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

    public String getQuestionText() {
	return question.getText();
    }
}