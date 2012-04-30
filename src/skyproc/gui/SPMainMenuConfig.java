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
 *
 * @author Justin Swanson
 */
public class SPMainMenuConfig extends LCheckBoxConfig {

    public SPMainMenuConfig(String title_, Boolean cBoxPresent, Boolean large, Color color, Point location, LSaveFile saveFile, Enum setting) {
        super(title_);
        save = saveFile;
	help = SPComplexGUI.helpPanel;
        saveTie = setting;
        setHelpInfo(saveTie, save);

        int size;
        if (large) {
            size = 20;
        } else {
            size = 16;
        }

        button = new LButton(buttonText);
        button.addActionListener(new UpdateHelpActionHandler());

        if (cBoxPresent) {
            cbox = new LSpecialCheckBox(title, new Font("Serif",Font.PLAIN, size), color, this);
            cbox.tie(setting, save, help, false);
            cbox.setFocusable(false);
            button.setLocation(new Point(cbox.getWidth() + spacing, 0));
        } else {
            titleLabel = new LLabel(title, new Font("Serif",Font.PLAIN, size), color);
            button.setLocation(new Point(titleLabel.getWidth() + spacing, 0));
        }

        if (!large) {
            if (cBoxPresent) {
                cbox.setOffset(2);
            }
            button.setLocation(button.getX(), button.getY() + 1);
        } else {
            button.setLocation(button.getX(), button.getY() + 4);
        }

        add(button);
        if (cBoxPresent) {
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
