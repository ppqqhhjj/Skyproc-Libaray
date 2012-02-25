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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lev.LFileChannel;
import lev.LFlags;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
public class BSA {

    private static String header = "BSA";
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

    public BSA(String filePath) throws FileNotFoundException, IOException, BadParameter {
        this.filePath = filePath;
        in.openFile(filePath);
        if (!in.readInString(0, 3).equals("BSA") || in.readInInt(1, 4) != 104) {
            throw new BadParameter("Was not a BSA file of version 104.");
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
        loadFolders();
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
                f.offset = in.readInLong(0, 4);
                fileName = fileNames.extractString();
                files.put(fileName.toUpperCase(), f);
                if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
                    SPGlobal.log(header, "  " + fileName + ", size: " + Ln.prettyPrintHex(f.size) + ", offset: " + Ln.prettyPrintHex(f.offset));
                    fileCounter++;
                }
            }
        }
        if (SPGlobal.debugBSAimport && SPGlobal.logging()) {
            SPGlobal.log(header, "Loaded " + fileCounter + " files.");
        }
    }

    public LShrinkArray getFile(String filePath) throws IOException, DataFormatException {
        filePath = filePath.toUpperCase();
        BSAFileRef ref;
        if ((ref = getFileRef(filePath)) != null) {
            return extractFile(ref.offset, ref.size);
        }
        return new LShrinkArray(new byte[0]);
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

    public boolean hasFile(String filePath) {
        return getFileRef(filePath) != null;
    }

    public String getFilePath () {
        return filePath.substring(0, filePath.length());
    }

    public boolean hasFolder(String folderPath) {
        filePath = filePath.toUpperCase();
        if (folders.containsKey(folderPath)) {
            return true;
        } else {
            return false;
        }
    }

    public Set<String> getFolders () {
        return folders.keySet();
    }

    public Map<String, ArrayList<String>> getFiles () {
        Map<String, ArrayList<String>> out = new HashMap<String, ArrayList<String>> (folders.size());
        for (String folder : folders.keySet()) {
            out.put(folder, new ArrayList<String>(folders.get(folder).values().size()));
            for (String file : folders.get(folder).keySet()) {
                out.get(folder).add(file);
            }
        }
        return out;
    }

    LShrinkArray extractFile(long offset, int size) throws IOException, DataFormatException {
        in.pos(offset);
        LShrinkArray out = new LShrinkArray(in.readInByteBuffer(0, size));
        if (archiveFlags.is(2)) {
            out.correctForCompression();
        }
        return out;
    }

    public boolean contains(FileType fileType) {
        return fileFlags.is(fileType.ordinal());
    }

    public static ArrayList<BSA> loadInBSAs(FileType ... types) {
        File data = new File(SPGlobal.pathToData);
        ArrayList<BSA> out = new ArrayList<BSA>();
        for (File f : data.listFiles()) {
            if (f.isFile() && f.getPath().toUpperCase().endsWith(".BSA")) {
                try {
                    out.add(new BSA(f.getPath()));
                    SPGlobal.log(header, "Loaded BSA: " + f.getPath());
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
        long offset;
    }

    public enum FileType {
        NIF,
        DDS,
        XML,
        WAV,
        MP3,
        TXT_HTML_BAT_SCC,
        SPT,
        TEX_FNT,
        CTL
    }
}
