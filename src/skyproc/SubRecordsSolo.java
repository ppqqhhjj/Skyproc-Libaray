/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubRecordsSolo extends SubRecords {

    Type[] types;
    protected ArrayList<SubRecord> list = new ArrayList<>(2);
    protected Set<Type> forceExport = new HashSet<>(0);

    public SubRecordsSolo(Type t) {
	super();
	types = new Type[]{t};
    }

    public SubRecordsSolo(Type[] t) {
	super();
	types = t;
    }

    @Override
    public void add(SubRecord r) {
	super.add(r);
	list.add(r);
    }

    public void forceExport(Type t) {
	forceExport.add(t);
    }

    @Override
    public boolean shouldExport(SubRecord s) {
	return forceExport.contains(s.getType()) || super.shouldExport(s);
    }

    @Override
    void importSubRecord(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	Type nextType = Record.getNextType(in);
	if (contains(nextType)) {
	    SubRecord record = get(nextType);
	    record.parseData(record.extractRecordData(in));
	} else {
	    throw new BadRecord("Doesn't know what to do with a " + nextType.toString() + " record.");
	}
    }

    @Override
    public void remove(Type in) {
	super.remove(in);
	for (SubRecord s : list) {
	    if (s.getType().equals(in)) {
		list.remove(s);
		break;
	    }
	}
    }

    @Override
    public Iterator<SubRecord> iterator() {
	return list.iterator();
    }

    @Override
    public ArrayList<Type> typeOrder() {
	ArrayList<Type> out = new ArrayList<>(list.size());
	for (SubRecord s : list) {
	    out.add(s.getType());
	}
	return out;
    }
}
