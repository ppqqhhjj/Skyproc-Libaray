/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LOutFile;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import lev.LShrinkArray;
import lev.LImport;

/**
 *
 * @author Justin Swanson
 */
class SubFormData extends SubForm {

    byte[] data;

    SubFormData(String type, FormID id, byte[] data) {
	super(type, id);
	this.data = data;
    }

    SubFormData(String in) {
	super(in);
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	setData(in.extract(4));
    }

    @Override
    int getContentLength(ModExporter out) {
	return data.length + super.getContentLength(out);
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubFormData(type_);
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	out.write(data, 0);
    }

    public byte[] getData() {
	return data;
    }

    public void setData(byte[] in) {
	data = in;
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " data: " + Ln.printHex(in, false, true));
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubFormData other = (SubFormData) obj;
	if (!ID.equals(other.ID)
		|| !Arrays.equals(this.data, other.data)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = super.hashCode();
	hash = 61 * hash + Arrays.hashCode(this.data);
	return hash;
    }
}
