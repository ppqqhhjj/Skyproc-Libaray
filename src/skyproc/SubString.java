/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubString extends SubRecordTyped {

    static SubString getNew(String type, boolean nullterminated) {
	if (nullterminated) {
	    return new SubString(type);
	} else {
	    return new SubStringNonNull(type);
	}
    }
    String string;

    SubString(String type_) {
	super(type_);
    }

    SubString(LShrinkArray in, String type_) throws BadRecord, DataFormatException, BadParameter {
	this(type_);
	parseData(in);
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	string = Ln.arrayToString(in.extractInts(in.available() - 1));
	if (logging()) {
	    logSync(getType().toString(), "Setting " + toString() + " to " + print());
	}
    }

    @Override
    boolean isValid() {
	return (string != null);
    }

    public void setString(String input) {
	string = input;
    }

    @Override
    public String print() {
	if (isValid()) {
	    return string;
	} else {
	    return "";
	}
    }

    @Override
    int getContentLength(Mod srcMod) {
	return string.length() + 1;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	out.write(string);
	out.write(0, 1);
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubString(type_);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof SubString)) {
	    return false;
	}
	final SubString other = (SubString) obj;
	if ((this.string == null) ? (other.string != null) : !this.string.equals(other.string)) {
	    return false;
	}
	return true;
    }

    public boolean equalsIgnoreCase(SubString in) {
	return equalsIgnoreCase(in.string);
    }

    public boolean equalsIgnoreCase(String in) {
	return string.equalsIgnoreCase(in);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 29 * hash + (this.string != null ? this.string.hashCode() : 0);
	return hash;
    }

    public int hashUpperCaseCode() {
	int hash = 7;
	hash = 29 * hash + (this.string != null ? this.string.toUpperCase().hashCode() : 0);
	return hash;
    }
}
