/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LFileChannel;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;

/**
 * An object that interfaces with BSA files, allowing for queries of its contents
 * and file data extraction.
 * @author Justin Swanson
 */
public class BSA {

    static Map<String, BSA> BSAs = new HashMap<String,BSA>();
    static String header = "BSA";
    String filePath;
    int offset;
    LFlags archiveFlags;
    int folderCount;
    int fileCount;
    int folderNameLength;
    int fileNameLength;
    LFlags fileFlags;
    Map<String, Map<String, BSAFileRef>> folders;
    LFileChannel in = new LFileChannel();

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
	    SPGlobal.log(header, "hasDirectoryNames: " + archiveFlags.is(0) + ", hasFileNames: " + archiveFlags.is(1) + ", compressed: " + archiveFlags.is(2));
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
	if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
	    SPGlobal.log(header, "Loaded " + fileCounter + " files.");
	}
    }

    /**
     * 
     * @param filePath filepath to query for and retrieve.
     * @return ShrinkArray of the raw data from the BSA of the file specified, already decompressed if applicable; Empty ShrinkArray if the file did not exist.
     * @throws IOException
     * @throws DataFormatException
     */
    public LShrinkArray getFile(String filePath) throws IOException, DataFormatException {
	BSAFileRef ref;
	if ((ref = getFileRef(filePath)) != null) {
	    in.pos(ref.dataOffset);
	    LShrinkArray out = new LShrinkArray(in.readInByteBuffer(0, ref.size));
	    if (archiveFlags.is(2)) {
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
	String out;
	File file = new File(filePath);
	if (!(file = Ln.getFilepathCaseInsensitive(file)).getPath().equals("")) {
	    return file.getName();
	}
	for (BSA b : BSAs.values()) {
	    out = b.getFilename(filePath);
	    if (!out.equals("")) {
		return out;
	    }
	}
	return "";
    }

    /**
     * 
     * @param filePath File to query for.
     * @return The used file, which prioritizes loose files first, and then BSAs.<br>
     * NOTE:  Not fully sophisticated yet for prioritizing between BSAs.
     * @throws IOException
     * @throws DataFormatException
     */
    static public LShrinkArray getUsedFile(String filePath) throws IOException, DataFormatException {
	File outsideBSA = new File(SPGlobal.pathToData + filePath);
	if (outsideBSA.isFile()) {
	    SPGlobal.log(header, "  Nif " + outsideBSA.getPath() + " loaded from outside BSA.");
	    return new LShrinkArray(outsideBSA);
	} else {
	    for (BSA b : BSAs.values()) {
		if (b.contains(BSA.FileType.NIF) && b.hasFile(filePath)) {
		    SPGlobal.log(header, "  Nif " + filePath + " loaded from BSA " + b.getFilePath());
		    return b.getFile(filePath);
		}
	    }
	}
	return null;
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
     * @return Map containing folder paths as keys, and list of file paths as values.
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
     * @param fileType Filetype to query for.
     * @return True if BSA contains files of that type.
     */
    public boolean contains(FileType fileType) {
	return fileFlags.is(fileType.ordinal());
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
     */
    public static ArrayList<BSA> loadInBSAs(FileType... types) {
	File data = new File(SPGlobal.pathToData);
	ArrayList<BSA> out = new ArrayList<BSA>();
	for (File f : data.listFiles()) {
	    if (f.isFile() && f.getPath().toUpperCase().endsWith(".BSA")
		    && !f.getName().equalsIgnoreCase("ArchiveInvalidationInvalidated!.bsa")) {
		try {
		    BSA tmp = new BSA(f.getPath(), false);
		    if (tmp.containsAny(types)) {
			tmp.loadFolders();
			out.add(tmp);
			SPGlobal.log(header, "Loaded BSA: " + f.getPath());
		    }
		} catch (Exception e) {
		    SPGlobal.logException(e);
		    SPGlobal.logError(header, "Error loading in BSA file: " + f.getPath());
		}
	    }

	}
	return out;
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
