/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyprocstarter;

import java.awt.Color;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;

/**
 *
 * @author Justin Swanson
 */
public class YourFirstSettingsPanel extends SPSettingPanel {


    public YourFirstSettingsPanel(SPMainMenuPanel parent_) {
	super(parent_, "Settings Panel", Color.ORANGE);
    }

    @Override
    protected void initialize() {
	super.initialize();

	// Add Settings here

	alignRight();

    }
}
