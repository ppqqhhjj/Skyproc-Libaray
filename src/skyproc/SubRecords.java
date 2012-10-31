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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class SubRecords implements Serializable {

    protected Map<Type, SubRecord> map = new HashMap<>(0);

    public SubRecords() {
    }

    public void add(SubRecord r) {
	for (Type t : r.getTypes()) {
	    map.put(t, r);
	}
    }

    protected abstract void export(LExporter out, Mod srcMod) throws IOException;

    public boolean shouldExport(SubRecord s) {
	return s.isValid();
    }

    public boolean contains(Type t) {
	return map.containsKey(t);
    }

    public SubRecord get(Type in) {
	return map.get(in);
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

    public void setSubData(Type in, int i) {
	getSubData(in).setData(i);
    }

    public SubFlag getSubFlag(Type in) {
	return (SubFlag) get(in);
    }

    public void setSubFlag(Type in, int i, boolean b) {
	getSubFlag(in).set(i, b);
    }

    public SubInt getSubInt(Type in) {
	return (SubInt) get(in);
    }

    public void setSubInt(Type in, int i) {
	getSubInt(in).set(i);
    }

    public SubRGB getSubRGB(Type in) {
	return (SubRGB) get(in);
    }

    public void setSubRGB(Type in, RGB c, float f) {
	getSubRGB(in).set(c, f);
    }

    public SubMarkerSet getSubMarker(Type in) {
	return (SubMarkerSet) get(in);
    }

    public KeywordSet getKeywords() {
	return (KeywordSet) get(Type.KWDA);
    }

    public SubFormArray getSubFormArray(Type in) {
	return (SubFormArray) get(in);
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
	    SubRecord record = get(nextType);
	    record.parseData(record.extractRecordData(in));
	} else {
	    throw new BadRecord("Doesn't know what to do with a " + nextType.toString() + " record.");
	}
    }

    public void remove(Type in) {
	if (map.containsKey(in)) {
	    map.remove(in);
	}
    }

    public int length(Mod srcMod) {
	int length = 0;
	for (SubRecord s : map.values()) {
	    if (shouldExport(s)) {
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

    void fetchStringPointers(Mod srcMod) {
	for (SubRecord s : map.values()) {
	    s.fetchStringPointers(srcMod);
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