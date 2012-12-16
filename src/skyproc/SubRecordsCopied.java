/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import lev.LExporter;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
class SubRecordsCopied extends SubRecords {

    SubRecords orig;

    SubRecordsCopied(SubRecords rhs) {
	super();
	orig = rhs;
    }

    @Override
    public Iterator<SubRecord> iterator() {
	ArrayList<SubRecord> iter = new ArrayList<>();
	for (Type t : orig.getTopLevelTypes()) {
	    if (contains(t)) {
		iter.add(get(t));
	    }
	}
	return iter.iterator();
    }

    @Override
    protected void export(LExporter out, Mod srcMod) throws IOException {
	for (SubRecord s : iteratorNoCopy()) {
	    s.export(out, srcMod);
	}
    }

    @Override
    public int length(Mod srcMod) {
	int length = 0;
	for (SubRecord s : iteratorNoCopy()) {
	    length += s.getTotalLength(srcMod);
	}
	return length;
    }

    public ArrayList<SubRecord> iteratorNoCopy() {
	ArrayList<SubRecord> out = new ArrayList<>();
	for (Type t : orig.getTopLevelTypes()) {
	    if (contains(t)) {
		SubRecord s;
		if (map.containsKey(t)) {
		    s = map.get(t);
		} else {
		    s = orig.get(t);
		}
		if (shouldExport(s)) {
		    out.add(s);
		}
	    }
	}
	return out;
    }

    @Override
    public boolean contains(Type t) {
	return orig.contains(t) || map.containsKey(t);
    }

    @Override
    public SubRecord get(Type in) {
	if (!map.containsKey(in)) {
	    map.put(in, (SubRecord) Ln.deepCopy(orig.get(in)));
	}
	return map.get(in);
    }

    @Override
    public ArrayList<Type> getTypes() {
	return orig.getTypes();
    }

    @Override
    public ArrayList<Type> getTopLevelTypes() {
	return orig.getTopLevelTypes();
    }
}
