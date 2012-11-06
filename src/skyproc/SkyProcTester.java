/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.exceptions.BadMod;
import skyproc.exceptions.BadRecord;
import skyproc.gui.SPDefaultGUI;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Justin Swanson
 */
public class SkyProcTester {

    static ArrayList<FormID> badIDs;

//    static GRUP_TYPE[] types = {GRUP_TYPE.ENCH};
    static GRUP_TYPE[] types = GRUP_TYPE.values();

    /**
     */
    public static void runTests() {
	setSkyProcGlobal();
	try {
	    SPGlobal.testing = true;
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

	badIDs = new ArrayList<>();
	ModListing skyrim = new ModListing("Skyrim.esm");
	badIDs.add(new FormID("010B115", skyrim));  //EnchSilverSword
	badIDs.add(new FormID("10A27F", skyrim));  //TrapLightningRune
	badIDs.add(new FormID("10A27E", skyrim));  //TrapFrostRune
	badIDs.add(new FormID("073328", skyrim));  //TrapFireRune
	badIDs.add(new FormID("018A45", skyrim));  //RiverwoodZone
	badIDs.add(new FormID("00001E", skyrim));  //NoZoneZone

	SubStringPointer.shortNull = false;

	FormID.allIDs.clear();
	SPImporter importer = new SPImporter();
	importer.importMod(skyrim, SPGlobal.pathToData, types);

	SPProgressBarPlug.reset();
	SPProgressBarPlug.setMax(types.length);

	for (GRUP_TYPE g : types) {
	    if (!test(g)) {
		SPProgressBarPlug.setStatus("FAILED: " + g);
		break;
	    }
	    SPProgressBarPlug.setStatus("Validating DONE");
	}

	boolean idFail = false;
	for (FormID id : FormID.allIDs) {
	    if (!id.isNull() && id.getMaster() == null && !badIDs.contains(id)) {
		System.out.println("A bad id: " + id);
		idFail = true;
		break;
	    }
	}
	if (idFail) {
	    System.out.println("Some FormIDs were unstandardized!!");
	    return;
	} else {
	    System.out.println("All FormIDs properly standardized.");
	}
    }

    private static boolean test(GRUP_TYPE type) throws IOException, BadRecord, BadMod {
	System.out.println("Testing " + type);
	SPProgressBarPlug.setStatus("Validating " + type);
	SPProgressBarPlug.pause(true);

	boolean passed = true;
	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.addAsOverrides(SPGlobal.getDB(), type);
	for (FormID f : badIDs) {
	    patch.remove(f);
	}
	patch.setAuthor("Leviathan1753");
	patch.export(new File(SPGlobal.pathToData + patch.getName()), patch);
	if (type != GRUP_TYPE.ENCH) {
	    passed = passed && NiftyFunc.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
	}
	File validF = new File("Validation Files/" + type.toString() + "validation.esp");
	if (validF.isFile()) {
	    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", validF.getPath(), 10) && passed;
	} else {
	    System.out.println("Didn't have a source file to validate bytes to.");
	}

	SPProgressBarPlug.pause(false);
	SPProgressBarPlug.incrementBar();
	return passed;
    }

    /**
     *
     */
    public static void parseEmbeddedScripts() {
	try {
	    EmbeddedScripts.generateEnums();
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    private static void setSkyProcGlobal() {
	SPGlobal.createGlobalLog();
	LDebug.timeElapsed = true;
	SPGlobal.logging(true);
	SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }
}
