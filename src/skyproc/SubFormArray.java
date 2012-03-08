/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubFormArray extends SubRecord {

    ArrayList<FormID> IDs;

    public SubFormArray(Type type_, int size) {
        super(type_);
        IDs = new ArrayList<FormID>(size);
        for (int i = 0; i < size; i++) {
            IDs.add(new FormID());
        }
    }

    @Override
    SubRecord getNew(Type type) {
        return new SubFormArray(type, 0);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        super.export(out, srcMod);
        if (isValid()) {
            for (FormID ID : IDs) {
                out.write(ID.getInternal(true), 4);
            }
        }
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        int size = IDs.size();
        for (int i = 0; i < size; i++) {
            setIth(i, in.extract(4));
        }
    }

    void setIth(int i, byte[] in) {
        if (logging()) {
            logSync(toString(), "Setting " + toString() + " FormID[" + i + "]: " + Ln.printHex(in, false, true));
        }
        IDs.get(i).setInternal(in);
    }

    @Override
    void standardizeMasters(Mod srcMod) {
        super.standardizeMasters(srcMod);
        for (FormID ID : IDs) {
            ID.standardize(srcMod);
        }
    }

    @Override
    Boolean isValid() {
        for (FormID ID : IDs) {
            if (ID.isValid()) {
                return true;
            }
        }
        return false;
    }

    @Override
    int getContentLength(Mod srcMod) {
        return IDs.size() * 4;
    }
}
