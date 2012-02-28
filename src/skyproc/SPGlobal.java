package skyproc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import lev.debug.LDebug;
import skyproc.exceptions.BadMod;

/**
 * Global variables/settings of SkyProc.
 *
 * @author Justin Swanson
 */
public class SPGlobal {

    static String gameName = "Skyrim";
    /**
     * Path and filename to look for the active plugins file.<br>
     * "/Skyrim/plugins.txt" by default.
     */
    public static String pluginsListPath = "/Skyrim/plugins.txt";
    /**
     * Path to the Data/ folder to look for plugins to import/export.<br><br> By
     * default, this is set to "../../", meaning the patch has to be in a
     * subfolder of "Data/". (ex "Data/SkyProc Patchers/My SkyProc Patcher/My
     * Patcher.jar")
     */
    public static String pathToData = "../../";
    /**
     * Skyproc will import and embed the language given by SPGlobal.language
     * every time a patch is created. To offer multi-language support, simply
     * give the users of your program ability to adjust this setting.
     */
    public static String language = "English";
    /**
     * The path from the .jar location to create/look for the file used to
     * remember where the plugins.txt file is if the program cannot locate it
     * automatically.
     */
    public static String pluginListBackupPath = "SkyProc-PluginListLocation.txt";
    /**
     * The logger object built into SkyProc. <br> See the levnifty JavaDocs for
     * more information.
     */
    private static SPLogger log;
    static SPDatabase globalDatabase = new SPDatabase();

    /**
     *
     * @return The database defined as the Global Database
     */
    public static SPDatabase getDB() {
	return globalDatabase;
    }
    static Mod globalPatchOut;
    static Map<String, FormID> edidToForm = new HashMap<String, FormID>();

    /**
     * Creating your patch ahead of time, and setting it as the Global Patch
     * will prevent it from being imported by getActiveMods() and getAllMods()
     *
     * @param patch Mod to set as the global patch.
     */
    public static void setGlobalPatch(Mod patch) {
	if (globalPatchOut != null) {
	    modsToSkip.remove(globalPatchOut.getInfo());
	}
	globalPatchOut = patch;
	globalPatchOut.modInfo.setDate(Long.MAX_VALUE);
	modsToSkip.add(globalPatchOut.getInfo());

	// Import old patch for consistency
	SPImporter importer = new SPImporter();
	Mod consistencyPatch;
	try {

	    boolean tmpLogging = logging();
	    logging(debugConsistencyImport);
	    consistencyPatch = importer.importMod(globalPatchOut.modInfo, pathToData, false, GRUP_TYPE.values());
	    logging(tmpLogging);
	    edidToForm = new HashMap<String, FormID>(consistencyPatch.numRecords());
	    for (GRUP g : consistencyPatch.GRUPs.values()) {
		for (Object o : g) {
		    MajorRecord m = (MajorRecord) o;
		    // If src mod is the patch itself
		    if (m.getFormMaster().equals(consistencyPatch.getInfo())) {
			// If EDID is not empty
			if (!m.getEDID().equals("")) {
			    // If already exists, problem
			    if (!edidToForm.containsKey(m.getEDID())) {
				edidToForm.put(m.getEDID(), m.getForm());
				if (logging()) {
				    log("Consistency", m.toString());
				}
			    } else {
				logError("Consistency", "Record " + m.getFormStr() + " had an already existing EDID: " + m.getEDID());
			    }
			} else {
			    logError("Consistency", "Record " + m.getFormStr() + " didn't have an EDID.");
			}
		    }
		}
	    }

	} catch (BadMod ex) {
	    logError("SPGlobal", "Error importing global consistency patch: " + patch.getName());
	}
    }

    /**
     *
     * @return the set Global Patch, or null if one hasn' been set.
     */
    public static Mod getGlobalPatch() {
	return globalPatchOut;
    }
    static ArrayList<ModListing> modsToSkip = new ArrayList<ModListing>();

    public static void addModToSkip(ModListing m) {
	modsToSkip.add(m);
    }

    /**
     * Initializes the Debug Logs in a "SkyProcDebug/" folder, and allows you to
     * print messages to them.<br> Do this step early in your program.
     */
    public static void createGlobalLog() {
	createGlobalLog("");
    }

    /**
     * Initializes the Debug Logs in a path + "SkyProcDebug/" folder, and allows
     * you to print messages to them.<br> Do this step early in your program.
     *
     * @param path The path to create the "SkyProcDebug/" folder.
     */
    public static void createGlobalLog(String path) {
	log = new SPLogger(path + "SkyProcDebug/");
    }

    static void logSync(String header, String... print) {
	if (log != null) {
	    log.logSync(header, print);
	}
    }

    /**
     * Logs a message to the Debug Overview file. <br> Use this for major
     * program "milestones".
     *
     * @param header
     * @param print
     */
    public static void logMain(String header, String... print) {
	if (log != null) {
	    SPGlobal.log.logMain(header, print);
	}
    }

    /**
     *
     * @return True if the logger is currently on.
     */
    public static boolean logging() {
	if (log != null) {
	    return SPGlobal.log.logging();
	} else {
	    return false;
	}
    }

    /**
     *
     * @param on Turns the logger on/off.
     */
    public static void logging(Boolean on) {
	if (log != null) {
	    SPGlobal.log.logging(on);
	}
    }

    /**
     * Flushes the Debug buffers to the files.
     */
    public static void flush() {
	if (log != null) {
	    SPGlobal.log.flush();
	}
    }

    /**
     * A special function that simply prints to both the debug overview and the
     * asynchronous log for easy location in either place.
     *
     * @param header
     * @param print
     */
    public static void logError(String header, String... print) {
	if (log != null) {
	    SPGlobal.log.logError(header, print);
	}
    }

    /**
     * Used for printing exception stack data to the debug overview log.
     *
     * @param e Exception to print.
     */
    public static void logException(Exception e) {
	if (log != null) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw, true);
	    e.printStackTrace(pw);
	    pw.flush();
	    sw.flush();
	    SPGlobal.log.logException(sw.toString());
	}
    }

    static void logSpecial(Enum e, String header, String... print) {
	if (log != null) {
	    SPGlobal.log.logSpecial(e, header, print);
	}
    }

    static void newSyncLog(String fileName) {
	if (log != null) {
	    SPGlobal.log.newSyncLog(fileName);
	}
    }

    /**
     * Prints a message to the asynchronous log.
     *
     * @param header
     * @param print
     */
    public static void log(String header, String... print) {
	if (log != null) {
	    SPGlobal.log.log(header, print);
	}
    }

    /**
     * Creates a new asynchronous log.
     *
     * @param fileName Name of the log.
     */
    public static void newLog(String fileName) {
	if (log != null) {
	    SPGlobal.log.newLog(fileName);
	}
    }

    static void sync(boolean flag) {
	if (log != null) {
	    log.sync(flag);
	}
    }

    static boolean sync() {
	if (log != null) {
	    return log.sync();
	} else {
	    return false;
	}
    }

    /**
     * Closes all logs.
     */
    public static void closeDebug() {
	try {
	    LDebug.wrapUp();
	} catch (IOException ex) {
	}
    }
    // Debug Globals
    public static boolean debugConsistencyTies = false;
    public static boolean debugConsistencyImport = false;
    public static boolean debugBSAimport = true;
    public static boolean debugNIFimport = true;
    /**
     * Prints messages about records pairing strings with external STRINGS
     * files.<br> Prints to the sync log<br>
     */
    public static boolean debugStringPairing = true;
    /**
     * Print messages concerning the merging of two plugins.<br> Prints to the
     * sync log<br>
     */
    public static boolean debugModMerge = true;
    // SubRecords
    /**
     * Print short summary of imported subrecords after each major record is
     * imported.<br> Prints to the sync log<br>
     */
    public static boolean debugSubrecordSummary = true;
    /**
     * Print messages from all subrecord sources. Turn this off and add allowed
     * subrecords to print only selected ones.<br> Prints to the sync log<br>
     */
    public static boolean debugSubrecordAll = true;
    /**
     * Print messages from specific subrecord sources. Only matters if
     * debugSubrecordAll is OFF.<br> Prints to the sync log<br>
     */
    public static Set<Type> debugSubrecordsAllowed = new HashSet<Type>();
    // Exporting
    /**
     * Print short export messages to confirm with records are exporting.<br>
     * Prints to the sync log<br>
     */
    public static boolean debugExportSummary = true;
}
