/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class SubRecordsSolo extends SubRecords {

    Type[] types;
    protected ArrayList<SubRecord> list = new ArrayList<>();
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
    protected void export(LExporter out, Mod srcMod) throws IOException {
	for (SubRecord s : list) {
	    if (shouldExport(s)) {
		s.export(out, srcMod);
	    }
	}
    }

    @Override
    public boolean shouldExport(SubRecord s) {
	return forceExport.contains(s.getType()) || super.shouldExport(s);
    }

    @Override
    public boolean contains(Type t) {
	return map.containsKey(t);
    }

    @Override
    public SubRecord get(Type in) {
	return map.get(in);
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
    public int length(Mod srcMod) {
	int length = 0;
	for (SubRecord s : list) {
	    if (shouldExport(s)) {
		length += s.getTotalLength(srcMod);
	    }
	}
	return length;
    }

    @Override
    public Iterator<SubRecord> iterator() {
	return list.iterator();
    }
}
