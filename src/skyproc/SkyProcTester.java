/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.IOException;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SkyProcTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
	setSkyProcGlobal();

	try {

	    SPDefaultGUI gui = new SPDefaultGUI("Tester Program", "A tester program meant to flex SkyProc.");
	    validate();
//	    testFreshNewRecord(GRUP_TYPE.SPEL);
	    gui.finished();

	} catch (Exception e) {
	    LDebug.wrapUp();
	    throw e;
	}


	LDebug.wrapUp();
    }

    private static void testFreshNewRecord(GRUP_TYPE type) throws Exception {

	SPImporter importer = new SPImporter();
	SPGlobal.logging(false);
	importer.importMod(new ModListing("Skyrim.esm"), SPGlobal.pathToData, type);
	SPGlobal.logging(true);
	importer.importMod(new ModListing("CKReadIn.esp"), "Validation Files/", type);

	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.setAuthor("DEFAULT");

	patch.header.addMaster(new ModListing("Skyrim.esm"));
	patch.header.addMaster(new ModListing("Update.esm"));

	SPEL spel = new SPEL(patch, "AV_test");

	patch.export();

	Ln.validateCompare(SPGlobal.pathToData + "Test.esp", "Validation Files/CKReadIn.esp", 10);
    }

    private static void validate() throws Exception {

	SubStringPointer.shortNull = false;

	GRUP_TYPE[] types = {GRUP_TYPE.AMMO};
//	GRUP_TYPE[] types = GRUP_TYPE.values();

	SPImporter importer = new SPImporter();
	importer.importMod(new ModListing("Skyrim.esm"), SPGlobal.pathToData, types);

	SPGUI.progress.reset();
	SPGUI.progress.setMax(types.length);

	for (GRUP_TYPE g : types) {
	    if (!test(g)) {
		SPGUI.progress.setStatus("FAILED: " + g);
		break;
	    }
	    SPGUI.progress.setStatus("Validating DONE");
	}

    }

    private static boolean test(GRUP_TYPE type) throws IOException, BadRecord {
	SPGUI.progress.setStatus("Validating " + type);
	SPGUI.progress.pause(true);

	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.addAsOverrides(SPGlobal.getDB(), type);
	patch.setAuthor("Leviathan1753");
	patch.export();

	boolean passed = true;
	System.out.println("Testing Record Lengths " + type);
	passed = passed && SPExporter.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
	File validF = new File("Validation Files/" + type.toString() + "validation.esp");
	if (validF.isFile()) {
	    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", validF.getPath(), 10) && passed;
	} else {
	    System.out.println("Didn't have a source file to validate bytes to.");
	}

	SPGUI.progress.pause(false);
	SPGUI.progress.incrementBar();
	return passed;
    }

    private static void setSkyProcGlobal() {
	SPGlobal.createGlobalLog();
	SPGlobal.pathToData = "../";
	SPGlobal.consistency = false;
	LDebug.timeElapsed = true;
	SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }
}
