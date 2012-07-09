/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubInt extends SubRecord {

    int data;
    int length = 4;
    boolean valid = false;

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

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	data = in.extractInt(length);
	valid = true;
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
    public void clear() {
	data = 0;
    }

    @Override
    Boolean isValid() {
	return valid;
    }
}
