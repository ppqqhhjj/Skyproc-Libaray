package skyproc;

import skyproc.gui.SPProgressBarPlug;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.exceptions.BadMod;

/**
 * Global variables/settings of SkyProc.
 *
 * @author Justin Swanson
 */
public class SPGlobal {

    static String header = "SPGlobal";
    static String gameName = "Skyrim";
    static String pathToDebug = "SkyProcDebug/";
    static Mod globalPatchOut;
    static SPLogger log;
    static SPDatabase globalDatabase = new SPDatabase();
    /*
     * Customizable Strings
     */
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
     * A default path to "internal files". This is currently only used for
     * saving custom path information for Skyrim.ini and plugins.txt. This can
     * also be used to store your own internal files.
     */
    public static String pathToInternalFiles = "Files/";
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
     * To be used when implementing the SUM interface.  Add this SUMpath to the beginning of any
     * pathing to access files in your SkyProc patcher program.  This will allow SUM to add extra directory
     *  pathing when it hooks onto your patcher program.
     */
    public static String SUMpath = "";
    static File skyProcDocuments;

    /**
     * Returns a File path to the SkyProc Documents folder.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File getSkyProcDocuments() throws FileNotFoundException, IOException {
	if (skyProcDocuments == null) {
	    File myDocs = SPGlobal.getMyDocumentsSkyrimFolder();
	    skyProcDocuments = new File (myDocs.getPath() + "\\SkyProc\\");
	    skyProcDocuments.mkdirs();
	}
	return skyProcDocuments;
    }

    /**
     *
     * @return The database defined as the Global Database
     */
    public static SPDatabase getDB() {
	return globalDatabase;
    }

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
	modsToSkip.add(globalPatchOut.getInfo());
	Consistency.importConsistency();
    }

    /**
     *
     * @return the set Global Patch, or null if one hasn' been set.
     */
    public static Mod getGlobalPatch() {
	return globalPatchOut;
    }
    static ArrayList<ModListing> modsToSkip = new ArrayList<ModListing>();

    /**
     *
     * @param m Mod to skip when importing.
     */
    public static void addModToSkip(ModListing m) {
	modsToSkip.add(m);
    }

    /**
     * Querys the Global Database and returns whether the FormID exists.<br>
     * NOTE: it is recommended you use the version that only searches in
     * specific GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query) {
	return SPDatabase.queryMajor(query, SPGlobal.getDB());
    }

    /**
     * Querys the Global Database and returns whether the FormID exists. It
     * limits its search to the given GRUP types for speed reasons.
     *
     * @param query FormID to look for.
     * @param grup_types GRUPs to look in.
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query, GRUP_TYPE... grup_types) {
	return SPDatabase.queryMajor(query, SPGlobal.getDB(), grup_types);
    }

    /**
     * Returns the My Documents Skyrim folder where the ini's are located.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    static public File getMyDocumentsSkyrimFolder() throws FileNotFoundException, IOException {
	return getSkyrimINI().getParentFile();
    }

    /**
     * Returns the Skyrim.ini file in the My Documents folder.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    static public File getSkyrimINI() throws FileNotFoundException, IOException {
	File myDocuments = Ln.getMyDocuments();
	File ini = new File(myDocuments.getPath() + "//My Games//Skyrim//Skyrim.ini");

	// See if there's a manual override
	File override = new File(SPGlobal.pathToInternalFiles + "Skyrim-INI-Location.txt");
	if (override.exists()) {
	    SPGlobal.log(header, "Skyrim.ini override file exists: " + override);
	    BufferedReader in = new BufferedReader(new FileReader(override));
	    File iniTmp = new File(in.readLine());
	    if (iniTmp.exists()) {
		SPGlobal.log(header, "Skyrim.ini location override: " + iniTmp);
		ini = iniTmp;
	    } else {
		SPGlobal.log(header, "Skyrim.ini location override thought to be in: " + iniTmp + ", but it did not exist.");
	    }
	}

	if (!ini.exists()) {
	    SPGlobal.log(header, "Skyrim.ini believed to be in: " + ini + ". But it does not exist.  Locating manually.");
	    ini = Ln.manualFindFile("your Skyrim.ini file.", new File(SPGlobal.pathToInternalFiles + "SkyrimINIlocation.txt"));
	} else if (SPGlobal.logging()) {
	    SPGlobal.log(header, "Skyrim.ini believed to be in: " + ini + ". File exists.");
	}
	return ini;
    }

    /**
     * Returns the plugins.txt file that contains load order information.
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    static public String getPluginsTxt() throws FileNotFoundException, IOException {
	String dataFolder = System.getenv("LOCALAPPDATA");

	// If XP
	if (dataFolder == null) {
	    SPGlobal.logError(header, "Can't locate local app data folder directly, probably running XP.");
	    dataFolder = System.getenv("APPDATA");

	    // If Messed Up
	    if (dataFolder == null) {
		SPGlobal.logError(header, "Can't locate local app data folder.");
		dataFolder = Ln.manualFindFile("your Plugins.txt file.\nThis is usually found in your Local Application Data folder.\n"
			+ "You may need to turn on hidden folders to see it.", new File(SPGlobal.pathToInternalFiles + "PluginsListLocation.txt")).getPath();
	    } else {

		SPGlobal.logSync(header, "APPDATA returned: ", dataFolder, "     Shaving off the \\Application Data.");
		dataFolder = dataFolder.substring(0, dataFolder.indexOf("\\Application Data"));
		SPGlobal.logSync(header, "path now reads: ", dataFolder, "     appending \\Local Settings\\Application Data");
		dataFolder = dataFolder + "\\Local Settings\\Application Data";
		SPGlobal.logSync(header, "path now reads: ", dataFolder);
		dataFolder = dataFolder.concat(SPGlobal.pluginsListPath);
		SPGlobal.logSync(header, SPGlobal.gameName + " Plugin file found in: ", dataFolder);
	    }
	} else {
	    dataFolder = dataFolder.concat(SPGlobal.pluginsListPath);
	    SPGlobal.logSync(header, SPGlobal.gameName + " Plugin list file thought to be in: ", dataFolder);
	}

	File pluginListPath = new File(dataFolder);
	if (!pluginListPath.exists()) {
	    dataFolder = Ln.manualFindFile("your Plugins.txt file.\nThis is usually found in your Local Application Data folder.\n"
		    + "You may need to turn on hidden folders to see it.", new File(SPGlobal.pathToInternalFiles + "PluginsListLocation.txt")).getPath();
	}
	return dataFolder;
    }


    /*
     * Logging functions
     */
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
	pathToDebug = path + "SkyProcDebug/";
	log = new SPLogger(pathToDebug);
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
     * Logs a specific record as blocked in the "Blocked Records.txt" log.
     *
     * @param header
     * @param reason Reason for blocking the record.
     * @param m Record that was blocked.
     */
    public static void logBlocked(String header, String reason, MajorRecord m) {
	log.logSpecial(SPLogger.SpecialTypes.BLOCKED, header, "Blocked " + m + " for reason: " + reason);
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
     *
     * @return True if the logger is currently on.
     */
    public static boolean loggingSync() {
	if (log != null) {
	    return SPGlobal.log.loggingSync();
	} else {
	    return false;
	}
    }

    /**
     *
     * @param on Turns the logger on/off.
     */
    public static void loggingSync(Boolean on) {
	if (log != null) {
	    SPGlobal.log.loggingSync(on);
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
	    SPGlobal.log.logException(e);
	}
    }

    /**
     * Logs to a specially created log that was previously created using newSpecialLog().
     * @param e The enum you defined to symbolize the "key" to the special log.
     * @param header
     * @param print
     */
    public static void logSpecial(Enum e, String header, String... print) {
	if (log != null) {
	    SPGlobal.log.logSpecial(e, header, print);
	}
    }

    /**
     * Creates a new special log with any enum value as the key.
     * @param e Any enum you define to symbolize the "key" to the special log.
     * @param logName
     */
    public static void newSpecialLog(Enum e, String logName) {
	if (log != null) {
	    SPGlobal.log.addSpecial(e, logName);
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
     * Redirects System.out to the asynchronous log stream.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void redirectSystemOutStream() throws FileNotFoundException, IOException {
	if (log == null) {
	    createGlobalLog();
	}
	OutputStream outToDebug = new OutputStream() {

	    @Override
	    public void write(final int b) throws IOException {
		if (b != 116) {
		    log("", String.valueOf((char) b));
		}
	    }

	    @Override
	    public void write(byte[] b, int off, int len) throws IOException {
		String output = new String(b, off, len);
		if (output.length() > 2) {
		    log("", output);
		}
	    }

	    @Override
	    public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	    }
	};

	System.setOut(new PrintStream(outToDebug, true));
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

    /**
     * Returns the path to the debug folder.
     * @return
     */
    public static String pathToDebug() {
	return pathToDebug;
    }
    // Debug Globals
    /**
     * This flag prints messages when records are tied to FormIDs from the last
     * patch via EDID match.
     */
    public static boolean debugConsistencyTies = false;
    /**
     * This flag prints old FormIDs imported from the last patch.
     */
    public static boolean debugConsistencyImport = false;
    /**
     * Displays information about BSA importing
     */
    public static boolean debugBSAimport = false;
    /**
     * Displays information about NIF importing
     */
    public static boolean debugNIFimport = false;
    /**
     * Prints messages about records pairing strings with external STRINGS
     * files.<br> Prints to the sync log<br>
     */
    public static boolean debugStringPairing = true;
    /**
     * Print messages concerning the merging of two plugins.<br> Prints to the
     * sync log<br>
     */
    public static boolean debugModMerge = false;
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
