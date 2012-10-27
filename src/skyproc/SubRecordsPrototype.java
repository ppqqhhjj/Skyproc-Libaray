/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.*;

/**
 *
 * @author Justin Swanson
 */
public class SubRecordsPrototype {

    protected ArrayList<Type> list = new ArrayList<>();
    protected Map<Type, SubRecord> map = new HashMap<>(0);
    protected Set<Type> forceExport = new HashSet<>(0);

    public SubRecordsPrototype() {
	add(new SubString(Type.EDID, true));
    }

    public SubRecordsPrototype(SubRecordsPrototype in) {
	list.addAll(in.list);
	for (SubRecord s : in.map.values()) {
	    map.put(s.getType(), s.getNew(s.getType()));
	}
	forceExport.addAll(in.forceExport);
    }

    public final void add(SubRecord r) {
	remove(r.getType());
	for (Type t : r.getTypes()) {
	    map.put(t, r);
	}
	list.add(r.getType());
    }

    public void reposition(Type t) {
	list.remove(t);
	list.add(t);
    }

    public void forceExport(Type t) {
	forceExport.add(t);
    }

    public void remove(Type in) {
	if (map.containsKey(in)) {
	    for (int i = 0; i < list.size(); i++) {
		if (list.get(i).equals(in)) {
		    list.remove(i);
		    break;
		}
	    }
	    map.remove(in);
	    forceExport.remove(in);
	}
    }

    public boolean contains(Type t) {
	return map.containsKey(t);
    }

    public SubRecord get(Type t) {
	return map.get(t);
    }
}
