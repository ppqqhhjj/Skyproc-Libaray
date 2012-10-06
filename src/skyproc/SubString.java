/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
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
class SubString extends SubRecord {

    String string;
    boolean nullterm;

    SubString(Type type_, boolean nullTerminated) {
        super(type_);
        nullterm = nullTerminated;
    }

    SubString(Type[] types, boolean nullTerminated) {
        super(types);
        nullterm = nullTerminated;
    }

    SubString(LShrinkArray in, Type type_, boolean nullTerminated) throws BadRecord, DataFormatException, BadParameter {
        this(type_, nullTerminated);
        parseData(in);
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        if (nullterm) {
            string = Ln.arrayToString(in.extractInts(in.available() - 1));
        } else {
            string = Ln.arrayToString(in.extractInts(in.available()));
        }
        if (logging()) {
            logSync(type.toString(), "Setting " + toString() + " to " + print());
        }
    }

    @Override
    Boolean isValid() {
        return (string != null
//                && !"".equals(string)
                );
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
        if (isValid()) {
            if (nullterm) {
                return string.length() + 1;
            } else {
                return string.length();
            }
        } else {
            return 0;
        }
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        if (isValid()) {
            super.export(out, srcMod);
            out.write(string);
            if (nullterm) {
                out.write(0, 1);
            }
        }
    }

    @Override
    SubRecord getNew(Type type_) {
        return new SubString(type_, nullterm);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
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
