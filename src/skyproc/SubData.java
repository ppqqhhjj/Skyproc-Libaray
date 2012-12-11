/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LChannel;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubData extends SubRecordTyped {

    byte[] data;
    boolean forceExport = false;

    SubData(Type type_) {
	super(type_);
    }

    SubData(LShrinkArray in, Type type_) throws BadRecord, DataFormatException, BadParameter {
	this(type_);
	parseData(in);
    }

    SubData(Type type_, byte[] in) {
	this(type_);
	if (in != null) {
	    data = new byte[in.length];
	    System.arraycopy(in, 0, data, 0, in.length);
	}
    }

    SubData(Type type_, int in) {
	this(type_, Ln.toByteArray(in));
    }

    void forceExport(boolean on) {
	forceExport = on;
    }

    void initialize(int size) {
	data = new byte[size];
    }

    public void modValue(int mod) {
	setData(Ln.toByteArray(Ln.arrayToInt(getData()) + mod, getData().length));
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	setData(in.extractAllBytes());
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    Boolean isValid() {
	return (forceExport || data != null);
    }

    void setData(byte[] data_) {
	data = data_;
    }

    void setData(int data) {
	setData(data, 4);
    }

    void setData(int data, int size) {
	setData(Ln.toByteArray(data, size));
    }

    void setDataAbs(int data, int min, int max) {
	setData(Ln.toByteArray(Math.abs(data), min, max));
    }

    byte[] getData() {
	return data;
    }

    int toInt() {
	return Ln.arrayToInt(data);
    }

    @Override
    public String print() {
	if (isValid()) {
	    return Ln.printHex(data, true, false);
	} else {
	    return super.toString();
	}
    }

    @Override
    int getContentLength(Mod srcMod) {
	if (isValid() && data != null) {
	    return data.length;
	} else {
	    return 0;
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    if (data == null) {
		data = new byte[0];
	    }
	    super.export(out, srcMod);
	    out.write(data, 0);
	}
    }

    @Override
    SubRecord getNew(Type type_) {
	return new SubData(type_, data);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 23 * hash + Arrays.hashCode(this.data);
	return hash;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null) {
	    return false;
	}
	if (!(o instanceof byte[])) {
	    return false;
	}
	byte[] b = (byte[]) o;
	return (Arrays.equals(this.data, b));
    }
}
