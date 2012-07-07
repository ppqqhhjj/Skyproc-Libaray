package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * SubRecord that has a FormID followed by an integer.
 * @author Justin Swanson
 */
public class SubFormInt extends SubFormData {

    int num;

    SubFormInt(Type in) {
	super(in);
    }

    SubFormInt(Type type, FormID id, int number) {
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
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        num = Ln.arrayToInt(data);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return ID.getContentLength() + 4;
    }

    @Override
    SubRecord getNew(Type type_) {
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
        int hash = 5;
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
        if (!(o instanceof SubList)) {
            return false;
        }
        SubFormInt s = (SubFormInt) o; // Convert the object to a Person
        return (this.ID.equals(s.ID) && Arrays.equals(this.data, s.data));
    }
}
