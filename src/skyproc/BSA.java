/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import lev.LFileChannel;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;

/**
 * An object that interfaces with BSA files, allowing for queries of its
 * contents and file data extraction.
 *
 * @author Justin Swanson
 */
public class BSA {

    static ArrayList<BSA> resourceLoadOrder;
    static ArrayList<BSA> pluginLoadOrder;
    static String header = "BSA";
    String filePath;
    int offset;
    LFlags archiveFlags;
    int folderCount;
    int fileCount;
    int folderNameLength;
    int fileNameLength;
    LFlags fileFlags;
    boolean loaded = false;
    Map<String, Map<String, BSAFileRef>> folders;
    LFileChannel in = new LFileChannel();

    BSA(File file, boolean load) throws FileNotFoundException, IOException, BadParameter {
	this(file.getPath(), load);
    }

    BSA(String filePath, boolean load) throws FileNotFoundException, IOException, BadParameter {
	this.filePath = filePath;
	in.openFile(filePath);
	if (!in.readInString(0, 3).equals("BSA") || in.readInInt(1, 4) != 104) {
	    throw new BadParameter("Was not a BSA file of version 104: " + filePath);
	}
	offset = in.readInInt(0, 4);
	archiveFlags = new LFlags(in.readInBytes(0, 4));
	folderCount = in.readInInt(0, 4);
	folders = new HashMap<String, Map<String, BSAFileRef>>(folderCount);
	fileCount = in.readInInt(0, 4);
	folderNameLength = in.readInInt(0, 4);
	fileNameLength = in.readInInt(0, 4);
	fileFlags = new LFlags(in.readInBytes(0, 4));
	if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
	    SPGlobal.log(header, "Imported " + filePath);
	    SPGlobal.log(header, "Offset " + offset + ", archiveFlags: " + archiveFlags);
	    SPGlobal.log(header, "hasDirectoryNames: " + archiveFlags.get(0) + ", hasFileNames: " + archiveFlags.get(1) + ", compressed: " + archiveFlags.get(2));
	    SPGlobal.log(header, "FolderCount: " + Ln.prettyPrintHex(folderCount) + ", FileCount: " + Ln.prettyPrintHex(fileCount));
	    SPGlobal.log(header, "totalFolderNameLength: " + Ln.prettyPrintHex(folderNameLength) + ", totalFileNameLength: " + Ln.prettyPrintHex(fileNameLength));
	    SPGlobal.log(header, "fileFlags: " + fileFlags.toString());
	}
	if (load) {
	    loadFolders();
	}
    }

    /**
     *
     * @param filePath Filepath to load BSA data from.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BadParameter If the BSA is malformed (by SkyProc standards)
     */
    public BSA(String filePath) throws FileNotFoundException, IOException, BadParameter {
	this(filePath, true);
    }

    final void loadFolders() throws IOException {
	if (loaded) {
	    return;
	}
	String fileName, folderName;
	int fileCounter = 0;
	in.pos(offset);
	LShrinkArray folderData = new LShrinkArray(in.readInByteBuffer(0, folderCount * 16));
	in.pos(folderNameLength + fileCount * 16 + folderCount * 17 + offset);
	LShrinkArray fileNames = new LShrinkArray(in.readInByteBuffer(0, fileNameLength));
	for (int i = 0; i < folderCount; i++) {
	    folderData.skip(8); // Skip Hash
	    int count = folderData.extractInt(4);
	    Map<String, BSAFileRef> files = new HashMap<String, BSAFileRef>(count);
	    int fileRecordOffset = folderData.extractInt(4);

	    in.pos(fileRecordOffset - fileNameLength);
	    folderName = in.readInString(0, in.read() - 1) + "\\";
	    in.offset(1);
	    folders.put(folderName.toUpperCase(), files);
	    if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
		SPGlobal.log(header, "Loaded folder: " + folderName);
	    }
	    for (int j = 0; j < count; j++) {
		BSAFileRef f = new BSAFileRef();
		f.size = in.readInInt(8, 4); // Skip Hash
		f.dataOffset = in.readInLong(0, 4);
		fileName = fileNames.extractString();
		files.put(fileName.toUpperCase(), f);
		if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
		    SPGlobal.log(header, "  " + fileName + ", size: " + Ln.prettyPrintHex(f.size) + ", offset: " + Ln.prettyPrintHex(f.dataOffset));
		    fileCounter++;
		}
	    }
	}
	if (SPGlobal.logging()) {
	    if (SPGlobal.debugBSAimport) {
		SPGlobal.log(header, "Loaded " + fileCounter + " files.");
	    }
	    SPGlobal.log(header, "Loaded BSA: " + getFilePath());
	}
	loaded = true;
    }

    /**
     *
     * @param filePath filepath to query for and retrieve.
     * @return ShrinkArray of the raw data from the BSA of the file specified,
     * already decompressed if applicable; Empty ShrinkArray if the file did not
     * exist.
     * @throws IOException
     * @throws DataFormatException
     */
    public LShrinkArray getFile(String filePath) throws IOException, DataFormatException {
	BSAFileRef ref;
	if ((ref = getFileRef(filePath)) != null) {
	    in.pos(ref.dataOffset);
	    LShrinkArray out = new LShrinkArray(in.readInByteBuffer(0, ref.size));
	    if (archiveFlags.get(2)) {
		out.correctForCompression();
	    }
	    return out;
	}
	return new LShrinkArray(new byte[0]);
    }

    String getFilename(String filePath) throws IOException {
	BSAFileRef ref;
	if ((ref = getFileRef(filePath)) != null) {
	    in.pos(ref.nameOffset);
	    return in.readString();
	}
	return "";
    }

    static String getUsedFilename(String filePath) throws IOException {
	String tmp, out = "";
	File file = new File(filePath);
	if (!(file = Ln.getFilepathCaseInsensitive(file)).getPath().equals("")) {
	    return file.getName();
	}
	Iterator<BSA> bsas = BSA.BSAsIterator();
	while (bsas.hasNext()) {
	    tmp = bsas.next().getFilename(filePath);
	    if (!tmp.equals("")) {
		out = tmp;
	    }
	}
	return out;
    }

    /**
     *
     * @param filePath File to query for.
     * @return The used file, which prioritizes loose files first, and then
     * BSAs.<br> NOTE: Not fully sophisticated yet for prioritizing between
     * BSAs.
     * @throws IOException
     * @throws DataFormatException
     */
    static public LShrinkArray getUsedFile(String filePath) throws IOException, DataFormatException {
	File outsideBSA = new File(SPGlobal.pathToData + filePath);
	if (outsideBSA.isFile()) {
	    SPGlobal.log(header, "  Nif " + outsideBSA.getPath() + " loaded from outside BSA.");
	    return new LShrinkArray(outsideBSA);
	} else {
	    Iterator<BSA> bsas = BSA.BSAsIterator();
	    BSA tmp, bsa = null;
	    while (bsas.hasNext()) {
		tmp = bsas.next();
		if (tmp.hasFile(filePath)) {
		    bsa = tmp;
		}
	    }
	    if (bsa != null) {
		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "  Nif " + filePath + " loaded from BSA " + bsa.getFilePath());
		}
		return bsa.getFile(filePath);
	    }
	}
	return null;
    }

    static void loadPluginLoadOrder() throws IOException {
	if (pluginLoadOrder != null) {
	    return;
	}
	if (SPGlobal.logging()) {
	    SPGlobal.log(header, "Loading in active plugin BSA headers.");
	}
	ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
	pluginLoadOrder = new ArrayList<BSA>();
	for (ModListing m : activeMods) {
	    File bsaPath = new File(SPGlobal.pathToData + Ln.changeFileTypeTo(m.print(), "bsa"));
	    if (bsaPath.exists()) {
		try {
		    BSA bsa = new BSA(bsaPath, false);
		    pluginLoadOrder.add(bsa);
		} catch (FileNotFoundException ex) {
		    SPGlobal.logException(ex);
		} catch (BadParameter ex) {
		    SPGlobal.logException(ex);
		}
	    } else if (SPGlobal.logging()) {
		SPGlobal.log(header, "  BSA skipped because it didn't exist: " + bsaPath);
	    }
	}
    }

    static void loadResourceLoadOrder() throws IOException, BadParameter {
	if (resourceLoadOrder != null) {
	    return;
	}
	File myDocuments = Ln.getMyDocuments();
	ArrayList<String> resources = new ArrayList<String>();
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
	if (SPGlobal.logging()) {
	    SPGlobal.log(header, "Loading in BSA list from Skyrim.ini: " + ini);
	}

	// Load it in
	try {
	    LFileChannel input = new LFileChannel(ini);
	    boolean line1 = false, line2 = false;

	    String line = "";
	    // First line
	    while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
		line = input.readLine();
	    }
	    if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
		line2 = true;
		resources.addAll(processINIline(line));
	    } else {
		line1 = true;
		resources.addAll(0, processINIline(line));
	    }

	    // Second line
	    line = "";
	    while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
		line = Ln.cleanLine(input.readLine(), "#");
	    }
	    if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
		line2 = true;
		resources.addAll(processINIline(line));
	    } else {
		line1 = true;
		resources.addAll(0, processINIline(line));
	    }

	    if (SPGlobal.logging()) {
		SPGlobal.log(header, "BSA resource load order: ");
		for (String s : resources) {
		    SPGlobal.log(header, "  " + s);
		}
		SPGlobal.log(header, "Loading in their headers.");
	    }

	    resourceLoadOrder = new ArrayList<BSA>(resources.size());
	    for (String s : resources) {
		File bsaPath = new File(SPGlobal.pathToData + s);
		if (bsaPath.exists()) {
		    try {
			BSA bsa = new BSA(bsaPath, false);
			resourceLoadOrder.add(bsa);
		    } catch (BadParameter ex) {
			SPGlobal.logException(ex);
		    } catch (FileNotFoundException ex) {
			SPGlobal.logException(ex);
		    }
		} else if (SPGlobal.logging()) {
		    SPGlobal.log(header, "  BSA skipped because it didn't exist: " + bsaPath);
		}
	    }

	    if (!line1 || !line2) {
		// If one of the lines is missing.
		throw new BadParameter("<html>Your Skyrim.ini file did not have BOTH lines: 'sResourceArchiveList' and 'sResourceArchiveList2'.<br>"
			+ "It cannot figure out which BSAs to load in.<br>"
			+ "Please confirm that those lines appear in your Skyrim.ini and have the proper BSAs listed.");
	    }

	} catch (FileNotFoundException ex) {
	    SPGlobal.logException(ex);
	    throw new BadParameter("BSA load order could not be properly established.  Check logs for more information.");
	}
    }

    static ArrayList<String> processINIline(String in) {
	ArrayList<String> out = new ArrayList<String>();
	int index = in.indexOf("=");
	if (index != -1) {
	    in = in.substring(index + 1);
	    String[] split = in.split(",");
	    for (String s : split) {
		out.add(s.trim());
	    }
	}
	return out;
    }

    BSAFileRef getFileRef(String filePath) {
	filePath = filePath.toUpperCase();
	int index = filePath.lastIndexOf('\\');
	String folder = filePath.substring(0, index + 1);
	if (hasFolder(folder)) {
	    String file = filePath.substring(index + 1);
	    Map<String, BSAFileRef> files = folders.get(folder);
	    if (files.containsKey(file)) {
		return files.get(file);
	    }
	}
	return null;
    }

    /**
     *
     * @param filePath Filepath the query for.
     * @return True if BSA has a file with that path.
     */
    public boolean hasFile(String filePath) {
	return getFileRef(filePath) != null;
    }

    /**
     *
     * @return The BSA's filepath.
     */
    public String getFilePath() {
	return filePath.substring(0, filePath.length());
    }

    /**
     *
     * @param folderPath Folder path to query for.
     * @return True if BSA has a folder with that path.
     */
    public boolean hasFolder(String folderPath) {
	filePath = filePath.toUpperCase();
	if (folders.containsKey(folderPath)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     *
     * @return A list of contained folders.
     */
    public Set<String> getFolders() {
	return folders.keySet();
    }

    /**
     *
     * @return Map containing folder paths as keys, and list of file paths as
     * values.
     */
    public Map<String, ArrayList<String>> getFiles() {
	Map<String, ArrayList<String>> out = new HashMap<String, ArrayList<String>>(folders.size());
	for (String folder : folders.keySet()) {
	    out.put(folder, new ArrayList<String>(folders.get(folder).values().size()));
	    for (String file : folders.get(folder).keySet()) {
		out.get(folder).add(file);
	    }
	}
	return out;
    }

    /**
     *
     * @return Number of folders contained in the BSA
     */
    public int numFolders() {
	return folders.size();
    }

    /**
     *
     * @return Number of files contained in the BSA
     */
    public int numFiles() {
	int out = 0;
	for (Map m : folders.values()) {
	    out += m.size();
	}
	return out;
    }

    /**
     *
     * @param fileType Filetype to query for.
     * @return True if BSA contains files of that type.
     */
    public boolean contains(FileType fileType) {
	return fileFlags.get(fileType.ordinal());
    }

    /**
     *
     * @param fileTypes Filetypes to query for.
     * @return True if BSA contains any of the filetypes.
     */
    public boolean containsAny(FileType[] fileTypes) {
	for (FileType f : fileTypes) {
	    if (contains(f)) {
		return true;
	    }
	}
	return false;
    }

    /**
     *
     * @param types Types to load in.
     * @return List of all BSA files that contain any of the filetypes.
     * @throws IOException
     * @throws BadParameter If Skyrim.ini does not have the BSA load order lines
     */
    public static ArrayList<BSA> loadInBSAs(FileType... types) throws IOException, BadParameter {
	loadResourceLoadOrder();
	loadPluginLoadOrder();
	ArrayList<BSA> out = new ArrayList<BSA>();
	Iterator<BSA> bsas = BSAsIterator();
	while (bsas.hasNext()) {
	    BSA tmp = bsas.next();
	    if (tmp.containsAny(types)) {
		tmp.loadFolders();
		out.add(tmp);
	    }
	}
	return out;
    }

    static Iterator<BSA> BSAsIterator() {
	ArrayList<BSA> order = new ArrayList<BSA>(resourceLoadOrder.size() + pluginLoadOrder.size());
	order.addAll(resourceLoadOrder);
	order.addAll(pluginLoadOrder);
	return order.iterator();
    }

    class BSAFileRef {

	int size;
	long nameOffset;
	long dataOffset;
    }

    /**
     * Enum containing all of the various filetypes BSAs could contain.
     */
    public enum FileType {

	/**
	 *
	 */
	NIF,
	/**
	 *
	 */
	DDS,
	/**
	 *
	 */
	XML,
	/**
	 *
	 */
	WAV,
	/**
	 *
	 */
	MP3,
	/**
	 *
	 */
	TXT_HTML_BAT_SCC,
	/**
	 *
	 */
	SPT,
	/**
	 *
	 */
	TEX_FNT,
	/**
	 *
	 */
	CTL
    }
}
