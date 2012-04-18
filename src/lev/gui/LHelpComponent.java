/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.event.*;
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

    public LHelpPanel help = null;
    String helpPrefix = "";
    String helpInfo = "";
    public String title;

    public LHelpComponent(String text) {
        title = text;
    }

    public abstract void addHelpHandler(boolean hoverListener);

    public void updateHelp() {
        if (help != null) {
            help.setSetting(helpPrefix + title);
            help.setContent(helpInfo);
            help.setSettingPos(getY() + getHeight() / 2);
            help.textVisible(true);
        }
    }

    public class HelpActionHandler implements ActionListener {

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

    public class HelpFocusHandler implements FocusListener {

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

    public class HelpMouseHandler implements MouseListener {

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

    public void linkTo(Enum setting, LSaveFile save, LHelpPanel help_, boolean hoverListener) {
        help = help_;
        setHelpInfo(setting, save);
        addHelpHandler(hoverListener);
    }
}
