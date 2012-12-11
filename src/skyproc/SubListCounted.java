/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class SubListCounted<T extends SubRecord> extends SubList {

    Type counterType = Type.NULL;
    private int counterLength = 4;

    SubListCounted(Type counter, int counterLength, T prototype_) {
        super(prototype_);
        counterType = counter;
        this.counterLength = counterLength;
    }

    SubListCounted(SubListCounted rhs) {
	super(rhs);
	counterType = rhs.counterType;
	counterLength = rhs.counterLength;
    }

    @Override
    int getContentLength(Mod srcMod) {
	int length = super.getContentLength(srcMod);
	if (counterType != Type.NULL) {
	    length += counterLength + 6;
	}
	return length;
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
        Type t = getNextType(in);
        if (counterType != t) {
            super.parseData(in, t);
        }
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubListCounted(this);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    if (counterType != Type.NULL) {
		SubData counter = new SubData(counterType, Ln.toByteArray(collection.size(), counterLength));
		counter.export(out, srcMod);
	    }
	    super.export(out, srcMod);
	}
    }

    @Override
    ArrayList getTypes() {
	ArrayList<Type> out = new ArrayList<>(super.getTypes());
	out.add(counterType);
	return out;
    }


}
