/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import lev.LInChannel;
import lev.LMergeMap;
import lev.LOutChannel;
import lev.LShrinkArray;

/**
 *
 * @author Justin Swanson
 */
class Consistency {

    static String header = "Consistency";
    static String consistencyFile;
    static Map<ModListing, Map<String, FormID>> storage = new HashMap<>();
    static Set<FormID> set = new HashSet<>();
    static LMergeMap<FormID, String> conflicts = new LMergeMap<>(false);
    static boolean cleaned = false;
    static boolean automaticExport = true;
    static boolean imported = false;
    static char[] badChars = {(char) 0x0D, (char) 0x0A};
    static String debugFolder = "Consistency/";

    static FormID getOldForm(String edid) {
	return storage.get(SPGlobal.getGlobalPatch().getInfo()).get(edid);
    }

    static boolean requestID(FormID id) {
	return !set.contains(id);
    }

    static void cleanConsistency() {
	cleaned = true;
    }

    static void syncIDwithEDID(String edid, MajorRecord m) {
	FormID saved = Consistency.getOldForm(edid);
	if (saved != null) {
	    // If have an ID on record
	    if (SPGlobal.logging()) {
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Assigning FormID " + saved + " for EDID " + edid);
	    }
	    m.setForm(saved);
	} else if (m.getForm().isNull()) {
	    // If fresh record in need of new ID
	    FormID freshID = getNextID(m.srcMod);
	    Consistency.insert(edid, freshID);
	    m.setForm(freshID);
	    if (SPGlobal.logging()) {
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Assigning FRESH FormID " + freshID + " for EDID " + edid);
	    }
	}
    }

    static public FormID getNextID(Mod srcMod) {
	FormID possibleID = srcMod.getNextID();
	while (!Consistency.requestID(possibleID)) {
	    if (possibleID.isNull()) {
		if (!Consistency.cleaned) {
		    srcMod.resetNextIDcounter();
		    Consistency.cleanConsistency();
		    return getNextID(srcMod);
		} else {
		    SPGlobal.logError(srcMod.toString(), "Ran out of available formids.");
		    JOptionPane.showMessageDialog(null, "<html>The output patch ran out of available FormIDs.<br>"
			    + "Please contact Leviathan1753.</html>");
		    return null;
		}
	    }
	    possibleID = srcMod.getNextID();
	}
	return possibleID;
    }

    static public String edidFilter(String edid) {
	edid = edid.replaceAll(" ", "");
	for (char c : badChars) {
	    edid = edid.replaceAll(String.valueOf(c), "");
	}
	return edid;
    }

    static void getConsistencyFile() throws FileNotFoundException, IOException {
	if (consistencyFile == null) {
	    File myDocs = SPGlobal.getSkyProcDocuments();
	    consistencyFile = myDocs.getPath() + "\\Consistency";
	}
    }

    static void clear() {
	storage.clear();
	set.clear();
	imported = false;
    }

    static void importConsistency(boolean globalOnly) {
	try {
	    if (!imported) {
		storage.put(SPGlobal.getGlobalPatch().getInfo(), new HashMap<String, FormID>());
	    }
	    getConsistencyFile();
	    v2import(globalOnly);
	    v1import(globalOnly);
	    imported = true;
	} catch (Exception ex) {
	    SPGlobal.logException(ex);
	    JOptionPane.showMessageDialog(null, "<html>There was an error importing the consistency information.<br><br>"
		    + "This means your savegame has a good chance of having mismatched records.<br><br>"
		    + "It would be greatly appreciated if you sent the failed consistency file located in<br>"
		    + "'My Documents/My Games/Skyrim/Skyproc/' to Leviathan1753 for analysis."
		    + "<br><br> If this is the first time running the patch, please ignore this message.</html>");
	}
    }

    static boolean insert(String EDID, FormID id) {
	Map<String, FormID> modEDIDlist = storage.get(id.master);
	if (modEDIDlist == null) {
	    modEDIDlist = new HashMap<>();
	    storage.put(id.master, modEDIDlist);
	}
	// If EDID is already logged, skip it.
	if (!modEDIDlist.containsKey(EDID)) {
	    if (set.contains(id)) {
		// FormID conflict
		String offendingEDID = "";
		for (String item : modEDIDlist.keySet()) {
		    if (modEDIDlist.get(item).equals(id)) {
			offendingEDID = item;
			break;
		    }
		}
		conflicts.put(id, EDID);
		conflicts.put(id, offendingEDID);
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>> Duplicate FormID warning.  ID: " + id + "  EDID: " + EDID + " and EDID2: " + offendingEDID);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	    }
	    modEDIDlist.put(EDID, id);
	    set.add(id);
	    return true;
	} else {
	    return false;
	}
    }

    static void export() throws IOException {
	if (SPGlobal.logging()) {
	    SPGlobal.logMain(header, "Exporting Consistency file.");
	}
	pruneConflicts();
	importConsistency(false);
	try {
	    getConsistencyFile();
	    File tmp = new File(consistencyFile + "Tmp");
	    LOutChannel out = new LOutChannel(tmp);
	    //Header
	    out.write("SPC2");
	    out.markLength(4);
	    for (ModListing m : storage.keySet()) {
		out.write(m.print().length(), 2);
		out.write(m.print());
		out.setPosMarker(4);
	    }
	    out.closeLength();
	    for (ModListing m : storage.keySet()) {
		out.fillPosMarker();
		out.markLength(4);
		Map<String, FormID> map = storage.get(m);
		for (String edid : map.keySet()) {
		    out.write(edid.length(), 2);
		    out.write(edid);
		    out.write(map.get(edid).getFormStr().substring(0, 6));
		}
		out.closeLength();
	    }
	    out.close();
	    File f = new File(consistencyFile + "V2");
	    if (f.isFile()) {
		f.delete();
	    }
	    tmp.renameTo(f);
	    out.close();
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	    SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Error exporting Consistency file.");
	    JOptionPane.showMessageDialog(null, "<html>There was an error exporting the consistency information.<br><br>"
		    + "This means your savegame has a good chance of having mismatched records the next<br>"
		    + "time you run the patcher.</html>");
	}
    }

    static void pruneConflicts() {
	if (SPGlobal.logging()) {
	    SPGlobal.newLog(debugFolder + "Conflict Pruning.txt");
	    SPGlobal.log(header, "====================================");
	    SPGlobal.log(header, "====== Pruning Conflicts ===========");
	    SPGlobal.log(header, "====================================");
	}
	Mod global = SPGlobal.getGlobalPatch();
	for (FormID id : conflicts.keySet()) {
	    if (SPGlobal.logging()) {
		SPGlobal.log(header, "------ Addressing " + id);
	    }
	    ArrayList<String> edidConflicts = conflicts.get(id);
	    ArrayList<String> found = new ArrayList<>(2);
	    for (String edid : edidConflicts) {
		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "| For EDID: " + edid);
		}
		for (GRUP g : global.GRUPs.values()) {
		    if (g.contains(edid)) {
			found.add(edid);
			if (SPGlobal.logging()) {
			    SPGlobal.log(header, "|   Found!");
			}
			break;
		    }
		}
	    }
	    // If only one is being used, wipe the rest
	    if (found.size() == 1) {
		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "| Only one EDID is being used, pruning others.");
		}
		edidConflicts.remove(found.get(0));
		Map<String, FormID> edidMap = storage.get(global.getInfo());
		for (String unusedEDID : edidConflicts) {
		    edidMap.remove(unusedEDID);
		}
	    }
	    if (SPGlobal.logging()) {
		SPGlobal.log(header, "-----------------------");
	    }
	}
    }

    static boolean isImported() {
	return imported;
    }

    static boolean v2import(boolean globalOnly) {
	File f = new File(consistencyFile + "V2");
	if (!f.isFile()) {
	    return false;
	}
	if (SPGlobal.logging()) {
	    String name;
	    if (globalOnly) {
		name = debugFolder + "Import - V2 - Only Global.txt";
	    } else {
		name = debugFolder + "Import - V2 - Remaining.txt";
	    }
	    SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
	    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "Importing v2 consistency file.");
	}
	LInChannel in = new LInChannel(f);
	in.skip(4);
	ModListing globalListing = SPGlobal.getGlobalPatch().getInfo();
	Map<ModListing, Integer> headerMap = importHeader(in);
	if (globalOnly) {
	    Integer tmp = headerMap.get(globalListing);
	    headerMap.clear();
	    if (tmp != null) {
		headerMap.put(globalListing, tmp);
	    }
	} else {
	    headerMap.remove(globalListing);
	}
	for (ModListing m : headerMap.keySet()) {
	    if (SPGlobal.logging()) {
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "===========================");
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "== Importing " + m);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "===========================");
	    }
	    in.pos(headerMap.get(m));
	    int length = in.extractInt(0, 4);
	    LInChannel contentArr = new LInChannel(in, length);
	    while (!contentArr.isDone()) {
		int edidLen = in.extractInt(2);
		String EDID = in.extractString(edidLen);
		String FormStr = in.extractString(6);
		FormID ID = new FormID(FormStr, m);
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  | EDID: " + EDID);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  | Form: " + FormStr);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  |============================");
		}
		insert(EDID, ID);
	    }
	}
	in.close();
	return true;
    }

    static Map<ModListing, Integer> importHeader(LInChannel in) {
	Map<ModListing, Integer> out = new HashMap<>();
	int headerLength = in.extractInt(4);
	LShrinkArray headerArr = new LShrinkArray(in.extract(headerLength));
	while (!headerArr.isDone()) {
	    int length = headerArr.extractInt(2);
	    String name = headerArr.extractString(length);
	    ModListing mod = new ModListing(name);
	    Integer pos = headerArr.extractInt(0, 4);
	    out.put(mod, pos);
	}
	return out;
    }

    static boolean v1import(boolean globalOnly) throws IOException {
	File f = new File(consistencyFile);
	if (!f.isFile()) {
	    return false;
	}
	if (SPGlobal.logging()) {
	    String name;
	    if (globalOnly) {
		name = debugFolder + "Import - V1 - Only Global.txt";
	    } else {
		name = debugFolder + "Import - V1 - Remaining.txt";
	    }
	    SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
	    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v1Import", "Importing v1 consistency file.");
	}
	BufferedReader in = new BufferedReader(new FileReader(f));
	while (in.ready()) {
	    String EDID = in.readLine();
	    String form = in.readLine();

	    // Check to see if following line is actually a formid
	    boolean fail = false;
	    while (in.ready() && !form.toUpperCase().endsWith(".ESP") && !form.toUpperCase().endsWith(".ESM")) {
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v1Import", "  Read fail line: " + form);
		}
		form = in.readLine();
		fail = true;
	    }
	    // If not, skip whole "pair"
	    if (fail) {
		continue;
	    }

	    FormID ID = new FormID(form);
	    boolean isGlobal = ID.getMaster().equals(SPGlobal.getGlobalPatch().getInfo());
	    if ((globalOnly && isGlobal) || (!globalOnly && !isGlobal)) {
		if (insert(EDID, ID) && SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Consistency Import", "  Inserting " + form + " with " + EDID);
		}
	    }
	}
	in.close();
	return true;
    }

    static enum LogTypes {
	CONSISTENCY,
	CONSISTENCY_IMPORT,
	;
    }
}
