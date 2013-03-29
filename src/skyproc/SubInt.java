/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubInt extends SubRecordTyped {

    private Integer data;
    int length = 4;

    SubInt(String type) {
	super(type);
    }

    SubInt(String type, int length) {
	this(type);
	this.length = length;
    }

    @Override
    SubRecord getNew(String type) {
	return new SubInt(type, length);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return length;
    }

    public void set (int in) {
	data = in;
        if (logging()) {
            logSync(toString(), "Setting " + toString() + " to " + print());
        }
    }

    public int get () {
	return data;
    }

    @Override
    void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	data = in.extractInt(length);
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
	    out.write(data, length);
	}
    }

    @Override
    boolean isValid() {
	return data != null;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubInt other = (SubInt) obj;
	if (!Objects.equals(this.data, other.data)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.data);
	return hash;
    }
}
