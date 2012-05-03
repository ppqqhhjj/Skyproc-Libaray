/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.awt.Color;
import java.net.URL;
import javax.swing.JFrame;
import lev.gui.LSaveFile;
import skyproc.gui.SUMGUI;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;

/**
 *
 * @author Justin Swanson
 */
public class SUMprogram implements SUM {

    static SPMainMenuPanel mmenu;
    
    public static void main(String[] args) throws Exception {
	openGUI();
    }
    
    static void openGUI() {
	mmenu = new SPMainMenuPanel();
	SUMGUI.open(new SUMprogram());
    }
    
    @Override
    public String getName() {
	return "SkyProc Unified Manager";
    }

    @Override
    public GRUP_TYPE[] duplicateOriginalsReport() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return new GRUP_TYPE[0];
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
	return mmenu;
    }

    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame getCustomMenu() {
	return null;
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
	return false;
    }

    @Override
    public LSaveFile getSave() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVersion() {
	return "1.0";
    }

    @Override
    public Mod getExportPatch() {
	Mod patch = new Mod("SUM", false);
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.setAuthor("Leviathan1753");
	return patch;
    }

    @Override
    public Color getHeaderColor() {
	return Color.BLUE;
    }

    @Override
    public void runChangesToPatch() throws Exception {
    }
    
//    class SUMclassLoader extends ClassLoader 
}
