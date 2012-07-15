/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import skyproc.Condition.Param;
import skyproc.Condition.ParamType;

/**
 *
 * @author Justin Swanson
 */
public class EmbeddedScripts {

    static EmbeddedScript[] indexing;

    // Highly specific function meant to parse the function list from
    // mod.gib.me/skyrim/functions.html as an easier way to generate
    // script objects, rather than typing it all by hand.
    // Shouldn't need to be used by you or any users.
    static void parseScriptData() throws FileNotFoundException, IOException {

	String dir = "Validation Files/";
	BufferedReader in = new BufferedReader(new FileReader(dir + "EmbeddedScriptSource.txt"));
	BufferedWriter out = new BufferedWriter(new FileWriter(dir + "EmbeddedScriptOut.txt"));
	BufferedWriter log = new BufferedWriter(new FileWriter(dir + "EmbeddedScriptOutLog.txt"));
	ArrayList<String> enumStrings = new ArrayList<>();
	int curIndex = -1;
	while (in.ready()) {
	    String line = in.readLine();
	    log.write("Read Line: " + line + "\n");
	    Scanner tokenizer = new Scanner(line);
	    try {
		int index = Integer.valueOf(tokenizer.next());
		log.write("  Index: " + index + "\n");
		if (index < 4096) {
		    log.write("  Skipped.\n");
		    continue;
		} else {
		    index -= 4096;
		}
		String name = tokenizer.next();
		if (name.contains("ref.")) {
		    name = tokenizer.next();
		}
		log.write("  Ref: " + name + "\n");
		ArrayList<ParamType> parameterMask = new ArrayList<>();
		tokenizer.useDelimiter(",");
		while (tokenizer.hasNext()) {
		    String parameter = tokenizer.next();
		    if (parameter.toUpperCase().contains("NAME")) {
			parameterMask.add(ParamType.String);
			log.write("  Parameter " + parameter + " " + ParamType.String + "\n");
		    } else if (parameter.toUpperCase().contains("AXIS")) {
			parameterMask.add(ParamType.Axis);
			log.write("  Parameter " + parameter + " " + ParamType.Axis + "\n");
		    } else if (parameter.toUpperCase().contains("UNK")
			    || parameter.toUpperCase().contains("QUEST")
			    || parameter.toUpperCase().contains("ACTOR")
			    || parameter.toUpperCase().contains("CONTAINER")) {
			parameterMask.add(ParamType.FormID);
			log.write("  Parameter " + parameter + " " + ParamType.FormID + "\n");
		    } else {
			parameterMask.add(ParamType.Int);
			log.write("  Parameter " + parameter + " " + ParamType.Int + "\n");
		    }
		}

		if (parameterMask.size() > 3) {
		    log.write("  Skipped.\n");
		    continue;
		}

		// Generate string
		String enumString = name + " (";
		boolean first = true;
		for (ParamType b : parameterMask) {
		    if (first) {
			first = false;
		    } else {
			enumString += ", ";
		    }
		    enumString += "ParamType." + b;
		}
		enumString += "), //" + index;
		while (++curIndex < index) {
		    enumStrings.add("UNKNOWN" + curIndex + " (),");
		}
		enumStrings.add(enumString);

	    } catch (NumberFormatException ex) {
		log.write("  Skipped\n");
	    }
	}

	for (String s : enumStrings) {
	    out.write(s + "\n");
	}

	in.close();
	out.close();
	log.close();
    }

    static void init() {
	int maxIndex = 0;
	for (EmbeddedScript s : EmbeddedScript.values()){
	    if (maxIndex < s.index) {
		maxIndex = s.index;
	    }
	}
	indexing = new EmbeddedScript[maxIndex];
	for (int i = 0 ; i < indexing.length ; i++) {
	    indexing[i] = EmbeddedScript.NULL;
	}

	for (EmbeddedScript s : EmbeddedScript.values()) {
	    indexing[s.index] = s;
	}
    }

    static EmbeddedScript getScript(int index) {
	if (indexing == null) {
	    init();
	}

	if (index < indexing.length) {
	    return indexing[index];
	} else {
	    return EmbeddedScript.NULL;
	}
    }

    public enum EmbeddedScript {

	CanHaveFlames(153),
	CanPayCrimeGold(497, ParamType.FormID),				    // Faction
	EPAlchemyGetMakingPoison(500),
	EPAlchemyEffectHasHeyword(501, ParamType.FormID),		    // Keyword
	EPTemperingItemHasKeyword(660, ParamType.FormID),		    // Keyword
	EPModSkillUsage_IsAdvanceSkill(681, ParamType.FormID),		    // Actor Value
	EPModSkillUsage_AdvanceObjectHasKeyword(691, ParamType.FormID),	    // Keyword
	EPModSkillUsage_IsAdvanceAction(692, ParamType.Int),		    // Skill Action
	EPMagic_SpellHasKeyword(693, ParamType.FormID),			    // Keyword
	EPMagic_SpellHasSkill(696, ParamType.FormID),			    // Actor Value
	EffectWasDualCast(724),
	DoesNotExist(726),
	CanFlyHere(731),
	NULL(-1);
	int index;
	ParamType[] paramsFormMask;

	EmbeddedScript(int index, ParamType... params) {
	    this.index = index;
	    this.paramsFormMask = params;
	}

	ParamType getType(Param paramIndex) {
	    if (paramIndex.ordinal() < paramsFormMask.length) {
		return paramsFormMask[paramIndex.ordinal()];
	    } else {
		return ParamType.Int;
	    }
	}
    }
}
