/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import lev.LExporter;

/**
 * An abstract class outlining the functionality of subrecords, which are
 * records within Major Records.
 *
 * @author Justin Swanson
 */
public abstract class SubRecord extends Record {

    @Override
    public String print() {
	return "No " + getType().toString();
    }

    @Override
    public String toString() {
	return getType().toString() + "[" + getClass().getSimpleName() + "]";
    }

    @Override
    int getSizeLength() {
	return 2;
    }

    @Override
    int getFluffLength() {
	return 0;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	out.write(getType().toString());
	out.write(getContentLength(srcMod), 2);
    }

    abstract SubRecord getNew(Type type);

    boolean confirmLink() {
	return true;
    }

    ArrayList<FormID> allFormIDs() {
	return new ArrayList<>(0);
    }

    void standardize(Mod srcMod) {
	for (FormID id : allFormIDs()) {
	    id.standardize(srcMod);
	}
    }

    void standardize(MajorRecord r) {
	standardize(r.srcMod);
    }

    void fetchStringPointers(Mod srcMod) {
    }

    void fetchStringPointers (MajorRecord r) {
	fetchStringPointers(r.srcMod);
    }

    @Override
    void logSync(String header, String... log) {
	if (SPGlobal.debugSubrecordAll || SPGlobal.debugSubrecordsAllowed.contains(getType())) {
	    super.logSync(header, log);
	}
    }

    @Override
    Record getNew() {
	return getNew(Type.NULL);
    }
}
