/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubShell extends SubRecord {

    SubRecordsDerived subRecords;

    SubShell(SubPrototype proto) {
	super();
	subRecords = new SubRecordsDerived(proto);
    }

    @Override
    boolean isValid() {
	return subRecords.isValid();
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return subRecords.length(srcMod);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	subRecords.export(out, srcMod);
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	return subRecords.allFormIDs();
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	subRecords.importSubRecords(in);
    }

    SubPrototype getPrototype() {
	return subRecords.prototype;
    }

    @Override
    void fetchStringPointers(Mod srcMod) {
	subRecords.fetchStringPointers(srcMod);
    }

    @Override
    ArrayList<String> getTypes() {
	return subRecords.getTypes();
    }

    @Override
    SubRecord getNew(String type) {
	return new SubShell(subRecords.prototype);
    }
}
