/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import lev.gui.LButton;
import lev.gui.LCheckBoxConfig;
import lev.gui.LLabel;
import lev.gui.LSaveFile;

/**
 * An main menu GUI component that is used in SUMGUI.
 *
 * @author Justin Swanson
 */
public class SPMainMenuConfig extends LCheckBoxConfig {

    static int size = 20;

    /**
     * Creates a main menu GUI line tied to a savefile.
     *
     * @param title_ Text to display
     * @param checkbox Whether to include a checkbox
     * @param color Color to display
     * @param location Location of the component
     * @param saveFile Savefile to tie to
     * @param setting Setting to tie to
     */
    public SPMainMenuConfig(LLabel title_, boolean checkbox, Color color, Point location, LSaveFile saveFile, Enum setting) {
	super(title_.getText());
	boolean saveField = saveFile != null && setting != null;

	help = SUMGUI.helpPanel;
	if (saveField) {
	    save = saveFile;
	    saveTie = setting;
	    setHelpInfo(saveTie, save);
	}

	button = new LButton(buttonText);
	button.addActionListener(new UpdateHelpActionHandler());

	Font font = title_.getFont().deriveFont(Font.PLAIN, size);

	if (checkbox) {
	    cbox = new LSpecialCheckBox(title, font, color, this);
	    cbox.setOffset(5);
	    cbox.tie(setting, save, help, false);
	    cbox.setFocusable(false);
	    button.setLocation(new Point(cbox.getWidth() + spacing, 0));
	} else {
	    titleLabel = new LLabel(title, font, color);
	    button.setLocation(new Point(titleLabel.getWidth() + spacing, 0));
	}

	button.setLocation(button.getX(), button.getY() + 4);

	add(button);
	if (checkbox) {
	    add(cbox);
	    setLocation(location.x - button.getWidth() - cbox.getWidth() - spacing, location.y);
	    setSize(cbox.getWidth() + button.getWidth() + spacing, cbox.getHeight());
	} else {
	    add(titleLabel);
	    setLocation(location.x - button.getWidth() - titleLabel.getWidth() - spacing, location.y);
	    setSize(titleLabel.getWidth() + button.getWidth() + spacing, titleLabel.getHeight());
	}
    }

}
