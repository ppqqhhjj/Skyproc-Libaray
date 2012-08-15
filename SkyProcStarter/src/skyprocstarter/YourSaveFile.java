/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyprocstarter;

import lev.gui.LSaveFile;

/**
 *
 * @author Justin Swanson
 */
public class YourSaveFile extends LSaveFile {

    @Override
    protected void initSettings() {
	//  The Setting,	The default value,  Whether or not it changing means a new patch should be made
	Add(Settings.FirstSettingsPanelHelp,		1,	    false);
    }

    @Override
    protected void initHelp() {
	helpInfo.put(Settings.FirstSettingsPanelHelp, "This will show up when you click"
		+ " your first settings panel.");
    }

    public enum Settings {
	FirstSettingsPanelHelp;
    }
}
