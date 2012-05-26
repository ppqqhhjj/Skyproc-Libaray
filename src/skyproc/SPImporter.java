package skyproc;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import lev.LFileChannel;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.MajorRecord.Mask;
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
    Map<Type, Mask> masks = new EnumMap<Type, Mask>(Type.class);
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
     * Adds the specified mask to SPImporter as a filter.<br><br> Once the
     * importer has imported every record type allowed in the mask, it will move
     * on to the next record in the GRUP.
     *
     * @param m Mask filter to add to the importer
     */
    public void addMask(Mask m) {
	masks.put(m.type, m);
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

	    //Open Plugin file
	    ModFile = new BufferedReader(new FileReader(dataFolder));

	    try {
		String line = ModFile.readLine();
		ArrayList<String> lines = new ArrayList<String>();
		SPGlobal.logSync(header, "Loading in Active Plugins");
		File pluginName;

		//If Skyrim, add Skyrim.esm and Update.esm, as they
		//are automatically removed from the list, and assumed
		if (SPGlobal.gameName.equals("Skyrim")) {
		    lines.add("Skyrim.esm");
		    lines.add("Update.esm");
		}

		while (line != null) {
		    pluginName = new File(SPGlobal.pathToData + line);
		    if (!SPGlobal.modsToSkip.contains(new ModListing(line))) {
			if (pluginName.isFile()) {
			    if (!lines.contains(line)) {
				SPGlobal.logSync(header, "Adding mod: " + line);
				lines.add(line);
			    }
			} else if (SPGlobal.logging()) {
			    SPGlobal.logSync(header, "Mod didn't exist: ", line);
			}
		    } else if (SPGlobal.logging()) {
			SPGlobal.logSync(header, "Mod was on the list to skip: " + line);
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
	SPProgressBarPlug.progress.reset();
	SPProgressBarPlug.progress.setMax(mods.size() * (grup_targets.length + extraStepsPerMod), "Importing plugins.");

	for (int i = 0; i < mods.size(); i++) {
	    String mod = mods.get(i).print();
	    int curBar = SPProgressBarPlug.progress.getBar();
	    SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(mods.get(i)));
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
		SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(mods.get(i)) + ": Skipped!");
	    }
	    SPProgressBarPlug.progress.setBar(curBar + (grup_targets.length + extraStepsPerMod));
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
	SPProgressBarPlug.progress.reset();
	SPProgressBarPlug.progress.setMax(grup_targets.length + extraStepsPerMod);
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
     */
    public Mod importMod(ModListing listing, String path, ArrayList<GRUP_TYPE> grup_targets) throws BadMod {
	GRUP_TYPE[] types = new GRUP_TYPE[grup_targets.size()];
	types = grup_targets.toArray(types);
	return importMod(listing, path, types);
    }

    Mod importMod(ModListing listing, String path, Boolean addtoDb, GRUP_TYPE... grup_targets) throws BadMod {
	int curBar = SPProgressBarPlug.progress.getBar();
	try {
	    ArrayList<GRUP_TYPE> grups = new ArrayList<GRUP_TYPE>(Arrays.asList(grup_targets));

	    SPGlobal.logSync(header, "Opening filestream to mod: " + listing.print());
	    LFileChannel input = new LFileChannel(path + listing.print());
	    Mod plugin = new Mod(listing, extractHeaderInfo(input));

	    if (plugin.isFlag(Mod.Mod_Flags.STRING_TABLED)) {
		importStrings(plugin);
	    }

	    ArrayList<Type> typeTargets = new ArrayList<Type>();
	    for (GRUP_TYPE g : grup_targets) {
		typeTargets.add(Type.toRecord(g));
	    }

	    Type result;
	    while (!Type.NULL.equals((result = scanToRecordStart(input, typeTargets)))) {
		SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(listing) + ": " + result);
		SPGlobal.logSync(header, "================== Loading in GRUP " + result + ": ", plugin.getName(), "===================");
		plugin.parseData(result, extractGRUPData(input), masks);
		typeTargets.remove(result);
		SPGlobal.flush();
		SPProgressBarPlug.progress.incrementBar();
		if (grups.isEmpty()) {
		    break;
		}
	    }

	    SPProgressBarPlug.progress.setBar(curBar + grup_targets.length);
	    SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(listing) + ": Standardizing");
	    plugin.fetchStringPointers();
	    plugin.standardizeMasters();
	    SPProgressBarPlug.progress.incrementBar();
	    input.close();

	    if (addtoDb) {
		SPGlobal.getDB().add(plugin);
	    }

	    SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(listing) + ": Done");

	    return plugin;
	} catch (Exception e) {
	    SPGlobal.logException(e);
	    SPProgressBarPlug.progress.setStatus(curMod, maxMod, genStatus(listing) + ": Failed");
	    SPProgressBarPlug.progress.setBar(curBar + grup_targets.length + extraStepsPerMod);
	    throw new BadMod("Ran into an exception, check SPGlobal.logs for more details.");
	}

    }

    static ByteBuffer extractHeaderInfo(LFileChannel in) throws BadMod, IOException {
	if (Ln.arrayToString(in.readInInts(0, 4)).equals("TES4")) {
	    int size = Ln.arrayToInt(in.readInInts(0, 4)) + 24;  // +24 for TES4 extra info
	    in.offset(-8); // To start of TES4 header
	    return in.readInByteBuffer(0, size);
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

    static void importStrings(Mod plugin, SubStringPointer.Files file) throws FileNotFoundException, IOException {

	// Open file
	LFileChannel istream = new LFileChannel(pathToStringFile(plugin, file));

	// Read header
	int numRecords = istream.readInInt(0, 4);
	int recordsSize = numRecords * 8 + 8;
	LShrinkArray in = new LShrinkArray(istream.readInByteBuffer(4, recordsSize));

	// Read entry pairs
	for (int i = 0; i < numRecords; i++) {
	    plugin.strings.get(file).put(in.extractInt(4),
		    in.extractInt(4) + recordsSize);
	}
    }

    static String pathToStringFile(Mod plugin, SubStringPointer.Files file) {
	return SPGlobal.pathToData + "Strings/" + plugin.getName().substring(0, plugin.getName().indexOf(".es")) + "_" + SPGlobal.language + "." + file;
    }

    static Type scanToRecordStart(LFileChannel in, ArrayList<Type> target) throws java.io.IOException {
	Type type;
	int size;

	while (in.available() >= 12) {
	    size = Ln.arrayToInt(in.readInInts(4, 4));
	    try {
		type = Type.valueOf(Ln.arrayToString(in.readInInts(0, 4)));
		for (Type t : target) {
		    if (t.equals(type)) {
			in.offset(-12); // Go to start of GRUP
			return type;
		    }
		}
	    } catch (java.lang.IllegalArgumentException e) {
		// In case the GRUP type isn't in program yet, we want to continue
	    }
	    // else skip GRUP
	    in.offset(size - 12);  // -12 for parts already read in
	}

	return Type.NULL;
    }

    static ByteBuffer extractGRUPData(LFileChannel in) throws IOException {
	int size = Ln.arrayToInt(in.readInInts(4, 4));
	if (SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Extract GRUP size: " + Ln.prettyPrintHex(size));
	}
	in.offset(-8); // Back to start of GRUP
	return in.readInByteBuffer(0, size);
    }

    static private String genStatus(ModListing mod) {
	return "Importing " + mod.print();
    }
}
