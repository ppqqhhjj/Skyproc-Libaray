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
     * Reference to the Main Menu parent GUI object
     */
    protected SPMainMenuPanel parent;
    /**
     * Spacing to be used between settings
     */
    protected int spacing = 12;
    /**
     * Reference to the position of the last-added setting or component added
     * using Add() or AddSetting()
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
     * Flag to symbolize Panel has been initialized and the components have been
     * created and added.
     */
    protected boolean initialized = false;
    static Font font = new Font("Serif", Font.BOLD, 25);

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     */
    public SPSettingPanel(SPMainMenuPanel parent_, String title, Color headerColor) {
	super(SUMGUI.fullDimensions);
	parent = parent_;
	header = new LLabel(title, font, headerColor);
    }

    /**
     * Function that creates all components and adds them to the GUI using Add()
     * or AddSetting().<br><br> It should look like this:<br> <i>if
     * (super.initialize()) {<br> <br> //... Your initializing code ...<br> <br>
     * return true;<br> } else {<br> return false;<br> }<br>
     *
     * @return Whether the GUI was just initialized. False if it has already
     * been.
     */
    protected void initialize() {
	settingsPanel = new LPanel(SUMGUI.middleDimensions);
	settingsPanel.add(header);
	add(settingsPanel);

	header.addShadow();
	header.setLocation(settingsPanel.getWidth() / 2 - header.getWidth() / 2, 15);

	last = new Point(settingsPanel.getWidth(), 65);

	initialized = true;
	setVisible(true);
    }

    /**
     * Function that will be called after the Defaults and Saved buttons are
     * pressed.<br> You may override it and add your own functionality.
     */
    protected void update() {
    }

    protected Point getSpacing(LButton in1, LButton in2, boolean left) {
	int spacing = (settingsPanel.getWidth() - in1.getWidth() - in2.getWidth()) / 3;
	if (left) {
	    return new Point(spacing, settingsPanel.getHeight() - in1.getHeight() - 15);
	} else {
	    return new Point(in1.getX() + in1.getWidth() + spacing, settingsPanel.getHeight() - in2.getHeight() - 15);
	}
    }

    /**
     * Adds a non-setting component to the panel and adds it to the components
     * list.
     *
     * @param c
     */
    @Override
    public final void Add(Component c) {
	components.add(c);
	settingsPanel.Add(c);
    }

    /**
     * Adds a non-setting component to the panel and adds it to the settings AND
     * components list.
     *
     * @param c
     */
    public void AddSetting(LUserSetting c) {
	Add(c);
	settings.add(c);
    }

    /**
     * Sets the placement relative to the last component added.
     *
     * @param c
     * @return The point the component was placed
     */
    public Point setPlacement(Component c) {
	return setPlacement(c, last.x, last.y);
    }

    /**
     * Sets the placement to (x,y)
     *
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
	updateLast(c);
	return last;
    }

    public void updateLast(Component c) {
	last = new Point(last.x, c.getY() + c.getHeight());
    }

    /**
     * Aligns each component to the right, as you would expect from a word
     * processor's "align right".
     */
    public void alignRight() {
	for (Component c : components) {
	    c.setLocation(rightMost - c.getWidth(), c.getY());
	}
    }

    /**
     * Function that opens, initializes if needed, and displays the settings
     * panel.
     */
    public void open() {
	parent.open();
	if (!initialized) {
	    initialize();
	}
	SUMGUI.helpPanel.reset();
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
     * An empty function that can be overwritten to provide special directives
     * to the open command.
     *
     * @param parent
     */
    public void specialOpen(SPMainMenuPanel parent) {
    }
}
