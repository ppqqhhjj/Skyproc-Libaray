/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script package attached to Major Records.
 *
 * @author Justin Swanson
 */
public class ScriptPackage extends SubRecord {

    int version = 5;
    int unknown = 2;
    ArrayList<ScriptRef> scripts = new ArrayList<ScriptRef>();

    ScriptPackage() {
	super(Type.VMAD);
    }

    @Override
    SubRecord getNew(Type type) {
	return new ScriptPackage();
    }

    @Override
    public void clear() {
	scripts.clear();
    }

    @Override
    Boolean isValid() {
	return scripts.size() > 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
	int out = 6;
	for (ScriptRef s : scripts) {
	    out += s.getTotalLength(srcMod);
	}
	return out;
    }

    @Override
    void standardizeMasters(Mod srcMod) {
	super.standardizeMasters(srcMod);
	for (ScriptRef s : scripts) {
	    s.standardizeMasters(srcMod);
	}
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	version = in.extractInt(2);
	unknown = in.extractInt(2);
	int scriptCount = in.extractInt(2);
	if (logging()) {
	    logSync(toString(), "Importing VMAD record with " + scriptCount + " scripts.  Version: " + version + ", unknown: " + unknown);
	}
	for (int i = 0; i < scriptCount; i++) {
	    scripts.add(new ScriptRef(in));
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

    public void addScript(ScriptRef script) {
	scripts.add(script);
    }

    public ScriptRef getScript(String scriptName) {
	return getScript(new ScriptRef(scriptName));
    }

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

    public void removeScript(ScriptRef script) {
	scripts.remove(script);
    }
}