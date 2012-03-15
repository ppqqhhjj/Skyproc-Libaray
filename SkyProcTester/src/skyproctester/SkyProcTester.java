package skyproctester;

import java.io.IOException;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.*;
import skyproc.exceptions.BadMod;

/**
 *
 * @author Justin Swanson
 */
public class SkyProcTester {

    static boolean stringfiles = false;

    private static void normalRun() throws Exception {

        GRUP_TYPE[] types = {GRUP_TYPE.NPC_};

//        for (GRUP_TYPE g : types) {
        for (GRUP_TYPE g : GRUP_TYPE.values()) {
            if (!importAndTest(g)) {
                break;
            }
        }

    }

    private static void setSkyProcGlobal() {
        SPGlobal.createGlobalLog();
        SPGlobal.pathToData = "../../";
        LDebug.timeElapsed = true;
        SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }

    private static boolean importAndTest(GRUP_TYPE type) throws IOException, BadMod {
        System.out.println("Importing " + type);
        SPImporter importer = new SPImporter();
        ModListing listing = new ModListing("Test", false);
        Mod patch = new Mod(listing);
        patch.addAsOverrides(importer.importMod(new ModListing("Skyrim.esm"), SPGlobal.pathToData, type));
        patch.setFlag(Mod.Mod_Flags.STRING_TABLED, stringfiles);

        // Export
        patch.setAuthor("Leviathan1753");
        patch.export();
        for (GRUP g : patch) {
            patch.makeCopy(g);
        }
        return test(type);
    }

    private static boolean test(GRUP_TYPE type) {
        boolean passed = true;
        System.out.println("Testing Record Lengths " + type);
        passed = passed && SPExporter.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
        switch (type) {
            case NPC_:
                if (stringfiles) {
                    System.out.println("Testing NPC with strings.");
                    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "NPCtessnipString.esp", 10) && passed;
                } else {
                    System.out.println("Testing NPC with embedded.");
                    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "NPCtessnipNoString.esp", 10) && passed;
                }
                break;
            case IMGS:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "IMGSvalidation.esp", 10) && passed;
                break;
            case LVLN:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "LVLNvalidation.esp", 10) && passed;
                break;
            case SPEL:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "SPELvalidation.esp", 10) && passed;
                break;
            case PERK:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "PERKvalidation.esp", 10) && passed;
                break;
            case RACE:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "RACEvalidation.esp", 10) && passed;
                break;
            case ARMO:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "ARMOvalidation.esp", 10) && passed;
                break;
            case ARMA:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "ARMAvalidation.esp", 10) && passed;
                break;
            case TXST:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "TXSTvalidation.esp", 10) && passed;
                break;
            case WEAP:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "WEAPvalidation.esp", 10) && passed;
                break;
            case FLST:
                passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", SPGlobal.pathToData + "FLSTvalidation.esp", 10) && passed;
                break;
	    default:
		System.out.println("Didn't have a source file to validate bytes to.");

        }
        return passed;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        setSkyProcGlobal();

        try {

            SPDefaultGUI gui = new SPDefaultGUI("Tester Program", "A tester program meant to flex SkyProc.");
            normalRun();
            gui.finished();

        } catch (Exception e) {
            LDebug.wrapUp();
            throw e;
        }


        LDebug.wrapUp();
    }
}
