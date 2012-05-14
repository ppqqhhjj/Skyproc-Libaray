/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import javax.swing.JFrame;
import lev.gui.LSaveFile;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.ModListing;

/**
 *
 * @author Justin Swanson
 */
public interface SUM {
    public String getName();
    public GRUP_TYPE[] duplicateOriginalsReport();
    public GRUP_TYPE[] importRequests();
    public boolean importAtStart();
    public boolean hasStandardMenu();
    public SPMainMenuPanel getStandardMenu();
    public boolean hasCustomMenu();
    public JFrame getCustomMenu();
    public boolean hasLogo();
    public URL getLogo();
    public boolean hasSave();
    public LSaveFile getSave();
    public String getVersion();
    public ModListing getListing();
    public Mod getExportPatch();
    public Color getHeaderColor();
    public void runChangesToPatch() throws Exception ;
}
