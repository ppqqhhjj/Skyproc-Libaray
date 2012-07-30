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
import javax.swing.JOptionPane;
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
	    SPSettingPanel cancel, SPSettingPanel back) {
	super(parent_, title, headerColor);
	this.cancelPanel = cancel;
	this.backPanel = back;
    }

    @Override
    protected void initialize() {
	super.initialize();

	question = new LTextPane(settingsPanel.getWidth() - 20, 20, SUMGUI.light);
	question.setEditable(false);
	question.centerIn(settingsPanel, header.getBottom() + 10);
	settingsPanel.add(question);

	last = new Point(last.x, question.getBottom());

	cancelButton = new LButton("Cancel");
	cancelButton.setLocation(15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	cancelButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testCancel()) {
		    int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Confirm Cancel",
			    JOptionPane.YES_NO_OPTION);
		    if (answer == JOptionPane.YES_OPTION) {
			onCancel();
			cancelPanel.open();
		    }
		}
	    }
	});
	settingsPanel.add(cancelButton);

	backButton = new LButton("Back");
	backButton.setLocation(settingsPanel.getWidth() / 2 - backButton.getWidth() / 2, settingsPanel.getHeight() - backButton.getHeight() - 10);
	backButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testBack()) {
		    onBack();
		    backPanel.open();
		}
	    }
	});
	settingsPanel.add(backButton);

	nextButton = new LButton("Next");
	nextButton.setLocation(settingsPanel.getWidth() - nextButton.getWidth() - 15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	nextButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testNext()) {
		    onNext();
		    nextPanel.open();
		}
	    }
	});
	settingsPanel.add(nextButton);

	setCancel(cancelPanel);
	setBack(backPanel);
	setNext(nextPanel);
    }

    public void setCancel(SPSettingPanel in) {
	cancelPanel = in;
	cancelButton.setVisible(in != null);
    }

    public void setBack(SPSettingPanel in) {
	backPanel = in;
	backButton.setVisible(in != null);
    }

    public void setNext(SPSettingPanel in) {
	nextPanel = in;
	nextButton.setVisible(in != null);
    }

    public boolean testCancel() {
	return true;
    }

    public boolean testBack() {
	return true;
    }

    public boolean testNext() {
	return true;
    }

    public void onCancel() {
    }

    public void onBack() {
    }

    public void onNext() {
    }

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