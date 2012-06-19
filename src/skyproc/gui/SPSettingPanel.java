/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.SwingUtilities;
import lev.gui.*;

/**
 *
 * @author Justin Swanson
 */
public abstract class SPSettingPanel extends LPanel {

    /**
     * Button at the bottom center column that will iterate over each item
     * in the settings list and revert them to the default settings of the savefile field.
     */
    protected LButton defaults = new LButton("Set to Default");
    /**
     * Button at the bottom center column that will iterate over each item
     * in the settings list and revert them to the last saved settings of the savefile field.
     */
    protected LButton save = new LButton("Revert to Saved");
    /**
     * Reference to the Main Menu parent GUI object
     */
    protected SPMainMenuPanel parent;
    /**
     * Spacing to be used between settings
     */
    protected int spacing = 12;
    /**
     * Reference to the position of the last-added setting or component added using Add() or AddSetting()
     */
    protected Point last;
    /**
     * The top label
     */
    protected LLabel header;
    /**
     * List of all the setting components added with AddSetting()
     */
    protected ArrayList<LUserSetting> settings = new ArrayList<LUserSetting>();
    /**
     * Reference to the panel in the center column
     */
    protected LPanel settingsPanel;
    private ArrayList<Component> components = new ArrayList<Component>();
    private int rightMost = 0;
    /**
     * SaveFile reference used by defaults and saved buttons
     */
    protected LSaveFile saveFile;
    /**
     * Flag to symbolize Panel has been initialized and the components have been created and added.
     */
    protected boolean initialized = false;

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     */
    public SPSettingPanel(String title, SPMainMenuPanel parent_, Color headerColor) {
	super(SUMGUI.fullDimensions);
	parent = parent_;
	header = new LLabel(title, new Font("Serif", Font.BOLD, 26), headerColor);
    }

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     * @param saveFile_
     */
    public SPSettingPanel(String title, SPMainMenuPanel parent_, Color headerColor, LSaveFile saveFile_) {
	this(title, parent_, headerColor);
	saveFile = saveFile_;
    }

    /**
     * Function that creates all components and adds them to the GUI using
     * Add() or AddSetting().<br><br>
     * It should look like this:<br>
     * <i>if (super.initialize()) {<br>
     * <br>
     *    //... Your initializing code ...<br>
     * <br>
     *    return true;<br>
     * } else {<br>
     *    return false;<br>
     * }<br>
     *
     * @return Whether the GUI was just initialized.  False if it has already been.
     */
    public boolean initialize() {
	if (!initialized) {
	    settingsPanel = new LPanel(SUMGUI.middleDimensions);

	    setVisible(true);
	    int spacing = (settingsPanel.getWidth() - defaults.getWidth() - save.getWidth()) / 3;

	    defaults.setLocation(spacing, settingsPanel.getHeight() - defaults.getHeight() - 15);
	    defaults.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
		    if (saveFile != null) {
			for (LUserSetting s : settings) {
			    saveFile.revertToDefault(s);
			}
			update();
		    }
		}
	    });
	    defaults.addMouseListener(new MouseListener() {

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    if (saveFile != null) {
			saveFile.peekDefaults();
			update();
		    }
		}

		@Override
		public void mouseExited(MouseEvent e) {
		    if (saveFile != null) {
			saveFile.clearPeek();
			update();
		    }
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	    });

	    save.setLocation(defaults.getX() + spacing + defaults.getWidth(), defaults.getY());
	    save.addMouseListener(new MouseListener() {

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    if (saveFile != null) {
			saveFile.peekSaved();
			update();
		    }
		}

		@Override
		public void mouseExited(MouseEvent e) {
		    if (saveFile != null) {
			saveFile.clearPeek();
			update();
		    }
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}
	    });
	    save.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
		    if (saveFile != null) {
			for (LUserSetting s : settings) {
			    saveFile.revertToSaved(s);
			}
			update();
		    }
		}
	    });

	    header.addShadow();
	    header.setLocation(settingsPanel.getWidth() / 2 - header.getWidth() / 2, 15);

	    settingsPanel.add(defaults);
	    settingsPanel.add(save);
	    settingsPanel.add(header);
	    add(settingsPanel);
	    last = new Point(settingsPanel.getWidth(), 65);
	    initialized = true;
	    return true;
	}
	return false;
    }

    /**
     * Function that will be called after the Defaults and Saved buttons are pressed.<br>
     * You may override it and add your own functionality.
     */
    protected void update() {
    }

    /**
     * Adds a non-setting component to the panel and adds it to the components list.
     * @param c
     */
    @Override
    public final void Add(Component c) {
	components.add(c);
	settingsPanel.Add(c);
    }

    /**
     * Adds a non-setting component to the panel and adds it to the settings AND components list.
     * @param c
     */
    public void AddSetting(LUserSetting c) {
	Add(c);
	settings.add(c);
    }

    /**
     * Sets the placement relative to the last component added.
     * @param c
     * @return The point the component was placed
     */
    public Point setPlacement(Component c) {
	return setPlacement(c, last.x, last.y);
    }

    /**
     * Sets the placement to (x,y)
     * @param c
     * @param x
     * @param y
     * @return The point the component was placed
     */
    public Point setPlacement(Component c, int x, int y) {
	c.setLocation(x / 2 - c.getWidth() / 2, y + spacing);
	if (c.getX() + c.getWidth() > rightMost) {
	    rightMost = c.getX() + c.getWidth();
	}
	last = new Point(last.x, c.getY() + c.getHeight());
	return last;
    }

    /**
     * Aligns each component to the right, as you would expect from a word processor's "align right".
     */
    public void alignRight() {
	for (Component c : components) {
	    c.setLocation(rightMost - c.getWidth(), c.getY());
	}
    }

    /**
     * Function that opens, initializes if needed, and displays the settings panel.
     */
    public void open() {
	parent.open();
	initialize();
	parent.openPanel(this);
	specialOpen(parent);
	parent.revalidate();
    }

    /**
     *
     * @return An ActionListener that will open this panel.
     */
    public ActionListener getOpenHandler() {

	return new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		SwingUtilities.invokeLater(
			new Runnable() {

			    @Override
			    public void run() {
				open();
			    }
			});
	    }
	};
    }

    /**
     * An empty function that can be overwritten to provide special directives to the open command.
     * @param parent
     */
    public void specialOpen(SPMainMenuPanel parent) {
    }
}
