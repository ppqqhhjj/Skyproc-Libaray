/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Justin Swanson
 */
public abstract class LHelpComponent extends LComponent {

    LHelpPanel help = null;
    String helpPrefix = "";
    String helpInfo = "";
    String title;

    public LHelpComponent(String text) {
        title = text;
    }

    public abstract void addHelpHandler();

    public void updateHelp() {
        if (help != null) {
            help.setSetting(helpPrefix + title);
            help.setContent(helpInfo);
            help.setSettingPos(getY() + getHeight() / 2);
            help.textVisible(true);
        }
    }

    protected class HelpActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateHelp();
                }
            });

        }

    }

    protected class HelpFocusHandler implements FocusListener {

        @Override
        public void focusGained(FocusEvent event) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateHelp();
                }
            });

        }

        @Override
        public void focusLost(FocusEvent event) {
//            SwingUtilities.invokeLater(new Runnable() {
//
//                public void run() {
//                    if (help != null) {
//                        help.textVisible(false);
//                    }
//                }
//            });

        }
    }

    protected class HelpMouseHandler implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	    updateHelp();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	    updateHelp();
	}

    }

    class HelpListHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateHelp();
                }
            });
        }
    }

    class HelpChangeHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateHelp();
                }
            });
        }
    }

    public void addHelpPrefix(String input) {
        helpPrefix = input + " ";
    }

    public void setHelpInfo(String info) {
        helpInfo = info;
    }

    public void setHelpInfo(Enum setting, LSaveFile save) {
	if (save.helpInfo.get(setting) != null) {
	    setHelpInfo((String) save.helpInfo.get(setting));
	} else {
	    setHelpInfo("...");
	}
    }

    public void linkTo(Enum setting, LSaveFile save, LHelpPanel help_) {
        help = help_;
        setHelpInfo(setting, save);
        addHelpHandler();
    }
}
