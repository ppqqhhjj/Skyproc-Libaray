/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    static Map<ModListing, BSA> pluginLoadOrder = new TreeMap<>();
    static boolean pluginsLoaded = false;
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
    boolean bad = false;
    Map<String, Map<String, BSAFileRef>> folders;
    LFileChannel in = new LFileChannel();

    BSA(File file, boolean load) throws FileNotFoundException, IOException, BadParameter {
	this(file.getPath(), load);
    }

    BSA(String filePath, boolean load) throws FileNotFoundException, IOException, BadParameter {
	this.filePath = filePath;
	in.openFile(filePath);
	if (!in.extractString(0, 3).equals("BSA") || in.extractInt(1, 4) != 104) {
	    throw new BadParameter("Was not a BSA file of version 104: " + filePath);
	}
	offset = in.extractInt(0, 4);
	archiveFlags = new LFlags(in.extract(0, 4));
	folderCount = in.extractInt(0, 4);
	folders = new HashMap<>(folderCount);
	fileCount = in.extractInt(0, 4);
	folderNameLength = in.extractInt(0, 4);
	fileNameLength = in.extractInt(0, 4);
	fileFlags = new LFlags(in.extract(0, 4));
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

    final void loadFolders() {
	if (loaded) {
	    return;
	}
	loaded = true;
	try {
	    String fileName, folderName;
	    int fileCounter = 0;
	    in.pos(offset);
	    LShrinkArray folderData = new LShrinkArray(in.extract(0, folderCount * 16));
	    in.pos(folderNameLength + fileCount * 16 + folderCount * 17 + offset);
	    LShrinkArray fileNames = new LShrinkArray(in.extract(0, fileNameLength));
	    for (int i = 0; i < folderCount; i++) {
		folderData.skip(8); // Skip Hash
		int count = folderData.extractInt(4);
		Map<String, BSAFileRef> files = new HashMap<>(count);
		int fileRecordOffset = folderData.extractInt(4);

		in.pos(fileRecordOffset - fileNameLength);
		folderName = in.extractString(0, in.read() - 1) + "\\";
		in.skip(1);
		folders.put(folderName.toUpperCase(), files);
		if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
		    SPGlobal.log(header, "Loaded folder: " + folderName);
		}
		for (int j = 0; j < count; j++) {
		    BSAFileRef f = new BSAFileRef();
		    f.size = in.extractInt(8, 4); // Skip Hash
		    f.dataOffset = in.extractLong(0, 4);
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
	} catch (Exception e) {
	    SPGlobal.logException(e);
	    SPGlobal.logError("BSA", "Skipped BSA " + this);
	    bad = true;
	}
    }

    public boolean loaded() {
	return loaded;
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
	    in.pos(getFileLocation(ref));
	    LShrinkArray out = new LShrinkArray(in.extract(0, ref.size));
	    if (archiveFlags.get(2)) {
		out = out.correctForCompression();
	    }
	    return out;
	}
	return new LShrinkArray(new byte[0]);
    }

    long getFileLocation(BSAFileRef ref) {
	return ref.dataOffset;
    }

    public long getFileLocation(String filePath) {
	BSAFileRef ref;
	if ((ref = getFileRef(filePath)) != null) {
	    return getFileLocation(ref);
	}
	return -1;
    }

    public long getFileLocation(File f) {
	return getFileLocation(f.getPath());
    }

    public LShrinkArray getFile(File f) throws IOException, DataFormatException {
	return getFile(f.getPath());
    }

    String getFilename(String filePath) throws IOException {
	BSAFileRef ref;
	if ((ref = getFileRef(filePath)) != null) {
	    in.pos(ref.nameOffset);
	    return in.extractString();
	}
	return "";
    }

    static String getUsedFilename(String filePath) throws IOException {
	String tmp, out = "";
	File file = new File(filePath);
	if (!(file = Ln.getFilepathCaseInsensitive(file)).getPath().equals("")) {
	    return file.getName();
	}
	Iterator<BSA> bsas = BSA.iterator();
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
	    SPGlobal.log(header, "  Nif " + outsideBSA.getPath() + " loaded from loose files.");
	    return new LShrinkArray(outsideBSA);
	} else {
	    Iterator<BSA> bsas = BSA.iterator();
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

    static void loadPluginLoadOrder() {
	if (pluginsLoaded) {
	    return;
	}
	if (SPGlobal.logging()) {
	    SPGlobal.log(header, "Loading in active plugin BSA headers.");
	}
	try {
	    ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
	    for (ModListing m : activeMods) {
		if (!pluginLoadOrder.containsKey(m)) {
		    BSA bsa = getBSA(m);
		    if (bsa != null) {
			pluginLoadOrder.put(m, bsa);
		    }
		}
	    }
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}

	pluginsLoaded = true;
    }

    static void loadResourceLoadOrder() {
	if (resourceLoadOrder != null) {
	    return;
	}
	try {
	    ArrayList<String> resources = new ArrayList<>();
	    boolean line1 = false, line2 = false;
	    try {
		File ini = SPGlobal.getSkyrimINI();

		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "Loading in BSA list from Skyrim.ini: " + ini);
		}
		LFileChannel input = new LFileChannel(ini);

		String line = "";
		// First line
		while (input.available() > 0 && !line.toUpperCase().contains("SRESOURCEARCHIVELIST")) {
		    line = input.extractLine();
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
		    line = Ln.cleanLine(input.extractLine(), "#");
		}
		if (line.toUpperCase().contains("SRESOURCEARCHIVELIST2")) {
		    line2 = true;
		    resources.addAll(processINIline(line));
		} else {
		    line1 = true;
		    resources.addAll(0, processINIline(line));
		}
	    } catch (Exception e) {
		SPGlobal.logException(e);
	    }

	    if (!line1 || !line2) {
		//Assume standard BSA listing
		if (!resources.contains("Skyrim - Misc.bsa")) {
		    resources.add("Skyrim - Misc.bsa");
		}

		if (!resources.contains("Skyrim - Shaders.bsa")) {
		    resources.add("Skyrim - Shaders.bsa");
		}

		if (!resources.contains("Skyrim - Textures.bsa")) {
		    resources.add("Skyrim - Textures.bsa");
		}

		if (!resources.contains("Skyrim - Interface.bsa")) {
		    resources.add("Skyrim - Interface.bsa");
		}

		if (!resources.contains("Skyrim - Animations.bsa")) {
		    resources.add("Skyrim - Animations.bsa");
		}

		if (!resources.contains("Skyrim - Meshes.bsa")) {
		    resources.add("Skyrim - Meshes.bsa");
		}

		if (!resources.contains("Skyrim - Sounds.bsa")) {
		    resources.add("Skyrim - Sounds.bsa");
		}

		if (!resources.contains("Skyrim - Sounds.bsa")) {
		    resources.add("Skyrim - Voices.bsa");
		}

		if (!resources.contains("Skyrim - Sounds.bsa")) {
		    resources.add("Skyrim - VoicesExtra.bsa");
		}
	    }

	    if (SPGlobal.logging()) {
		SPGlobal.log(header, "BSA resource load order: ");
		for (String s : resources) {
		    SPGlobal.log(header, "  " + s);
		}
		SPGlobal.log(header, "Loading in their headers.");
	    }

	    resourceLoadOrder = new ArrayList<>(resources.size());
	    for (String s : resources) {
		File bsaPath = new File(SPGlobal.pathToData + s);
		if (bsaPath.exists()) {
		    try {
			BSA bsa = new BSA(bsaPath, false);
			resourceLoadOrder.add(bsa);
		    } catch (BadParameter | FileNotFoundException ex) {
			SPGlobal.logException(ex);
		    }
		} else if (SPGlobal.logging()) {
		    SPGlobal.log(header, "  BSA skipped because it didn't exist: " + bsaPath);
		}
	    }

	} catch (IOException ex) {
	    SPGlobal.logException(ex);
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

    public boolean hasFile(File f) {
	return hasFile(f.getPath());
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
	Map<String, ArrayList<String>> out = new HashMap<>(folders.size());
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
	if (!fileFlags.isZeros()) {
	    return fileFlags.get(fileType.ordinal());
	} else {
	    return manualContains(fileType);
	}
    }

    boolean manualContains(FileType fileType) {
	FileType[] types = new FileType[1];
	types[0] = fileType;
	return manualContains(types);
    }

    boolean manualContains(FileType[] fileTypes) {
	loadFolders();
	for (String folder : folders.keySet()) {
	    for (String file : folders.get(folder).keySet()) {
		for (FileType type : fileTypes) {
		    if (file.endsWith(type.toString())) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    /**
     *
     * @param fileTypes Filetypes to query for.
     * @return True if BSA contains any of the filetypes.
     */
    public boolean containsAny(FileType[] fileTypes) {
	if (!fileFlags.isZeros()) {
	    for (FileType f : fileTypes) {
		if (contains(f)) {
		    return true;
		}
	    }
	    return false;
	} else {
	    return manualContains(fileTypes);
	}
    }

    /**
     *
     * @param types Types to load in.
     * @return List of all BSA files that contain any of the filetypes.
     * @throws IOException
     * @throws BadParameter If Skyrim.ini does not have the BSA load order lines
     */
    public static ArrayList<BSA> loadInBSAs(FileType... types) {
	loadResourceLoadOrder();
	loadPluginLoadOrder();
	deleteOverlap();
	ArrayList<BSA> out = new ArrayList<>();
	Iterator<BSA> bsas = iterator();
	while (bsas.hasNext()) {
	    BSA tmp = bsas.next();
	    try {
		if (!tmp.bad && tmp.containsAny(types)) {
		    tmp.loadFolders();
		    out.add(tmp);
		}
	    } catch (Exception e) {
		SPGlobal.logException(e);
		SPGlobal.logError("BSA", "Skipped BSA " + tmp);
	    }
	}
	return out;
    }

    static void deleteOverlap() {
	for (BSA b : pluginLoadOrder.values()) {
	    resourceLoadOrder.remove(b);
	}
    }

    static Iterator<BSA> iterator() {
	return getBSAs().iterator();
    }

    static ArrayList<BSA> getBSAs() {
	ArrayList<BSA> order = new ArrayList<>(resourceLoadOrder.size() + pluginLoadOrder.size());
	order.addAll(resourceLoadOrder);
	order.addAll(pluginLoadOrder.values());
	return order;
    }

    static public BSA getBSA(ModListing m) {
	if (pluginLoadOrder.containsKey(m)) {
	    return pluginLoadOrder.get(m);
	}

	File bsaPath = new File(SPGlobal.pathToData + Ln.changeFileTypeTo(m.print(), "bsa"));
	if (bsaPath.exists()) {
	    try {
		BSA bsa = new BSA(bsaPath, false);
		pluginLoadOrder.put(m, bsa);
		return bsa;
	    } catch (IOException | BadParameter ex) {
		SPGlobal.logException(ex);
		return null;
	    }
	}

	if (SPGlobal.logging()) {
	    SPGlobal.log(header, "  BSA skipped because it didn't exist: " + bsaPath);
	}
	return null;
    }

    static public BSA getBSA(Mod m) {
	return getBSA(m.getInfo());
    }

    static public boolean hasBSA(ModListing m) {
	File bsaPath = new File(SPGlobal.pathToData + Ln.changeFileTypeTo(m.print(), "bsa"));
	return bsaPath.exists();
    }

    static public boolean hasBSA(Mod m) {
	return hasBSA(m.getInfo());
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final BSA other = (BSA) obj;
	if (!Objects.equals(this.filePath, other.filePath)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 19 * hash + Objects.hashCode(this.filePath);
	return hash;
    }

    class BSAFileRef {

	int size;
	long nameOffset;
	long dataOffset;
    }

    @Override
    public String toString() {
	return filePath;
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
