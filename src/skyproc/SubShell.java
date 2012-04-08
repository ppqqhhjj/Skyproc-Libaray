/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFileChannel;
import lev.LShrinkArray;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class SubShell extends SubRecord {

    SubRecords subRecords = new SubRecords();
    boolean allValid = true;

    SubShell(Type type_) {
        super(type_);
    }

    SubShell(Type[] type_) {
        super(type_);
    }

    @Override
    public void clear() {
        subRecords.clear();
    }

    @Override
    Boolean isValid() {
        return subRecords.isValid(allValid);
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
    ArrayList<FormID> allFormIDs (boolean deep) {
	return subRecords.allFormIDs(deep);
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        subRecords.importSubRecords(in);
    }

    @Override
    void fetchStringPointers(Mod srcMod, Record r, Map<Files, LFileChannel> streams) throws IOException {
	subRecords.fetchStringPointers(srcMod, r, streams);
    }
}
