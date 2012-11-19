/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
public class SubRecordsCopied extends SubRecords {

    SubRecords orig;

    SubRecordsCopied(SubRecords rhs) {
	super();
	orig = rhs;
    }
    
    @Override
    public Iterator<SubRecord> iterator() {
	ArrayList<SubRecord> iter = new ArrayList<>();
	for (SubRecord s : orig) {
	    iter.add(get(s.getType()));
	}
	return iter.iterator();
    }

    @Override
    public boolean contains(Type t) {
	return orig.contains(t);
    }

    @Override
    public SubRecord get(Type in) {
	if (!map.containsKey(in)) {
	    map.put(in, (SubRecord)Ln.deepCopy(orig.get(in)));
	}
	return map.get(in);
    }
    
    @Override
    public Set<Type> getTypes() {
	return orig.getTypes();
    }
}
