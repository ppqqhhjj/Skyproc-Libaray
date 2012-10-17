package skyproc;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.DataFormatException;
import lev.LFileChannel;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.BadMod;
import skyproc.gui.SPProgressBarPlug;

/**
 * Used to import data from files into Mod objects that are ready to be
 * manipulated/exported.
 *
 * @author Justin Swanson
 */
public class SPImporter {

    private static String header = "Importer";
    int curMod = 1;
    int maxMod = 1;
    static int extraStepsPerMod = 1;

    /**
     * A placeholder constructor not meant to be called.<br> An SPImporter
     * object should only be instantiated as an extended class that overrides
     * the importControl() function.
     */
    public SPImporter() {
    }

    /**
     * This function can be used in conjunction with runBackgroundImport() to
     * create import functionality in a separate thread, as to not halt GUI
     * response while importing.<br> Override this function in an extended
     * SPImporter class, and add custom code to it for use with
     * runBackgroundImport().
     */
    public void importControl() {
    }

    /**
     * Creates a new thread and runs any code in importControl in the
     * background. This is useful for GUI programs where you don't want the
     * program to freeze while it processes. <br> <br> NOTE: You MUST override
     * importControl() with custom code telling the program what you want it to
     * do in the background.
     */
    public void runBackgroundImport() {
	(new Thread(new StartImportThread())).start();
    }

    private class StartImportThread implements Runnable {

	@Override
	public void run() {
	    importControl();
	}

	public void main(String args[]) {
	    (new Thread(new StartImportThread())).start();
	}
    }

    /**
     * Loads in plugins.txt and reads in the mods the user has active and
     * returns an ArrayList of the ModListings in load order. If a mod being
     * read in fits the criteria of isModToSkip(), then it will be omitted in
     * the results.<br> If any mods on the list are not present in the data
     * folder, they will be skipped.<br><br> If the program cannot locate
     * plugins.txt, it will prompt the user to locate the file themselves. Once
     * they do, it will create a file and use that as a reference for future
     * patch generations.<br><br> Related settings in SPGlobal:<br> -
     * pluginsListPath<br> - pathToData<br> - pluginListBackupPath
     *
     * @see SPGlobal
     * @return An ArrayList of ModListings of all active mods present in the
     * data folder.
     * @throws java.io.IOException
     */
    static public ArrayList<ModListing> getActiveModList() throws java.io.IOException {
	if (SPDatabase.activePlugins.isEmpty()) {
	    SPGlobal.sync(true);
	    SPGlobal.newSyncLog("Get Active Mod List.txt");
	    String header = "IMPORT MODS";
	    BufferedReader ModFile;
	    String dataFolder = SPGlobal.getPluginsTxt();

	    //Open Plugin file
	    ModFile = new BufferedReader(new FileReader(dataFolder));

	    try {
		String line = ModFile.readLine();
		ArrayList<String> lines = new ArrayList<>();
		SPGlobal.logSync(header, "Loading in Active Plugins");
		File pluginName;

		//If Skyrim, add Skyrim.esm and Update.esm, as they
		//are automatically removed from the list, and assumed
		if (SPGlobal.gameName.equals("Skyrim")) {
		    lines.add("Skyrim.esm");
		    lines.add("Update.esm");
		}

		while (line != null) {
		    if (line.indexOf("#") >= 0) {
			line = line.substring(0, line.indexOf("#"));
		    }
		    line = line.trim();
		    if (!line.equals("")) {
			pluginName = new File(SPGlobal.pathToData + line);
			if (!SPGlobal.modsToSkip.contains(new ModListing(line))
				&& !Ln.hasAnyKeywords(line, SPGlobal.modsToSkipStr)) {
			    if (pluginName.isFile()) {
				if (!Ln.containsIgnoreCase(lines, line)) {
				    SPGlobal.logSync(header, "Adding mod: " + line);
				    lines.add(line);
				}
			    } else if (SPGlobal.logging()) {
				SPGlobal.logSync(header, "Mod didn't exist: ", line);
			    }
			} else if (SPGlobal.logging()) {
			    SPGlobal.logSync(header, "Mod was on the list to skip: " + line);
			}
		    }
		    line = ModFile.readLine();
		}

		SPGlobal.sync(false);
		SPDatabase.activePlugins = sortModListings(lines);

	    } catch (java.io.FileNotFoundException e) {
		SPGlobal.logException(e);
		SPGlobal.sync(false);
		throw e;
	    } catch (java.io.IOException e) {
		SPGlobal.logException(e);
		SPGlobal.sync(false);
		throw e;
	    }
	}
	return SPDatabase.activePlugins;
    }

    /**
     * Will scan the data folder and ModListings for all .esp and .esm files,
     * regardless of if they are active. It will return a complete list in load
     * order. If a mod being read in fits the criteria of isModToSkip(), then it
     * will be omitted in the results.<br><br> Related settings in SPGlobal:<br>
     * - pathToData
     *
     * @see SPGlobal
     * @return ArrayList of ModListings of all mods present in the data folder.
     */
    static public ArrayList<ModListing> getModList() {
	SPGlobal.newSyncLog("Get All Present Mod List.txt");
	File directory = new File(SPGlobal.pathToData);
	ArrayList<String> out = new ArrayList<String>();
	if (directory.isDirectory()) {
	    File[] files = directory.listFiles();
	    for (File f : files) {
		String name = f.getName();
		if (name.contains(".esp") || name.contains(".esm")) {
		    if (!SPGlobal.modsToSkip.contains(new ModListing(name))) {
			out.add(name);
		    } else if (SPGlobal.logging()) {
			SPGlobal.logSync(header, "Mod was on the list to skip: " + name);
		    }
		}
	    }
	}
	return sortModListings(out);
    }

    static ArrayList<ModListing> sortModListings(ArrayList<String> lines) {
	SPGlobal.sync(true);
	//Read it in
	ArrayList<String> esms = new ArrayList<String>();
	ArrayList<String> esps = new ArrayList<String>();

	for (String line : lines) {
	    if (line.contains(".esm")) {
		esms.add(line);
	    } else {
		esps.add(line);
	    }
	}

	SPGlobal.flush();

	ArrayList<ModListing> listing = new ArrayList<ModListing>();
	for (String m : esms) {
	    listing.add(new ModListing(m));
	}
	for (String m : esps) {
	    listing.add(new ModListing(m));
	}

	if (SPGlobal.logging()) {
	    SPGlobal.logSync(header, "=========  Final sorted load order : ==========");
	    int counter = 0;
	    for (ModListing m : listing) {
		SPGlobal.logSync(header, Ln.prettyPrintHex(counter++) + " Name: " + m.print());
	    }
	}

	SPGlobal.sync(false);
	return listing;
    }

    /**
     * Imports all mods in the user's Data/ folder, no matter if they are
     * currently active or not. Imports all GRUPs currently supported by
     * SkyProc. If a mod being read in fits the criteria of isModToSkip(), then
     * it will be omitted in the results.<br><br> NOTE: It is suggested for
     * speed reasons to only import the GRUP types you are interested in by
     * using other import functions.
     *
     * @return A set of Mods with all their data imported and ready to be
     * manipulated.
     */
    public Set<Mod> importAllMods() {
	return importAllMods(GRUP_TYPE.values());
    }

    /**
     * Imports all mods in the user's Data/ folder, no matter if they are
     * currently active or not. Imports only GRUPS specified in the parameter.
     * If a mod being read in fits the criteria of isModToSkip(), then it will
     * be omitted in the results.<br><br> Related settings in SPGlobal:<br> -
     * pathToData
     *
     * @see SPGlobal
     * @param grup_targets Any amount of GRUP targets, separated by commas, that
     * you wish to import.
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importAllMods(GRUP_TYPE... grup_targets) {
	return importMods(getModList(), SPGlobal.pathToData, grup_targets);
    }

    /**
     * Imports all mods in the user's Data/ folder, no matter if they are
     * currently active or not. Imports only GRUPS specified in the parameter.
     * If a mod being read in fits the criteria of isModToSkip(), then it will
     * be omitted in the results.<br><br> Related settings in SPGlobal:<br> -
     * pathToData
     *
     * @see SPGlobal
     * @param grup_targets An arraylist of GRUP_TYPE with the desired types to
     * import
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importAllMods(ArrayList<GRUP_TYPE> grup_targets) {
	GRUP_TYPE[] tmp = new GRUP_TYPE[0];
	return importMods(getModList(), SPGlobal.pathToData, grup_targets.toArray(tmp));
    }

    /**
     * Loads in plugins.txt and reads in the mods the user has active, and loads
     * only those that are also present in the data folder. Imports all GRUPs
     * currently supported by SkyProc. If a mod being read in fits the criteria
     * of isModToSkip(), then it will be omitted in the results.<br><br> If the
     * program cannot locate plugins.txt, it will prompt the user to locate the
     * file themselves. Once they do, it will create a file and use that as a
     * reference for future patch generations.<br><br> NOTE: It is suggested for
     * speed reasons to only import the GRUP types you are interested in by
     * using other import functions.<br><br> Related settings in SPGlobal:<br> -
     * pluginsListPath<br> - pathToData<br> - pluginListBackupPath
     *
     * @see SPGlobal
     * @return A set of Mods with all their data imported and ready to be
     * manipulated.
     * @throws IOException
     */
    public Set<Mod> importActiveMods() throws IOException {
	return importActiveMods(GRUP_TYPE.values());
    }

    /**
     * Loads in plugins.txt and reads in the mods the user has active, and loads
     * only those that are also present in the data folder. Imports only GRUPS
     * specified in the parameter. If a mod being read in fits the criteria of
     * isModToSkip(), then it will be omitted in the results.<br><br> If the
     * program cannot locate plugins.txt, it will prompt the user to locate the
     * file themselves. Once they do, it will create a file and use that as a
     * reference for future patch generations.<br><br> Related settings in
     * SPGlobal:<br> - pluginsListPath<br> - pathToData<br> -
     * pluginListBackupPath
     *
     * @see SPGlobal
     * @param grup_targets Any amount of GRUP targets, separated by commas, that
     * you wish to import.
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     * @throws IOException
     */
    public Set<Mod> importActiveMods(GRUP_TYPE... grup_targets) throws IOException {
	return importMods(getActiveModList(), SPGlobal.pathToData, grup_targets);
    }

    /**
     * Loads in plugins.txt and reads in the mods the user has active, and loads
     * only those that are also present in the data folder. Imports only GRUPS
     * specified in the parameter. If a mod being read in fits the criteria of
     * isModToSkip(), then it will be omitted in the results.<br><br> If the
     * program cannot locate plugins.txt, it will prompt the user to locate the
     * file themselves. Once they do, it will create a file and use that as a
     * reference for future patch generations.<br><br> Related settings in
     * SPGlobal:<br> - pluginsListPath<br> - pathToData<br> -
     * pluginListBackupPath
     *
     * @see SPGlobal
     * @param grup_targets An arraylist of GRUP_TYPE with the desired types to
     * import
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     * @throws IOException
     */
    public Set<Mod> importActiveMods(ArrayList<GRUP_TYPE> grup_targets) throws IOException {
	GRUP_TYPE[] tmp = new GRUP_TYPE[0];
	return importMods(getActiveModList(), SPGlobal.pathToData, grup_targets.toArray(tmp));
    }

    /**
     * Looks for mods that match the given ModListings inside the data folder.
     * It imports any that are properly located, and loads in only GRUPS
     * specified in the parameter.<br><br> Related settings in SPGlobal:<br> -
     * pathToData<br>
     *
     * @see SPGlobal
     * @param mods ModListings to look for and import from the data folder.
     * @param grup_targets Any amount of GRUP targets, separated by commas, that
     * you wish to import.
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importMods(ArrayList<ModListing> mods, GRUP_TYPE... grup_targets) {
	return importMods(mods, SPGlobal.pathToData, grup_targets);
    }

    /**
     * Looks for mods that match the given ModListings inside the data folder.
     * It imports any that are properly located, and loads in only GRUPS
     * specified in the parameter.<br><br> Related settings in SPGlobal:<br> -
     * pathToData<br>
     *
     * @see SPGlobal
     * @param mods ModListings to look for and import from the data folder.
     * @param grup_targets An arraylist of GRUP_TYPE with the desired types to
     * import
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importMods(ArrayList<ModListing> mods, ArrayList<GRUP_TYPE> grup_targets) {
	GRUP_TYPE[] tmp = new GRUP_TYPE[0];
	return importMods(mods, SPGlobal.pathToData, grup_targets.toArray(tmp));
    }

    /**
     * Looks for mods that match the given ModListings inside the data folder.
     * It imports any that are properly located, and loads ALL GRUPs supported
     * by SkyProc.<br><br> NOTE: It is suggested for speed reasons to only
     * import the GRUP types you are interested in by using other import
     * functions.<br><br> Related settings in SPGlobal:<br> - pathToData
     *
     * @see SPGlobal
     * @param mods ModListings to look for and import from the data folder.
     * @return A set of Mods with all GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importMods(ArrayList<ModListing> mods) {
	return importMods(mods, SPGlobal.pathToData, GRUP_TYPE.values());
    }

    /**
     * Looks for mods that match the given ModListings in the path specified. It
     * imports any that are properly located, and loads ALL GRUPs supported by
     * SkyProc.<br><br> NOTE: It is suggested for speed reasons to only import
     * the GRUP types you are interested in by using other import functions.
     *
     * @param mods ModListings to look for and import from the data folder.
     * @param path Path from patch location to where to load mods from.
     * @return A set of Mods with all GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importMods(ArrayList<ModListing> mods, String path) {
	return importMods(mods, path, GRUP_TYPE.values());
    }

    /**
     * Looks for mods that match the given ModListings in the path specified. It
     * imports any that are properly located, and loads in only GRUPS specified
     * in the parameter.
     *
     * @param mods ModListings to look for and import from the data folder.
     * @param path Path from patch location to where to load mods from.
     * @param grup_targets Any amount of GRUP targets, separated by commas, that
     * you wish to import.
     * @return A set of Mods with specified GRUPs imported and ready to be
     * manipulated.
     */
    public Set<Mod> importMods(ArrayList<ModListing> mods, String path, GRUP_TYPE... grup_targets) {

	SPGlobal.sync(true);
	if (SPGlobal.logging()) {
	    SPGlobal.logMain(header, "Starting import of targets: ");
	    String grups = "";
	    for (GRUP_TYPE g : grup_targets) {
		grups += "   " + g.toString() + " ";
	    }
	    SPGlobal.logMain(header, grups);
	    SPGlobal.logMain(header, "In mods: ");
	    for (ModListing m : mods) {
		SPGlobal.logMain(header, "   " + m.print());
	    }

	}
	String header = "Import Mods";
	String debugPath = "Mod Import/";

	Set<Mod> outSet = new TreeSet<Mod>();

	curMod = 1;
	maxMod = mods.size();
	SPProgressBarPlug.reset();
	SPProgressBarPlug.setMax(mods.size() * (grup_targets.length + extraStepsPerMod), "Importing plugins.");

	for (int i = 0; i < mods.size(); i++) {
	    String mod = mods.get(i).print();
	    int curBar = SPProgressBarPlug.getBar();
	    SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(mods.get(i)));
	    if (!SPGlobal.modsToSkip.contains(new ModListing(mod))) {
		SPGlobal.newSyncLog(debugPath + Integer.toString(i) + " - " + mod + ".txt");
		try {
		    outSet.add(importMod(new ModListing(mod), path, true, grup_targets));
		} catch (BadMod ex) {
		    SPGlobal.logError(header, "Skipping a bad mod: " + mod);
		    SPGlobal.logError(header, "  " + ex.toString());
		} catch (Exception e) {
		    SPGlobal.logError(header, "Exception occured while importing mod : " + mod);
		    SPGlobal.logError(header, "  Message: " + e);
		    SPGlobal.logError(header, "  Stack: ");
		    for (StackTraceElement s : e.getStackTrace()) {
			SPGlobal.logError(header, "  " + s.toString());
		    }
		}
	    } else {
		SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(mods.get(i)) + ": Skipped!");
	    }
	    SPProgressBarPlug.setBar(curBar + (grup_targets.length + extraStepsPerMod));
	    curMod++;
	}

	if (SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Done Importing Mods.");
	    SPGlobal.logMain(header, "Done Importing Mods.");
	}
	SPGlobal.sync(false);
	return outSet;
    }

    /**
     * Looks for a mod matching the ModListing inside the given path. If
     * properly located, it imports only GRUPS specified in the parameter.
     *
     * @param listing Mod name and suffix to look for.
     * @param path Path to look for the mod data.
     * @param grup_targets Any amount of GRUP targets, separated by commas, that
     * you wish to import.
     * @return A mod with the specified GRUPs imported and ready to be
     * manipulated.
     * @throws BadMod If SkyProc runs into any unexpected data structures, or
     * has any error importing a mod at all.
     */
    public Mod importMod(ModListing listing, String path, GRUP_TYPE... grup_targets) throws BadMod {
	curMod = 1;
	maxMod = 1;
	SPProgressBarPlug.reset();
	SPProgressBarPlug.setMax(grup_targets.length + extraStepsPerMod);
	return importMod(listing, path, true, grup_targets);
    }

    /**
     * Looks for a mod matching the ModListing inside the given path. If
     * properly located, it imports only GRUPS specified in the parameter.
     *
     * @param listing Mod name and suffix to look for.
     * @param path Path to look for the mod data.
     * @param grup_targets An ArrayList of GRUP targets that you wish to import.
     * @return A mod with the specified GRUPs imported and ready to be
     * manipulated.
     * @throws BadMod If SkyProc runs into any unexpected data structures, or
     * has any error importing a mod at all.
     *
     * public Mod importMod(ModListing listing, String path,
     * ArrayList<GRUP_TYPE> grup_targets) throws BadMod { GRUP_TYPE[] types =
     * new GRUP_TYPE[grup_targets.size()]; types = grup_targets.toArray(types);
     * return importMod(listing, path, types); }
     */
    Mod importMod(ModListing listing, String path, Boolean addtoDb, GRUP_TYPE... grup_targets) throws BadMod {
	int curBar = SPProgressBarPlug.getBar();
	try {
	    ArrayList<GRUP_TYPE> grups = new ArrayList<>(Arrays.asList(grup_targets));

	    SPGlobal.logSync(header, "Opening filestream to mod: " + listing.print());
	    RecordFileChannel input = new RecordFileChannel(path + listing.print());
	    Mod plugin = new Mod(listing, extractHeaderInfo(input));
	    plugin.input = input;
	    if (SPGlobal.streamMode) {
		plugin.input = input;
	    }

	    if (plugin.isFlag(Mod.Mod_Flags.STRING_TABLED)) {
		importStrings(plugin);
	    }

	    ArrayList<Type> typeTargets = new ArrayList<>();
	    for (GRUP_TYPE g : grup_targets) {
		if (!GRUP_TYPE.unfinished(g)) {
		    typeTargets.add(Type.toRecord(g));
		}
	    }

	    Type result;
	    while (!Type.NULL.equals((result = scanToRecordStart(input, typeTargets)))) {
		SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(listing) + ": " + result);
		SPGlobal.logSync(header, "================== Loading in GRUP " + result + ": ", plugin.getName(), "===================");
		plugin.parseData(result, extractGRUPData(input));
		typeTargets.remove(result);
		SPGlobal.flush();
		SPProgressBarPlug.incrementBar();
		if (grups.isEmpty()) {
		    break;
		}
	    }

	    SPProgressBarPlug.setBar(curBar + grup_targets.length);
	    SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(listing) + ": Standardizing");
	    plugin.fetchStringPointers();
	    plugin.standardizeMasters();
	    SPProgressBarPlug.incrementBar();

	    if (addtoDb) {
		SPGlobal.getDB().add(plugin);
	    }

	    if (!SPGlobal.streamMode) {
		input.close();
	    }
	    SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(listing) + ": Done");

	    return plugin;
	} catch (Exception e) {
	    SPGlobal.logException(e);
	    SPProgressBarPlug.setStatus(curMod, maxMod, genStatus(listing) + ": Failed");
	    SPProgressBarPlug.setBar(curBar + grup_targets.length + extraStepsPerMod);
	    throw new BadMod("Ran into an exception, check SPGlobal.logs for more details.");
	}

    }

    static ByteBuffer extractHeaderInfo(LFileChannel in) throws BadMod, IOException {
	if (Ln.arrayToString(in.extractInts(0, 4)).equals("TES4")) {
	    int size = Ln.arrayToInt(in.extractInts(0, 4)) + 24;  // +24 for TES4 extra info
	    in.skip(-8); // To start of TES4 header
	    return in.extractByteBuffer(0, size);
	} else {
	    throw new BadMod("Mod did not have TES4 at the start of the file.");
	}
    }

    static void importStrings(Mod mod) {
	String header = "Importing Strings";
	if (SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Importing Strings");
	}
	for (Files f : SubStringPointer.Files.values()) {
	    try {
		importStrings(mod, f);
	    } catch (Exception e) {
		SPGlobal.logError(header, "Error Importing Strings " + f + ": " + e);
	    }
	}
    }

    static void importStrings(Mod plugin, SubStringPointer.Files file) throws FileNotFoundException, IOException, DataFormatException {

	String strings = pathToStringFile(plugin, file);
	File stringsFile = new File(SPGlobal.pathToData + strings);
	LShrinkArray in;
	int numRecords;
	int recordsSize;

	// Open file
	if (stringsFile.isFile()) {
	    LFileChannel istream = new LFileChannel(stringsFile);
	    // Read header
	    numRecords = istream.extractInt(0, 4);
	    recordsSize = numRecords * 8 + 8;
	    in = new LShrinkArray(istream.extractByteBuffer(4, recordsSize));
	} else if (BSA.hasBSA(plugin)) {
	    //In BSA
	    BSA bsa = BSA.getBSA(plugin);
	    bsa.loadFolders();
	    if (bsa.hasFile(strings)) {
		in = bsa.getFile(strings);
	    } else {
		SPGlobal.logError(header, plugin.toString() + " had no Strings file in BSA.");
		return;
	    }
	    numRecords = in.extractInt(4);
	    recordsSize = numRecords * 8 + 8;
	    //Skip bytes 4-8
	    in.skip(4);
	} else {
	    SPGlobal.logError(header, plugin.toString() + " did not have Strings files (loose or in BSA).");
	    return;
	}

	// Read entry pairs
	for (int i = 0; i < numRecords; i++) {
	    plugin.strings.get(file).put(in.extractInt(4),
		    in.extractInt(4) + recordsSize);
	}
    }

    static String pathToStringFile(Mod plugin, SubStringPointer.Files file) {
	return "Strings\\" + plugin.getName().substring(0, plugin.getName().indexOf(".es")) + "_" + SPGlobal.language + "." + file;
    }

    static Type scanToRecordStart(LFileChannel in, ArrayList<Type> target) throws java.io.IOException {
	Type type;
	int size;

	while (in.available() >= 12) {
	    size = Ln.arrayToInt(in.extractInts(4, 4));
	    try {
		type = Type.valueOf(Ln.arrayToString(in.extractInts(0, 4)));
		for (Type t : target) {
		    if (t.equals(type)) {
			in.skip(-12); // Go to start of GRUP
			return type;
		    }
		}
	    } catch (java.lang.IllegalArgumentException e) {
		// In case the GRUP type isn't in program yet, we want to continue
	    }
	    // else skip GRUP
	    in.skip(size - 12);  // -12 for parts already read in
	}

	return Type.NULL;
    }

    static RecordShrinkArray extractGRUPData(LFileChannel in) throws IOException {
	return new RecordShrinkArray(in, getGRUPsize(in));
    }

    static int getGRUPsize(LFileChannel in) {
	int size = Ln.arrayToInt(in.extractInts(4, 4));
	if (SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Extract GRUP size: " + Ln.prettyPrintHex(size));
	}
	in.skip(-8); // Back to start of GRUP
	return size;
    }

    static private String genStatus(ModListing mod) {
	return "Importing " + mod.print();
    }
}
