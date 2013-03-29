/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubFloat extends SubRecordTyped {

    float data;

    SubFloat(String type) {
	super(type);
    }

    @Override
    SubRecord getNew(String type) {
	return new SubFloat(type);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return 4;
    }

    @Override
    void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	data = in.extractFloat();
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    public String print() {
	if (isValid()) {
	    return String.valueOf(data);
	} else {
	    return super.toString();
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    super.export(out, srcMod);
	    out.write(data);
	}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof SubFloat)) {
            return false;
        }
        SubFloat s = (SubFloat) o; // Convert the object to a Person
        return (Float.compare(this.data, s.data) == 0);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 67 * hash + Float.floatToIntBits(this.data);
	return hash;
    }

    public void set(float in) {
	data = in;
    }

    public float get() {
	return data;
    }
}
