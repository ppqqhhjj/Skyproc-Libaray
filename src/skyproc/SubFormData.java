/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import lev.LShrinkArray;
import lev.LChannel;

/**
 *
 * @author Justin Swanson
 */
class SubFormData extends SubForm {

    byte[] data;

    SubFormData(Type type, LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        this(type);
        parseData(in);
    }

    SubFormData(Type type, FormID id, byte[] data) {
        super(type, id);
        this.data = data;
    }

    SubFormData(Type in) {
        super(in);
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        setData(in.extract(4));
    }

    @Override
    int getContentLength(Mod srcMod) {
        return data.length + super.getContentLength(srcMod);
    }

    @Override
    SubRecord getNew(Type type_) {
        return new SubFormData(type_);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        super.export(out, srcMod);
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
	if (!Arrays.equals(this.data, other.data) && this.ID.equals(other.ID)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 79 * hash + Arrays.hashCode(this.data);
	return hash;
    }
}
