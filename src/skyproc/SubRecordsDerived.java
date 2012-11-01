/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFileChannel;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class SubRecordsDerived extends SubRecords {

    protected SubRecordsPrototype prototype;
    protected Map<Type, Long> pos = new HashMap<>(0);
    protected Set<Type> fetched = new HashSet<>();
    Mod srcMod;

    public SubRecordsDerived(SubRecordsPrototype proto) {
	this.prototype = proto;
    }

    @Override
    protected void export(LExporter out, Mod srcMod) throws IOException {
	for (Type t : prototype.list) {
	    if (shouldExport(t)) {
		SubRecord instance = get(t);
		instance.export(out, srcMod);
	    }
	}
    }

    @Override
    public boolean shouldExport(SubRecord s) {
	return prototype.forceExport.contains(s.getType()) || super.shouldExport(s);
    }

    public boolean shouldExport(Type t) {
	if (map.containsKey(t)) {
	    return shouldExport(map.get(t));
	} else {
	    return shouldExport(prototype.get(t));
	}
    }

    @Override
    public boolean contains(Type t) {
	return prototype.contains(t);
    }

    @Override
    public SubRecord get(Type in) {
	SubRecord s = null;
	if (map.containsKey(in)) {
	    s = map.get(in);
	    if (!fetched.contains(s.getType())) {
		s.fetchStringPointers(srcMod);
		fetched.add(s.getType());
	    }
	} else if (prototype.contains(in)) {
	    s = prototype.get(in).getNew(in);
	    add(s);
	}
	return s;
    }

    @Override
    void importSubRecord(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	Type nextType = Record.getNextType(in);
	if (contains(nextType)) {
	    if (SPGlobal.streamMode && (in instanceof RecordShrinkArray || in instanceof LFileChannel)) {
		if (!pos.containsKey(nextType)) {
		    long position = in.pos();
		    if (SPGlobal.logging()) {
			SPGlobal.logSync(nextType.toString(), nextType.toString() + " is at position: " + Ln.printHex(position));
		    }
		    pos.put(nextType, position);
		}
		in.skip(get(nextType).getRecordLength(in));
	    } else {
		SubRecord record = get(nextType);
		record.parseData(record.extractRecordData(in));
		record.standardize(srcMod);
	    }
	} else {
	    throw new BadRecord("Doesn't know what to do with a " + nextType.toString() + " record.");
	}
    }

    @Override
    public void remove(Type in) {
	super.remove(in);
	if (pos.containsKey(in)) {
	    pos.remove(in);
	}
    }

    @Override
    public int length(Mod srcMod) {
	int length = 0;
	for (Type t : prototype.list) {
	    SubRecord s = get(t);
	    if (s != null && shouldExport(s)) {
		length += s.getTotalLength(srcMod);
	    }
	}
	return length;
    }
}
