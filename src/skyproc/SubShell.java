/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class SubShell extends SubRecord {

    SubRecords subRecords;

    SubShell(Type type_) {
	super(type_);
	subRecords = new SubRecordsSolo(type_);
    }

    SubShell(Type[] type_) {
	super(type_);
	subRecords = new SubRecordsSolo(type_);
    }

    @Override
    Boolean isValid() {
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

    @Override
    void fetchStringPointers(Mod srcMod, Record r, Map<SubStringPointer.Files, LChannel> streams) {
	subRecords.fetchStringPointers(srcMod, r, streams);
    }
}
