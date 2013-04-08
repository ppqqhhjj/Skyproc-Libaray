/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script package attached to Major Records.
 *
 * @author Justin Swanson
 */
public class ScriptPackage extends SubRecord implements Serializable {

    int version = 5;
    int unknown = 2;
    ArrayList<ScriptRef> scripts = new ArrayList<>();
    ScriptFragments fragments;

    ScriptPackage() {
	super();
    }

    @Override
    SubRecord getNew(String type) {
	return new ScriptPackage();
    }

    @Override
    boolean isValid() {
	return scripts.size() > 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
	int out = 6;
	for (ScriptRef s : scripts) {
	    out += s.getTotalLength(srcMod);
	}
	if (fragments != null) {
	    out += fragments.getContentLength(srcMod);
	}
	return out;
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(2);
	for (ScriptRef s : scripts) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	version = in.extractInt(2);
	unknown = in.extractInt(2);
	int scriptCount = in.extractInt(2);
	if (logging()) {
	    logSync(toString(), "Importing VMAD record with " + scriptCount + " scripts.  Version: " + version + ", unknown: " + unknown);
	}
	for (int i = 0; i < scriptCount; i++) {
	    scripts.add(new ScriptRef(in, srcMod));
	}
	if (!in.isDone()) {
	    fragments = new ScriptFragments();
	    fragments.parseData(in, srcMod);
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	if (isValid()) {
	    out.write(version, 2);
	    out.write(unknown, 2);
	    out.write(scripts.size(), 2);
	    for (ScriptRef s : scripts) {
		s.export(out, srcMod);
	    }
	    if (fragments != null) {
		fragments.export(out, srcMod);
	    }
	}
    }

    class ScriptFragments {

	byte unknown = 0;
	LFlags fragmentFlags = new LFlags(1);
	StringNonNull fragmentFile = new StringNonNull();
	ArrayList<ScriptFragment> fragments = new ArrayList<>();

	void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    fragmentFlags.set(in.extract(1));
	    fragmentFile.set(in.extractString(in.extractInt(2)));
	    while (!in.isDone()) {
		ScriptFragment frag = new ScriptFragment();
		frag.parseData(in, srcMod);
		fragments.add(frag);
	    }
	}

	void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(unknown, 1);
	    out.write(fragmentFlags.export());
	    fragmentFile.export(out, srcMod);
	    for (ScriptFragment frag : fragments) {
		frag.export(out, srcMod);
	    }
	}
	
	int getContentLength(Mod srcMod) {
	    int out = 2;
	    out += fragmentFile.getTotalLength(srcMod);
	    for (ScriptFragment frag : fragments) {
		out += frag.getContentLength(srcMod);
	    }
	    return out;
	}
    }

    class ScriptFragment {

	byte unknown = 0;
	StringNonNull scriptName = new StringNonNull();
	StringNonNull fragmentName = new StringNonNull();

	void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    scriptName.set(in.extractString(in.extractInt(2)));
	    fragmentName.set(in.extractString(in.extractInt(2)));
	}

	void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(unknown, 1);
	    scriptName.export(out, srcMod);
	    fragmentName.export(out, srcMod);
	}
	
	int getContentLength(Mod srcMod) {
	    return 1 + scriptName.getTotalLength(srcMod) 
		    + fragmentName.getTotalLength(srcMod);
	}
    }

    // Get set
    /**
     *
     * @return List of the names of scripts attached.
     */
    public ArrayList<ScriptRef> getScripts() {
	return scripts;
    }

    /**
     *
     * @param scriptName Adds an empty script with the given name.
     */
    public void addScript(String scriptName) {
	addScript(new ScriptRef(scriptName));
    }

    /**
     * Adds the script reference to the package.
     *
     * @param script
     */
    public void addScript(ScriptRef script) {
	scripts.add(script);
    }

    /**
     * Returns a ScriptRef object matching the name, if one exists, or null if
     * one does not.
     *
     * @param scriptName
     * @return
     */
    public ScriptRef getScript(String scriptName) {
	return getScript(new ScriptRef(scriptName));
    }

    /**
     * Returns the ScriptRef object from the ScriptPackage that matches the
     * input's name.
     *
     * @param script
     * @return
     */
    public ScriptRef getScript(ScriptRef script) {
	return scripts.get(scripts.indexOf(script));
    }

    /**
     *
     * @param scriptName Script name to query
     * @return True if package has a script with that name.
     */
    public boolean hasScript(String scriptName) {
	return hasScript(new ScriptRef(scriptName));
    }

    /**
     * Returns true if package has a script matching the input's name
     *
     * @param script
     * @return
     */
    public boolean hasScript(ScriptRef script) {
	return scripts.contains(script);
    }

    /**
     *
     * @param scriptName Script name to remove, if present.
     */
    public void removeScript(String scriptName) {
	removeScript(new ScriptRef(scriptName));
    }

    /**
     * Removes a ScriptRef matching the input's name, if one exists.
     *
     * @param script
     */
    public void removeScript(ScriptRef script) {
	scripts.remove(script);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("VMAD");
    }
}
