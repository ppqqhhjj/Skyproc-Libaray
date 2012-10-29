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
public abstract class SubRecordsPrototype {

    protected ArrayList<Type> list = new ArrayList<>();
    protected Map<Type, SubRecord> map = new HashMap<>(0);
    protected Set<Type> forceExport = new HashSet<>(0);

    public SubRecordsPrototype() {
	addRecords();
    }

    protected abstract void addRecords();

    public SubRecordsPrototype(SubRecordsPrototype in) {
	list.addAll(in.list);
	for (Type t : in.map.keySet()) {
	    SubRecord s = in.map.get(t);
	    map.put(t, s.getNew(s.getType()));
	}
//	map.putAll(in.map);
	forceExport.addAll(in.forceExport);
	addRecords();
    }

    public final SubRecord add(SubRecord r) {
	for (Type t : r.getTypes()) {
	    remove(t);
	    map.put(t, r);
	}
	list.add(r.getType());
	return r;
    }

    public void before(SubRecord r, Type b) {
	add(r);
	list.remove(r.getType());
	list.add(list.indexOf(b), r.getType());
    }

    public void after(SubRecord r, Type b) {
	add(r);
	list.remove(r.getType());
	list.add(list.indexOf(b) + 1, r.getType());
    }

    public void reposition(Type t) {
	t = get(t).getType();
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
