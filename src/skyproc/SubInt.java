/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LStream;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubInt extends SubRecord {

    private Integer data;
    int length = 4;

    SubInt(Type type) {
	super(type);
    }

    SubInt(Type type, int length) {
	this(type);
	this.length = length;
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubInt(type);
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
    void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
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
    Boolean isValid() {
	return data != null;
    }
}
