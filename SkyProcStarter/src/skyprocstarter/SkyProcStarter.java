package skyprocstarter;

import java.awt.Color;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lev.gui.LSaveFile;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.ModListing;
import skyproc.SPGlobal;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;
import skyprocstarter.YourSaveFile.Settings;

/**
 *
 * @author Your Name Here
 */
public class SkyProcStarter implements SUM {

    // The important functions to change are:
    //  - getStandardMenu(), where you set up the GUI
    //  - runChangesToPatch(), where you put all the processing code and add
    //    records to the output patch

    String myPatchName = "My Patch";
    // The types of records you want your patcher to import
    // At the moment, it imports NPC_ and LVLN records.  Change this to
    // customize the import to what you need.
    GRUP_TYPE[] importRequests = new GRUP_TYPE[]{
	GRUP_TYPE.NPC_,
	GRUP_TYPE.LVLN
    };
    public static String version = "1.0";
    public static Color headerColor = Color.GRAY;
    public static LSaveFile save = new YourSaveFile();

    // Do not write the bulk of your program here
    // Instead, write your patch changes in the "runChangesToPatch" function
    // at the bottom
    public static void main(String[] args) {
	try {
	    save.init();
	    SUMGUI.open(new SkyProcStarter());
	} catch (Exception e) {
	    // If a major error happens, print it everywhere and display a message box.
	    System.err.println(e.toString());
	    SPGlobal.logException(e);
	    JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
	    SPGlobal.closeDebug();
	}
    }

    @Override
    public String getName() {
	return myPatchName;
    }

    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	return new GRUP_TYPE[0];
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return importRequests;
    }

    @Override
    public boolean importAtStart() {
	return false;
    }

    @Override
    public boolean hasStandardMenu() {
	return true;
    }

    @Override
    public SPMainMenuPanel getStandardMenu() {
	SPMainMenuPanel settingsMenu = new SPMainMenuPanel(getHeaderColor());

	settingsMenu.addMenu(new YourFirstSettingsPanel(settingsMenu), false, save, Settings.FirstSettingsPanelHelp);

	return settingsMenu;
    }

    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame openCustomMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLogo() {
	return false;
    }

    @Override
    public URL getLogo() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSave() {
	return true;
    }

    @Override
    public LSaveFile getSave() {
	return save;
    }

    @Override
    public String getVersion() {
	return version;
    }

    @Override
    public ModListing getListing() {
	return new ModListing(getName(), false);
    }

    @Override
    public Mod getExportPatch() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color getHeaderColor() {
	return headerColor;
    }

    @Override
    public boolean needsPatching() {
	return false;
    }

    @Override
    public void onStart() throws Exception {
    }

    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
    }

    @Override
    public void runChangesToPatch() throws Exception {

	Mod patch = SPGlobal.getGlobalPatch();

	Mod merger = new Mod(getName() + "Merger", false);
	merger.addAsOverrides(SPGlobal.getDB());

	// Write your code here

    }
}
