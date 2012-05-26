/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.IOException;
import lev.LExporter;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.*;
import skyproc.exceptions.BadRecord;
import skyproc.gui.SPDefaultGUI;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Justin Swanson
 */
public class SkyProcTester {

    /**
     * @param args the command line arguments
     */
    public static void runTests() {
	setSkyProcGlobal();

	try {

	    SPDefaultGUI gui = new SPDefaultGUI("Tester Program", "A tester program meant to flex SkyProc.");
	    validate();
	    gui.finished();

	} catch (Exception e) {
	    SPGlobal.logException(e);
	}
	try {
	    LDebug.wrapUp();
	} catch (IOException ex) {
	}
    }

    private static void validate() throws Exception {

	SubStringPointer.shortNull = false;

	GRUP_TYPE[] types = {GRUP_TYPE.MGEF};
//	GRUP_TYPE[] types = GRUP_TYPE.values();

	SPImporter importer = new SPImporter();
	importer.importMod(new ModListing("Skyrim.esm"), SPGlobal.pathToData, types);

	SPProgressBarPlug.progress.reset();
	SPProgressBarPlug.progress.setMax(types.length);

	for (GRUP_TYPE g : types) {
	    if (!test(g)) {
		SPProgressBarPlug.progress.setStatus("FAILED: " + g);
		break;
	    }
	    SPProgressBarPlug.progress.setStatus("Validating DONE");
	}

    }

    private static boolean test(GRUP_TYPE type) throws IOException, BadRecord {
	SPProgressBarPlug.progress.setStatus("Validating " + type);
	SPProgressBarPlug.progress.pause(true);

	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.addAsOverrides(SPGlobal.getDB(), type);
	patch.setAuthor("Leviathan1753");
	patch.export(new LExporter(SPGlobal.pathToData + patch.getName()), patch);

	boolean passed = true;
	System.out.println("Testing Record Lengths " + type);
	if (type != GRUP_TYPE.ENCH) {
	    passed = passed && SPExporter.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
	}
	File validF = new File("Validation Files/" + type.toString() + "validation.esp");
	if (validF.isFile()) {
	    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", validF.getPath(), 10) && passed;
	} else {
	    System.out.println("Didn't have a source file to validate bytes to.");
	}

	SPProgressBarPlug.progress.pause(false);
	SPProgressBarPlug.progress.incrementBar();
	return passed;
    }

    private static void setSkyProcGlobal() {
	SPGlobal.createGlobalLog();
	LDebug.timeElapsed = true;
	SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }
}
