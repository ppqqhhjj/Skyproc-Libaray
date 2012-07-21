/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import lev.Ln;
import lev.debug.LDebug;
import lev.gui.*;
import skyproc.*;

/**
 * The standard GUI setup used in SUM. This can be used to create GUIs that
 * manage your settings, handle savefiles, and hook directly into SUM.
 *
 * @author Justin Swanson
 */
public class SUMGUI extends JFrame {

    static JFrame singleton = null;
    /**
     * Bounds of the SUM GUI.
     */
    public final static Rectangle fullDimensions = new Rectangle(0, 0, 950, 632);
    /**
     * Bounds of the left column
     */
    public final static Rectangle leftDimensions = new Rectangle(0, 0, 299, fullDimensions.height - 28); // For status update
    /**
     * Bounds of the middle column
     */
    public final static Rectangle middleDimensions = new Rectangle(leftDimensions.x + leftDimensions.width + 7, 0, 330, fullDimensions.height);
    /**
     * Bounds of the right column
     */
    public final static Rectangle rightDimensions = new Rectangle(middleDimensions.x + middleDimensions.width + 7, 0, 305, fullDimensions.height);
    /**
     * Bounds of the two right columns
     */
    public final static Rectangle middleRightDimensions = new Rectangle(middleDimensions.x, 0, rightDimensions.x + rightDimensions.width, fullDimensions.height);
    /**
     * Bounds of the two left columns
     */
    public final static Rectangle middleLeftDimensions = new Rectangle(0, 0, middleDimensions.x + middleDimensions.width, middleDimensions.height);
    static final Color light = new Color(238, 233, 204);
    static final Color lightGray = new Color(190, 190, 190);
    static final Color darkGray = new Color(110, 110, 110);
    static final Color lightred = Color.red;
    static SUM hook;
    static final String header = "SUM";
    /**
     * Import/Export background thread is stored here for access.
     */
    static public Thread parser;
    static ProcessingThread parserRunnable;
    static boolean imported = false;
    static boolean exitRequested = false;
    /**
     * Progress bar frame that pops up at the end when creating the patch.
     */
    static public LProgressBarFrame progress = new LProgressBarFrame(
	    new Font("SansSerif", Font.PLAIN, 12), Color.GRAY,
	    new Font("SansSerif", Font.PLAIN, 10), Color.lightGray);
    /**
     * Help panel on the right column of the GUI.
     */
    static public LHelpPanel helpPanel = new LHelpPanel(rightDimensions, new Font("Serif", Font.BOLD, 25), light, lightGray, true, 10);
    static LImagePane backgroundPanel;
    static LLabel patchNeededLabel;
    static boolean needsPatching = false;
    static LCheckBox forcePatch;
    static LImagePane skyProcLogo;
    static JTextArea statusUpdate;
    static LLabel versionNum;
    static LButton cancelPatch;
    static Font SUMSmallFont = new Font("SansSerif", Font.PLAIN, 10);
    static String pathToLastMasterlist = SPGlobal.pathToInternalFiles + "Last Masterlist.txt";
    static String pathToLastModlist = SPGlobal.pathToInternalFiles + "Last Modlist.txt";
    static File crashFile = new File(SPGlobal.pathToInternalFiles + "Last Crash State.txt");

    SUMGUI() {
	super(hook.getName());
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setResizable(false);
	Dimension GUISIZE = new Dimension(954, 658);
	setSize(GUISIZE);
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	setLocation(dim.width / 2 - GUISIZE.width / 2, dim.height / 2 - GUISIZE.height / 2);
	setLayout(null);
	addComponents();
	addWindowListener(new WindowListener() {

	    @Override
	    public void windowClosed(WindowEvent arg0) {
	    }

	    @Override
	    public void windowActivated(WindowEvent arg0) {
	    }

	    @Override
	    public void windowClosing(WindowEvent arg0) {
		closingGUIwindow();
	    }

	    @Override
	    public void windowDeactivated(WindowEvent arg0) {
	    }

	    @Override
	    public void windowDeiconified(WindowEvent arg0) {
	    }

	    @Override
	    public void windowIconified(WindowEvent arg0) {
	    }

	    @Override
	    public void windowOpened(WindowEvent arg0) {
	    }
	});
	helpPanel.setHeaderColor(hook.getHeaderColor());
    }

    final void addComponents() {
	try {

	    backgroundPanel = new LImagePane(SUMGUI.class.getResource("background.jpg"));
	    super.add(backgroundPanel);

	    cancelPatch = new LButton("Cancel");
	    cancelPatch.setLocation(backgroundPanel.getWidth() - cancelPatch.getWidth() - 5, 5);
	    cancelPatch.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    if (SPGlobal.logging()) {
			SPGlobal.logMain(header, "Closing program early because user cancelled.");
		    }
		    exitProgram(false);
		}
	    });
	    backgroundPanel.add(cancelPatch);

	    forcePatch = new LCheckBox("Force Patch on Exit", SUMSmallFont, Color.GRAY);
	    forcePatch.setLocation(rightDimensions.x + 10, cancelPatch.getY() + cancelPatch.getHeight() / 2 - forcePatch.getHeight() / 2);
	    forcePatch.setOffset(-4);
	    backgroundPanel.add(forcePatch);

	    patchNeededLabel = new LLabel("A patch will be generated upon exit.", SUMSmallFont, Color.GRAY);
	    patchNeededLabel.setLocation(forcePatch.getLocation());
	    patchNeededLabel.setVisible(false);
	    backgroundPanel.add(patchNeededLabel);

	    progress.addWindowListener(new WindowListener() {

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
		    if (progress.closeOp == JFrame.DISPOSE_ON_CLOSE) {
			if (SPGlobal.logging()) {
			    SPGlobal.logMain(header, "Closing program early because progress bar was forced to close by user.");
			}
			exitProgram(false);
		    }
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
	    });
	    progress.setGUIref(singleton);

	    statusUpdate = new JTextArea();
	    statusUpdate.setSize(250, 18);
	    statusUpdate.setLocation(5, getFrameHeight() - statusUpdate.getHeight());
	    statusUpdate.setForeground(Color.LIGHT_GRAY);
	    statusUpdate.setOpaque(false);
	    statusUpdate.setText("Started application");
	    statusUpdate.setEditable(false);
	    statusUpdate.setVisible(true);
	    backgroundPanel.add(statusUpdate);

	    skyProcLogo = new LImagePane(SPDefaultGUI.class.getResource("SkyProc Logo Small.png"));
	    skyProcLogo.setLocation(5, statusUpdate.getY() - skyProcLogo.getHeight() - 5);
	    backgroundPanel.add(skyProcLogo);

	    helpPanel.setBounds(rightDimensions);
	    backgroundPanel.add(helpPanel);

	    SPProgressBarPlug.addProgressBar(new SUMProgress());

	    if (crashFile.exists()) {
		setPatchNeeded(true);
	    }

	    setVisible(true);

	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}

    }

    /**
     * Opens and hooks onto a program that implements the SUM interface.
     *
     * @param hook Program to open and hook to
     */
    public static void open(final SUM hook) {
	SUMGUI.hook = hook;
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		if (singleton == null) {

		    if (hook.hasSave()) {
			hook.getSave().init();
		    }

		    try {
			hook.onStart();
		    } catch (Exception ex) {
			SPGlobal.logException(ex);
		    }

		    if (hook.hasCustomMenu()) {
			singleton = hook.openCustomMenu();
		    } else {
			singleton = new SUMGUI();
		    }
		    progress.setGUIref(singleton);
		    progress.moveToCorrectLocation();
		    if (hook.hasStandardMenu()) {
			singleton.add(hook.getStandardMenu());
		    }

		    if (hook.importAtStart()) {
			runThread();
		    }
		}
	    }
	});
    }

    int getFrameHeight() {
	return this.getHeight() - 28;
    }

    static void imported() {
	SPProgressBarPlug.setStatus("Done importing.");
	setPatchNeeded(testNeedsPatching(true));
    }

    public static void setPatchNeeded(boolean on) {
	needsPatching = on;
	if (SPGlobal.logging()) {
	    SPGlobal.logMain(header, "Patch needed: " + on);
	}
	patchNeededLabel.setVisible(on);
	forcePatch.setVisible(!on);
    }

    static void setBackgroundPicture(URL backgroundPicture) {
	try {
	    backgroundPanel.setImage(backgroundPicture);
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    static boolean needsImporting() {

	if (forcePatch.isSelected() || testNeedsPatching(false)) {
	    setPatchNeeded(true);
	    return true;
	}
	try {
	    File f = new File(pathToLastModlist);
	    if (!f.isFile()) {
		return true;
	    }

	    ArrayList<String> oldList = Ln.loadFileToStrings(f, true);
	    ArrayList<ModListing> curList = new ArrayList<>(SPImporter.getActiveModList());
	    curList.remove(hook.getListing());
	    ArrayList<ModListing> curListTmp = new ArrayList<>(curList);

	    if (curList.size() != oldList.size()) {
		return true;
	    }

	    for (int i = 0; i < curList.size(); i++) {
		ModListing m = curListTmp.get(i);
		if (!oldList.get(i).equals(m.print().toUpperCase())) {
		    return true;
		}
	    }

	    //Don't need a patch, check for custom hook coding
	    return SUMGUI.hook.needsPatching();

	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}

	return true;
    }

    static boolean testNeedsPatching(boolean imported) {
	if (needsPatching) {
	    return true;
	}
	// Check if savefile has important settings changed
	if (hook.hasSave()) {
	    if (hook.getSave().needsPatch()) {
		if (SPGlobal.logging()) {
		    SPGlobal.logMain(header, "Patch needed because an important setting changed.");
		}
		return true;
	    }
	}
	// Check if crashed earlier while needing to patch
	if (crashFile.isFile()) {
	    if (SPGlobal.logging()) {
		SPGlobal.logMain(header, "Patch needed because it closed prematurely earlier while needing to patch.");
	    }
	    return true;
	}
	// If imported, check master lists
	if (imported) {
	    try {
		// Compile old and new Master lists
		File f = new File(pathToLastMasterlist);
		if (!f.isFile()) {
		    if (SPGlobal.logging()) {
			SPGlobal.logMain(header, "Patch needed because old master list file could not be found.");
		    }
		    return true;
		}
		ArrayList<String> oldMasterList = Ln.loadFileToStrings(pathToLastMasterlist, true);

		SPDatabase db = SPGlobal.getDB();
		ArrayList<String> curImportedMods = new ArrayList<>();
		for (ModListing m : db.getImportedMods()) {
		    curImportedMods.add(m.print().toUpperCase());
		}
		curImportedMods.remove(SPGlobal.getGlobalPatch().getName().toUpperCase());

		//Remove matching master mods, must be in order
		ArrayList<String> curImportedModsTmp = new ArrayList<>(curImportedMods);
		for (int i = 0; i < curImportedModsTmp.size(); i++) {
		    String curName = curImportedModsTmp.get(i);
		    if (oldMasterList.contains(curName)) {
			for (int j = 0; j < oldMasterList.size(); j++) {
			    if (oldMasterList.get(j).equalsIgnoreCase(curName)) {
				oldMasterList.remove(curName);
				curImportedMods.remove(curName);
				break;
			    } else if (curImportedModsTmp.contains(oldMasterList.get(j))) {
				//Matching mods out of order, need to patch
				if (SPGlobal.logging()) {
				    SPGlobal.logMain(header, "Patch needed because masters from before were in a different order.");
				}
				return true;
			    }
			}
		    }
		}

		//If old masters are missing, need patch
		if (!oldMasterList.isEmpty()) {
		    if (SPGlobal.logging()) {
			SPGlobal.logMain(header, "Patch needed because old masters are missing.");
		    }
		    return true;
		}

		//Remove mods that were imported last time
		ArrayList<String> oldModList = Ln.loadFileToStrings(pathToLastModlist, true);
		for (String s : oldModList) {
		    curImportedMods.remove(s);
		}

		//Check new mods for any records patcher is interested in.  If found, need patch.
		for (String curString : curImportedMods) {
		    Mod curMaster = SPGlobal.getDB().getMod(new ModListing(curString));
		    ArrayList<GRUP_TYPE> contained = curMaster.getContainedTypes();
		    for (GRUP_TYPE g : hook.importRequests()) {
			if (contained.contains(g)) {
			    if (SPGlobal.logging()) {
				SPGlobal.logMain(header, "Patch needed because a new mod had records patch might be interested in.");
			    }
			    return true;
			}
		    }
		}

	    } catch (IOException ex) {
		SPGlobal.logException(ex);
		if (SPGlobal.logging()) {
		    SPGlobal.logMain(header, "Patch needed because exception was thrown in patch sensing code.");
		}
		return true;
	    }
	}

	//Don't need a patch, check for custom hook coding
	if (SPGlobal.logging()) {
	    SPGlobal.logMain(header, "Checking SUM hook to see if it wants to create a patch.");
	}
	return SUMGUI.hook.needsPatching();
    }

    static void closingGUIwindow() {
	SPGlobal.log(header, "Window Closing.");
	exitRequested = true;
	if (!imported && !needsImporting()) {
	    SPProgressBarPlug.done();
	    if (SPGlobal.logging()) {
		SPGlobal.logMain(header, "Closing program early because it does not need importing.");
	    }
	    exitProgram(false);
	} else {
	    progress.setExitOnClose();
	    progress.open();
	}
	runThread();
    }

    /**
     * Immediately saves settings to file, closes debug logs, and exits the
     * program.<br> NO patch is generated.
     */
    static public void exitProgram(boolean generatedPatch) {
	SPGlobal.log(header, "Exit requested.");
	if (hook.hasSave()) {
	    hook.getSave().saveToFile();
	}
	if (generatedPatch) {
	    try {
		SPGlobal.getDB().exportModList(pathToLastModlist);
	    } catch (IOException ex) {
		SPGlobal.logException(ex);
	    }
	    if (crashFile.isFile()) {
		crashFile.delete();
	    }
	} else if (needsPatching) {
	    try {
		BufferedWriter out = new BufferedWriter(new FileWriter(crashFile));
		out.write("Closed prematurely while needing to patch");
		out.close();
	    } catch (Exception e) {
		SPGlobal.logException(e);
	    }
	}
	try {
	    hook.onExit(generatedPatch);
	} catch (Exception e) {
	    SPGlobal.logException(e);
	}
	LDebug.wrapUpAndExit();
    }

    static class ProcessingThread implements Runnable {

	public Set<Runnable> afterImporting = new HashSet<>();

	@Override
	public void run() {
	    SPGlobal.log("START IMPORT THREAD", "Starting of process thread.");
	    try {
		if (!imported) {
		    SPGlobal.setGlobalPatch(hook.getExportPatch());
		    SPImporter importer = new SPImporter();
		    importer.importActiveMods(hook.importRequests());
		    imported();
		    imported = true;
		    for (Runnable r : afterImporting) {
			r.run();
		    }
		}
		if (exitRequested) {
		    if (needsPatching || forcePatch.isSelected()) {

			hook.runChangesToPatch();

			try {
			    // Export your custom patch.
			    SPGlobal.getGlobalPatch().export();
			    SPGlobal.getGlobalPatch().exportMasterList(pathToLastMasterlist);
			} catch (Exception ex) {
			    // If something goes wrong, show an error message.
			    SPGlobal.logException(ex);
			    JOptionPane.showMessageDialog(null, "There was an error exporting the custom patch.\n(" + ex.getMessage() + ")\n\nPlease contact Leviathan1753.");
			}

			if (SPGlobal.logging()) {
			    SPGlobal.logMain(header, "Closing program after successfully running patch.");
			}
		    }
		    if (SPGlobal.logging()) {
			SPGlobal.logMain(header, "Closing program normally from thread.");
		    }
		    SPProgressBarPlug.done();
		    exitProgram(true);
		}
	    } catch (Exception e) {
		System.err.println(e.toString());
		SPGlobal.logException(e);
		JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs.");

		// if exception occurs
		if (SPGlobal.logging()) {
		    SPGlobal.logMain(header, "Closing program after UNSUCCESSFULLY running import/patch.");
		}
		exitProgram(false);
	    }
	}

	public void main(String args[]) {
	    (new Thread(new ProcessingThread())).start();
	}
    }

    static void runThread() {
	runThread(null);
    }

    static void runThread(Runnable r) {
	if (parser == null || !parser.isAlive()) {
	    parserRunnable = new ProcessingThread();
	    parser = new Thread(parserRunnable);
	    if (r != null) {
		parserRunnable.afterImporting.add(r);
	    }
	    parser.start();
	} else {
	    if (r != null) {
		parserRunnable.afterImporting.add(r);
	    }
	}
    }

    public static void startImport(Runnable codeToRunAfter) {
	if (!imported) {
	    runThread(codeToRunAfter);
	} else {
	    codeToRunAfter.run();
	}
    }

    public static void startImport() {
	runThread(null);
    }

    @Override
    public Component add(Component comp) {
	return backgroundPanel.add(comp);
    }

    /**
     * Interface that hooks SkyProc's progress bar output to the SUM GUI's
     * progress bar display.
     */
    public class SUMProgress implements LProgressBarInterface {

	@Override
	public void setMax(int in) {
	    progress.setMax(in);
	}

	@Override
	public void setMax(int in, String status) {
	    progress.setMax(in, status);
	    if (!progress.paused()) {
		statusUpdate.setText(status);
	    }
	}

	@Override
	public void setStatus(String status) {
	    progress.setStatus(status);
	    if (!progress.paused()) {
		statusUpdate.setText(status);
	    }
	}

	/**
	 *
	 * @param min
	 * @param max
	 * @param status
	 */
	@Override
	public void setStatus(int min, int max, String status) {
	    progress.setStatus(min, max, status);
	    if (!progress.paused()) {
		statusUpdate.setText(status);
	    }
	}

	@Override
	public void incrementBar() {
	    progress.incrementBar();
	}

	@Override
	public void reset() {
	    progress.incrementBar();
	}

	@Override
	public void setBar(int in) {
	    progress.setBar(in);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getBar() {
	    return progress.getBar();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getMax() {
	    return progress.getMax();
	}

	/**
	 *
	 * @param on
	 */
	@Override
	public void pause(boolean on) {
	    progress.pause(on);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean paused() {
	    return progress.paused();
	}

	/**
	 *
	 */
	@Override
	public void done() {
	    progress.done();
	}
    }
}
