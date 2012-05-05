/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import lev.Ln;
import lev.gui.LSaveFile;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Justin Swanson
 */
public class SUMprogram implements SUM {

    ArrayList<String> exclude;
    SPMainMenuPanel mmenu;

    SUMprogram() {
	exclude = new ArrayList<String>(2);
	exclude.add("SkyProc Unified Manager.jar");
	exclude.add("skyproc.jar");
    }

    public static void main(String[] args) throws Exception {
	SUMprogram sum = new SUMprogram();
	ArrayList<Class> hooks = sum.getHooks();
	
	sum.openGUI();
    }

    void openGUI() {
	mmenu = new SPMainMenuPanel();
	SUMGUI.open(this);
    }

    ArrayList<Class> getHooks() {
	ArrayList<File> jars = findJars(new File("../"));
	ArrayList<Class> sumClasses = new ArrayList<Class>();

	for (File jar : jars) {
	    try {
		for (Class c : Ln.loadClasses(jar)) {
		    if (c.isInstance(SUM.class)) {
			sumClasses.add(c);
		    }
		}
	    } catch (MalformedURLException ex) {
		SPGlobal.logException(ex);
	    } catch (FileNotFoundException ex) {
		SPGlobal.logException(ex);
	    } catch (IOException ex) {
		SPGlobal.logException(ex);
	    } catch (ClassNotFoundException ex) {
		SPGlobal.logException(ex);
	    }
	}

	return sumClasses;
    }

    ArrayList<File> findJars(File dir) {
	if (!dir.isDirectory()) {
	    return new ArrayList<File>(0);
	}
	ArrayList<File> out = new ArrayList<File>();
	for (File f : dir.listFiles()) {
	    if (f.isDirectory()) {
		out.addAll(findJars(f));
	    } else {
		if (f.getName().toUpperCase().endsWith(".jar")
			&& !exclude.contains(f.getName().toUpperCase())) {
		    out.add(f);
		}
	    }
	}
	return out;
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
}
