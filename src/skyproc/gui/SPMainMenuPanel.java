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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lev.gui.LImagePane;
import lev.gui.LLabel;
import lev.gui.LPanel;
import lev.gui.LSaveFile;
import skyproc.SPGlobal;

/**
 *
 * @author Justin Swanson
 */
public class SPMainMenuPanel extends JPanel {

    static final int spacing = 35;
    static final int xPlacement = SUMGUI.leftDimensions.width - 25;
    int yPlacement = 170;
    LImagePane customLogo;
    LLabel version;
    Color color;
    protected LPanel menu = new LPanel(SUMGUI.leftDimensions);
    protected Close closeHandler = new Close();
    protected Open openHandler = new Open();
    ArrayList<SPSettingPanel> panels = new ArrayList<SPSettingPanel>();
    SPSettingPanel activePanel;

    public SPMainMenuPanel(Color menuColor) {
	this.setLayout(null);
	setSize(SUMGUI.middleLeftDimensions.getSize());
	setLocation(0, 0);
	add(menu);
	setOpaque(false);
	color = menuColor;
    }

    public SPMainMenuPanel() {
	this(SUMGUI.light);
    }

    public void addLogo(URL logo) {
	try {
	    int height = 150;
	    customLogo = new LImagePane(logo);
	    customLogo.setMaxSize(SUMGUI.leftDimensions.width, height);
	    customLogo.setLocation(SUMGUI.leftDimensions.width / 2 - customLogo.getWidth() / 2, 0);
	    menu.Add(customLogo);
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    public void setVersion(String version) {
	setVersion(version, new Point(customLogo.getX(), customLogo.getHeight() + customLogo.getY() + 3));
    }

    public void setVersion(String version, Point location) {
	this.version = new LLabel(version, new Font("Serif", Font.PLAIN, 10), SUMGUI.darkGray);
	this.version.setLocation(location);
	menu.Add(this.version);
    }

    public SPMainMenuConfig addMenu(SPSettingPanel panel, boolean checkBox, LSaveFile save, Enum setting) {
	SPMainMenuConfig menuConfig = new SPMainMenuConfig(panel.header.getText(), checkBox, true, color, new Point(xPlacement, yPlacement), save, setting);
	yPlacement += spacing;
	menuConfig.addActionListener(panel.getOpenHandler(this));
	menu.add(menuConfig);
	return menuConfig;
    }

    public void openPanel(SPSettingPanel panel) {
	if (activePanel != null) {
	    activePanel.setVisible(false);
	}
	int index = panels.indexOf(panel);
	if (index != -1) {
	    activePanel = panels.get(index);
	} else {
	    panels.add(panel);
	    activePanel = panel;
	    add(panel);
	}
	activePanel.setVisible(true);
    }

    public ActionListener getOpenHandler() {
	return openHandler;
    }

    public void open() {
	setVisible(true);
	repaint();
    }

    public void close() {
	SwingUtilities.invokeLater(
		new Runnable() {

		    @Override
		    public void run() {
			setVisible(false);
//                        if (openMainMenu != null)
//                            openMainMenu.fire();
		    }
		});

    }

    public class Close implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
	    close();
	}
    }

    public class Open implements ActionListener {

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
    }
}
