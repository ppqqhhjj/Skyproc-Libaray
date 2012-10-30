/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import lev.LChannel;
import lev.LExporter;

/**
 * An abstract class outlining the functionality of subrecords, which are
 * records within Major Records.
 *
 * @author Justin Swanson
 */
public abstract class SubRecord extends Record {

    Type[] type;

    SubRecord(Type[] type_) {
	// Don't call explicity aside from special subrecord constructors
	type = type_;
    }

    SubRecord(Type type_) {
	type = new Type[1];
	type[0] = type_;
    }

    @Override
    public String print() {
	return "No " + type[0].toString();
    }

    @Override
    public String toString() {
	return type[0].toString();
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
    Type[] getTypes() {
	return type;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	out.write(getTypes()[0].toString());
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
    
    void fetchStringPointers(Mod srcMod, Record r, Map<SubStringPointer.Files, LChannel> streams) {
    }

    @Override
    void logSync(String header, String... log) {
	if (SPGlobal.debugSubrecordAll || SPGlobal.debugSubrecordsAllowed.contains(getTypes()[0])) {
	    super.logSync(header, log);
	}
    }

    @Override
    Record getNew() {
	return getNew(Type.NULL);
    }
}
