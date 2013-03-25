package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * SubRecord that has a FormID followed by an integer.
 * @author Justin Swanson
 */
public class SubFormInt extends SubFormData {

    int num;

    SubFormInt(String in) {
	super(in);
    }

    SubFormInt(String type, FormID id, int number) {
	super(type);
	ID = id;
	num = number;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	data = Ln.toByteArray(num, 4);
	super.export(out, srcMod);
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        num = Ln.arrayToInt(data);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return ID.getContentLength() + 4;
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubFormInt(type_);
    }

    @Override
    public String print() {
	return super.print() + ", value: " + num;
    }

    /**
     *
     * @param number Number to associate with this record.
     */
    public void setNum(int number) {
	num = number;
    }

    /**
     *
     * @return Number associated with this record.
     */
    public int getNum() {
	return num;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + this.num;
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
        if (!(o instanceof SubFormInt)) {
            return false;
        }
        SubFormInt s = (SubFormInt) o;
	if (!this.ID.equals(s.ID) || num != s.num) {
	    return false;
	}
	
        return true;
    }
}
