/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
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
class SubRecords implements Serializable {

    protected MajorPrototype prototype;
    protected Map<Type, SubRecord> map = new HashMap<>(0);
    protected Map<Type, Long> pos = new HashMap<>(0);

    public SubRecords () {

    }

    public SubRecords (MajorPrototype proto) {
	this.prototype = proto;
    }

    public void add(SubRecord r) {
	for (Type t : r.getTypes()) {
	    map.put(t, r);
	}
    }

    void export(LExporter out, Mod srcMod) throws IOException {
	for (Type t : prototype.list) {
	    if (contains(t)) {
		SubRecord instance = get(t);
		if (shouldExport(instance)) {
		    instance.export(out, srcMod);
		}
	    }
	}
    }

    public boolean shouldExport(SubRecord s) {
	return prototype.forceExport.contains(s.getType()) || s.isValid();
    }

    public void forceExport(Type t) {
    }

    public boolean contains(Type t) {
	return prototype.contains(t);
    }

    public SubRecord get(Type in) {
	if (map.containsKey(in)) {
	    return map.get(in);
	} else if (prototype.contains(in)) {
	    map.put(in, prototype.get(in).getNew(in));
	    return map.get(in);
	} else {
	    return null;
	}
    }

    public SubString getSubString(Type in) {
	return (SubString) get(in);
    }

    public void setSubString(Type in, String str) {
	getSubString(in).setString(str);
    }

    public SubForm getSubForm(Type in) {
	return (SubForm) get(in);
    }

    public void setSubForm(Type in, FormID id) {
	getSubForm(in).setForm(id);
    }

    public SubFloat getSubFloat(Type in) {
	return (SubFloat) get(in);
    }

    public void setSubFloat(Type in, float f) {
	getSubFloat(in).set(f);
    }

    public SubData getSubData(Type in) {
	return (SubData) get(in);
    }

    public void setSubData(Type in, byte[] b) {
	getSubData(in).setData(b);
    }

    public SubFlag getSubFlag(Type in) {
	return (SubFlag) get(in);
    }

    public void setSubFlag(Type in, int i, boolean b) {
	getSubFlag(in).set(i, b);
    }

    public SubList getSubList(Type in) {
	return (SubList) get(in);
    }

    boolean isValid() {
	for (SubRecord s : map.values()) {
	    if (!s.isValid()) {
		return false;
	    }
	}
	return true;
    }

    void printSummary() {
	if (SPGlobal.logging() && SPGlobal.debugSubrecordSummary) {
	    String header = "Summary: ";
	    String data = "";
	    int counter = 0;
	    ArrayList<Type> printedTypes = new ArrayList<Type>();
	    for (Type type : getTypes()) {
		SubRecord s = get(type);
		if (s.isValid() && !printedTypes.contains(type)) {
		    data = data + type.toString() + " ";
		    if (s instanceof SubList) {
			data = data + "(" + ((SubList) s).size() + ") ";
		    }
		    printedTypes.addAll(Arrays.asList(s.getTypes()));
		    if (counter++ == 12) {
			SPGlobal.logSync("Subrecords", header + data);
			header = "-------- ";
			data = "";
			counter = 0;
		    }
		}
	    }
	    if (counter > 0) {
		SPGlobal.logSync("Subrecords", header + data);
	    }
	}
    }

    void importSubRecords(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	while (!in.isDone()) {
	    importSubRecord(in);
	}
    }

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
	    }
	} else {
	    throw new BadRecord("Doesn't know what to do with a " + nextType.toString() + " record.");
	}
    }

    public void remove(Type in) {
	if (map.containsKey(in)) {
	    map.remove(in);
	}
	if (pos.containsKey(in)) {
	    pos.remove(in);
	}
    }

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

    public ArrayList<SubRecord> getRecords() {
	return new ArrayList(map.values());
    }

    public Set<Type> getTypes() {
	return map.keySet();
    }

    void fetchStringPointers(Mod srcMod, Record r, Map<SubStringPointer.Files, LChannel> streams) {
	for (SubRecord s : map.values()) {
	    s.fetchStringPointers(srcMod, r, streams);
	}
    }

    public ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (SubRecord s : map.values()) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }
}