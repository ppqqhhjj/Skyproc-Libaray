/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Justin Swanson
 */
public class MajorPrototype {

    protected ArrayList<Type> list = new ArrayList<>();
    protected Map<Type, SubRecord> map = new HashMap<>(0);
    protected Set<Type> forceExport = new HashSet<>(0);

    public MajorPrototype () {
	add(new SubString(Type.EDID, true));
    }

    public final void add(SubRecord r) {
	for (Type t : r.getTypes()) {
	    map.put(t, r);
	}
	list.add(r.getType());
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
