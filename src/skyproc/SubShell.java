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
import lev.LShrinkArray;
import lev.LStream;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class SubShell extends SubRecord {

    SubRecords subRecords = new SubRecords();

    SubShell(Type type_) {
	super(type_);
    }

    SubShell(Type[] type_) {
	super(type_);
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
    void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter {
	subRecords.importSubRecords(in);
    }

    @Override
    void fetchStringPointers(Mod srcMod, Record r, Map<SubStringPointer.Files, LChannel> streams) throws IOException {
	subRecords.fetchStringPointers(srcMod, r, streams);
    }
}
