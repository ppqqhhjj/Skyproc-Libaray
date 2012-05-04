/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import lev.Ln;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Justin Swanson
 */
class Consistency {

    static String header = "Consistency";
    static Map<String, FormID> edidToForm = new HashMap<String, FormID>();
    static Set<FormID> IDs = new HashSet<FormID>();
    static Set<FormID> newIDs = new HashSet<FormID>();
    static File consistencyFile = new File(SPGlobal.pathToInternalFiles + "Consistency.txt");
    static boolean cleaned = false;
    static boolean automaticExport = true;

    static FormID getOldForm(String edid) {
	if (edidToForm.containsKey(edid)) {
	    if (SPGlobal.debugConsistencyTies && SPGlobal.logging()) {
		SPGlobal.logSync(header, "Assigning old FormID " + edidToForm.get(edid) + " for EDID " + edid);
	    }
	    return edidToForm.get(edid);
	} else {
	    return null;
	}
    }

    static void mergetIntoKnownEDIDs(Mod mod) throws IOException {
	for (GRUP g : mod.GRUPs.values()) {
	    for (Object o : g) {
		MajorRecord m = (MajorRecord) o;
		// If src mod is the patch itself
		if (m.getFormMaster().equals(mod.getInfo())) {
		    // If EDID is not empty
		    if (!m.getEDID().equals("")) {
			// If already exists and not same FormID, problem
			if (!edidToForm.containsKey(m.getEDID()) || edidToForm.get(m.getEDID()).equals(m.getForm())) {
			    edidToForm.put(m.getEDID(), m.getForm());
			    if (SPGlobal.debugConsistencyImport && SPGlobal.logging()) {
				SPGlobal.log("Consistency", m.toString());
			    }
			} else {
			    SPGlobal.logError("Consistency", "Record " + m.getFormStr() + " had an already existing EDID: " + m.getEDID());
			}
		    } else {
			SPGlobal.logError("Consistency", "Record " + m.getFormStr() + " didn't have an EDID.");
		    }
		}
	    }
	}
    }

    static boolean requestID(FormID id) {
	if (!IDs.contains(id)) {
	    newIDs.add(id);
	    return true;
	} else {
	    return false;
	}
    }

    void importOldPatch(Mod patch) {

	File f = new File(SPGlobal.pathToData + patch.getName());
	if (!f.exists()) {
	    return;
	}
	SPImporter importer = new SPImporter();
	Mod consistencyPatch;
	try {
	    SPProgressBarPlug.progress.reset();
	    SPProgressBarPlug.progress.setMax(GRUP_TYPE.values().length + SPImporter.extraStepsPerMod);
	    SPImporter.getActiveModList();
	    SPGlobal.newSyncLog("Mod Import/ConsistencyPatch.txt");
	    boolean syncing = SPGlobal.sync();
	    SPGlobal.sync(true);
	    consistencyPatch = importer.importMod(patch.modInfo, SPGlobal.pathToData, false, GRUP_TYPE.values());
	    edidToForm = new HashMap<String, FormID>(consistencyPatch.numRecords());
	    SPGlobal.sync(syncing);
	    mergetIntoKnownEDIDs(consistencyPatch);

	} catch (Exception ex) {
	    SPGlobal.logException(ex);
	    JOptionPane.showMessageDialog(null, "<html>There was an error importing the consistency patch.<br><br>"
		    + "This means the old patch could not properly be imported to match new records with their<br>"
		    + "old formIDs.  This means your savegame has a good chance of having mismatched records.<br><br>"
		    + "Option 1: Your old patch has been moved to the debug folder.  You can move that back to the data <br>"
		    + "folder and use it. This essentially is 'reverting' to your original setup.<br>"
		    + "Option 2: Just keep letting the program run and use the new patch.  It will have fresh FormIDs that<br>"
		    + " are unrelated with past patches.  This may not be a problem depending on the situation.<br><br>"
		    + "Either way, it would be greatly appreciated if you sent the failed consistency patch (now located in<br>"
		    + "your debug folder) to Leviathan1753 for analysis.</html>");
	    File dest = new File(SPGlobal.pathToDebug + f.getName());
	    if (dest.isFile()) {
		dest.delete();
	    }
	    Ln.moveFile(f, dest, false);
	    SPGlobal.logError("SPGlobal", "Error importing global consistency patch: " + patch.getName());
	}
    }

    static void cleanConsistency() {
	cleaned = true;
    }

    static String getAvailableEDID(String edid) {
	// Currently not fixing EDIDs for newbs
	// It will cause tough bugs down the road
	// Its preferable to force them to do it right
	if (false && edidToForm.containsKey(edid)) {
	    int num = 0;
	    String newEDID = edid + num;
	    for (int i = 0; i < edidToForm.size(); i++) {
		if (!edidToForm.containsKey(newEDID)) {
		    break;
		}
		newEDID = edid + (++num);
	    }
	    edid = newEDID;
	}
	return edid;
    }
    
    static void addEntry (String edid, FormID id) {
	edidToForm.put(edid, id);
	IDs.add(id);
    }
    
    static void export() throws IOException {
	BufferedWriter out = new BufferedWriter(new FileWriter(consistencyFile));
	for (String s : edidToForm.keySet()) {
	    if (newIDs.contains(edidToForm.get(s))) {
		out.write(s + " " + edidToForm.get(s) + "\n");
	    }
	}
	out.close();
    }
}
