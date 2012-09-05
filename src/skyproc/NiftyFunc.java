/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JOptionPane;
import lev.LFileChannel;
import lev.Ln;

/**
 * A class to hold many common/useful functions.
 *
 * @author Justin Swanson
 */
public class NiftyFunc {

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts
     * attached<br> (Any NPC that is referenced by a LVLN)<br> is to give a
     * racial spell to them that has a magic effect that has the desired
     * script.<br><br> This function streamlines the process and gives you a
     * SPEL/MGEF setup that will attach the desired script.<br> Simply give this
     * SPEL to the NPC's race.<br><br> NOTE: Attaching a script attachment spell
     * to an NPCs race will affect ALL NPCs with that same race.<br> If you do
     * not want this, then consider using genSafeScriptAttachingRace().
     *
     * @param originateFrom Mod to make the new records originate from.
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any
     * other SkyProc user's setups.<br> (using your mod's name is usually
     * sufficient)
     * @return The generated SPEL record that can be attached to any RACE to
     * have it attach the desired script.
     */
    public static SPEL genScriptAttachingSpel(Mod originateFrom, ScriptRef script, String uniqueID) {
	String name = "SP_" + uniqueID + "_" + script.name.data + "_attacher";
	MGEF mgef = new MGEF(originateFrom, name + "_MGEF", name + "_MGEF");
	mgef.scripts.addScript(script);
	mgef.set(MGEF.SpellEffectFlag.HideInUI, true);
	SPEL spel = new SPEL(originateFrom, name + "_SPEL");
	spel.setSpellType(SPEL.SPELType.Ability);
	spel.addMagicEffect(mgef);
	return spel;
    }

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts
     * attached<br> (Any NPC that is referenced by a LVLN)<br> is to give a
     * racial spell to them that has a magic effect that has the desired
     * script.<br><br> This function streamlines the process and gives you a
     * duplicate race which will attach the desired script.<br> You can then set
     * it to be the target NPCs race. Since it is a duplicate, it will only
     * affect NPCs you explicitly attach it to, and not ALL NPCs that shared the
     * original race. <br> It is a full duplicate and will retain any settings
     * of the original race.
     *
     * @param originateFrom Mod to make the new records originate from.
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any
     * other SkyProc user's setups.<br> (using your mod's name is usually
     * sufficient)
     * @param raceToDup Original race that you wish to duplicate.
     * @return A duplicate of the input race, with the only difference being it
     * has a script attachment racial spell.
     */
    public static RACE genSafeScriptAttachingRace(Mod originateFrom, ScriptRef script, RACE raceToDup, String uniqueID) {
	SPEL attachmentSpel = genScriptAttachingSpel(originateFrom, script, uniqueID);
	RACE attachmentRace = (RACE) originateFrom.makeCopy(raceToDup);
	attachmentRace.addSpell(attachmentSpel.getForm());
	return attachmentRace;
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to
     * a Leveled List at any point. If it is, that Leveled List is returned;
     * Null if not.
     *
     * @param npc NPC formID to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag... templateFlagsToCheck) {
	return isTemplatedToLList(npc, templateFlagsToCheck, 0);
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to
     * a Leveled List at any point. If it is, that Leveled List is returned;
     * Null if not.
     *
     * @param npc NPC to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(NPC_ npc, NPC_.TemplateFlag... templateFlagsToCheck) {
	return isTemplatedToLList(npc.getForm(), templateFlagsToCheck);
    }

    static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag[] templateFlagsToCheck, int depth) {
	if (depth > 100) {
	    return null; // avoid circular template overflows
	}

	if (templateFlagsToCheck.length == 0) {
	    templateFlagsToCheck = NPC_.TemplateFlag.values();
	}

	NPC_ npcSrc = (NPC_) SPDatabase.getMajor(npc, GRUP_TYPE.NPC_);

	if (npcSrc != null && !npcSrc.getTemplate().equals(FormID.NULL)) {
	    boolean hasTargetTemplate = false;
	    for (NPC_.TemplateFlag flag : templateFlagsToCheck) {
		if (npcSrc.get(flag)) {
		    hasTargetTemplate = true;
		    break;
		}
	    }
	    if (!hasTargetTemplate) {
		return null;
	    }

	    NPC_ templateN = (NPC_) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.NPC_);
	    if (templateN != null) { // If template is an NPC, recursively chain the check
		return isTemplatedToLList(templateN.getForm(), templateFlagsToCheck, depth + 1);
	    } else {
		return (LVLN) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.LVLN);
	    }
	}
	return null;
    }

    /**
     * Makes a new quest that starts immediately in-game, that has this script
     * attached to it.
     *
     * @param originateFrom Mod the quest should originate from.
     * @param script The script to add to the quest.
     * @return
     */
    public static QUST makeScriptQuest(Mod originateFrom, ScriptRef script) {
	QUST quest = new QUST(originateFrom, script.getName() + "_qust");
	quest.scripts.addScript(script);
	quest.FULL.setText(script.getName() + " Quest");
	return quest;
    }

    /**
     * A function that starts a new java program with more memory. Use this to
     * allocate more memory for your SkyProc program by simply putting the name
     * of your jar file as the jarpath. This function will automatically open a
     * second instance of your program, giving it more memory, and close the
     * first program if the second is opened properly.
     *
     * @param startingMem Memory to start the new program with.
     * @param maxMem Max amount of memory to allow the new program to use.
     * @param jarPath Path to the jar file to open. Usually, just put the name
     * of your jar.
     * @param args Any special main function args you want to give to the second
     * program.
     * @throws IOException
     * @throws InterruptedException
     */
    public static void allocateMoreMemory(String startingMem, String maxMem, String jarPath, String... args) throws IOException, InterruptedException {
	String[] argsInternal = new String[args.length + 5];
	argsInternal[0] = "java";
	argsInternal[1] = "-jar";
	argsInternal[2] = "-Xms" + startingMem;
	argsInternal[3] = "-Xmx" + maxMem;
	argsInternal[4] = jarPath;
	for (int i = 5; i < args.length + 5; i++) {
	    argsInternal[i] = args[i - 5];
	}
	ProcessBuilder proc = new ProcessBuilder(argsInternal);
	Process start = proc.start();
	InputStream shellIn = start.getInputStream();
	int exitStatus = start.waitFor();
	String response = Ln.convertStreamToStr(shellIn);
	if (exitStatus != 0) {
	    JOptionPane.showMessageDialog(null, "Error allocating " + maxMem + " of memory:\n"
		    + response
		    + "\nMemory defaulted to lowest levels.  Please lower your\n"
		    + "allocated memory in Other Settings and start the program again.");
	} else {
	    System.exit(0);
	}
    }

    public static String EDIDtrimmer(String origEDID) {
	origEDID = origEDID.replaceAll(" ", "");
	origEDID = origEDID.replaceAll(":", "_");
	origEDID = origEDID.replaceAll("-", "_");
	return origEDID;
    }

    /*
     * Only supports XX.XX.XX.XX with numbers for X
     */
    public static int versionToNum(String version) {
	String tmp = "";
	for (int i = 0; i < version.length(); i++) {
	    if (Character.isDigit(version.charAt(i))
		    || version.charAt(i) == '.') {
		tmp += version.charAt(i);
	    } else {
		break;
	    }
	}
	version = tmp;
	String[] split = version.split("\\.");
	int out = 0;
	for (int i = 0; i < split.length && i < 4; i++) {
	    int next = Integer.valueOf(split[i]) * 1000000;
	    if (i != 0) {
		next /= Math.pow(100, i);
	    }
	    out += next;
	}
	return out;
    }
    static String recordLengths = "Record Lengths";

    public static boolean validateRecordLengths(File testFile, int numErrorsToPrint) {
	return validateRecordLengths(testFile.getPath(), numErrorsToPrint);
    }

    /**
     * Reads in the file and confirms that all GRUPs and Major Records have
     * correct lengths. It does not explicitly check subrecord lengths, but due
     * to the recursive nature of SkyProc, these will be implicitly checked as
     * well by confirming Major Record length.
     *
     * This will bug out if strings inside records contain major record types
     * (ex. "SomeEDIDforMGEF", would fail because of "MGEF")
     *
     * @param testFile Path to the file to test.
     * @param numErrorsToPrint Number of error messages to print before
     * stopping.
     * @return True if the file had correct record lengths.
     */
    public static boolean validateRecordLengths(String testFilePath, int numErrorsToPrint) {
	boolean correct = true;
	int numErrors = 0;
	try {
	    File file = new File(testFilePath);
	    if (file.isFile()) {
		SPGlobal.log("Validate", "Target file exists: " + file);
	    } else {
		SPGlobal.log("Validate", "Target file does NOT exist: " + file);
	    }
	    LFileChannel input = new LFileChannel(testFilePath);

	    correct = testHeaderLength(input);

	    String inputStr;
	    //Test GRUPs
	    String majorRecordType = "NULL";
	    int grupLength = 0;
	    long grupPos = input.pos();
	    int length;
	    long start = 0;
	    String EDID = "";
	    while (input.available() >= 4 && (numErrors < numErrorsToPrint || numErrorsToPrint == 0)) {

		inputStr = input.extractString(0, 4);
		if (inputStr.equals("GRUP")) {
		    long inputPos = input.pos();
		    if (inputPos - grupPos - 4 != grupLength) {
			SPGlobal.logError(recordLengths, "GRUP " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(grupPos) + ")");
			numErrors++;
			correct = false;
		    }
		    grupPos = input.pos() - 4;
		    grupLength = input.extractInt(0, 4);
		    majorRecordType = input.extractString(0, 4);
		    input.skip(12);
		} else if (inputStr.equals(majorRecordType)) {
		    start = input.pos() - 4;
		    length = input.extractInt(0, 4);
		    input.skip(16);
		    int edidLength = input.extractInt(4, 2);
		    EDID = input.extractString(0, edidLength);
		    input.skip(length - 6 - EDID.length() - 1);
		} else {
		    SPGlobal.logError(recordLengths, "Major Record: " + majorRecordType + " | " + EDID + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
		    numErrors++;
		    correct = false;
		}
	    }

	    input.close();

	} catch (FileNotFoundException ex) {
	    SPGlobal.logError(recordLengths, "File could not be found.");
	    SPGlobal.logException(ex);
	} catch (IOException ex) {
	    SPGlobal.logError(recordLengths, "File I/O error.");
	    SPGlobal.logException(ex);
	}


	if (correct) {
	    SPGlobal.log(recordLengths, "Validated.");
	} else {
	    SPGlobal.logError(recordLengths, "NOT Validated.");
	}
	return correct;
    }

    static boolean testHeaderLength(LFileChannel input) throws IOException {
	// Check header
	String inputStr;
	boolean correct = true;
	int length = input.extractInt(4, 4);
	input.skip(length + 16);
	if (input.available() > 0) {
	    // Next should be a GRUP
	    inputStr = input.extractString(0, 4);
	    if (!"GRUP".equals(inputStr)) {
		SPGlobal.logError("Mod Header", "Header length is wrong.");
		correct = false;
	    }
	    input.skip(-4);
	} else if (input.available() < 0) {
	    SPGlobal.logError("Mod Header", "Header length is wrong.");
	    correct = false;
	} else {
	    SPGlobal.logError("Mod Header", "File header was correct, but there were no GRUPS.  Validated.");
	    return true;
	}
	return correct;
    }
}
