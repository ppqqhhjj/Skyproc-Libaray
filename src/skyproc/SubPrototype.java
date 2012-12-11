/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Justin Swanson
 */
abstract class SubPrototype implements Serializable {

    protected ArrayList<Type> listExport = new ArrayList<>();
    protected Map<Type, SubRecord> map = new HashMap<>(0);
    protected ArrayList<Type> listExtensive = new ArrayList<>();
    protected Set<Type> forceExport = new HashSet<>(0);

    public SubPrototype() {
	addRecords();
    }

    protected abstract void addRecords();

    public SubPrototype(SubPrototype in) {
	mergeIn(in);
	addRecords();
    }

    public void mergeIn(SubPrototype in) {
	listExport.addAll(in.listExport);
	for (Type t : in.map.keySet()) {
	    SubRecord s = in.map.get(t);
	    map.put(t, s.getNew(s.getType()));
	}
	forceExport.addAll(in.forceExport);
    }

    public final SubRecord add(SubRecord r) {
	for (Type t : r.getTypes()) {
	    remove(t);
	    map.put(t, r);
	    listExtensive.add(t);
	}
	listExport.add(r.getType());
	return r;
    }

    public void before(SubRecord r, Type b) {
	add(r);
	listExport.remove(r.getType());
	listExport.add(listExport.indexOf(b), r.getType());
    }

    public void after(SubRecord r, Type b) {
	add(r);
	listExport.remove(r.getType());
	listExport.add(listExport.indexOf(b) + 1, r.getType());
    }

    public void reposition(Type t) {
	t = get(t).getType();
	listExport.remove(t);
	listExport.add(t);
    }

    public void forceExport(Type t) {
	forceExport.add(t);
    }

    public void remove(Type in) {
	if (map.containsKey(in)) {
	    for (int i = 0; i < listExport.size(); i++) {
		if (listExport.get(i).equals(in)) {
		    listExport.remove(i);
		    break;
		}
	    }
	    for (int i = 0; i < listExtensive.size(); i++) {
		if (listExtensive.get(i).equals(in)) {
		    listExtensive.remove(i);
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
