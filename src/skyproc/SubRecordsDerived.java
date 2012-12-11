/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Justin Swanson
 */
class SubRecordsDerived extends SubRecords {

    protected SubPrototype prototype;

    public SubRecordsDerived(SubPrototype proto) {
	this.prototype = proto;
    }

    @Override
    public void setPrototype(SubPrototype proto) {
	prototype = proto;
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
	} else if (prototype.contains(in)) {
	    s = createFromPrototype(in);
	}
	return s;
    }

    SubRecord createFromPrototype(Type in) {
	SubRecord s = prototype.get(in).getNew(in);
	add(s);
	return s;
    }

    @Override
    public Iterator<SubRecord> iterator() {
	ArrayList<SubRecord> list = new ArrayList<>();
	for (Type t : prototype.listExport) {
	    if (shouldExport(t)) {
		list.add(get(t));
	    }
	}
	return list.iterator();
    }

    @Override
    public ArrayList<Type> getTypes() {
	return prototype.listExtensive;
    }

    class DerivedIterator implements Iterator<SubRecord> {

	Iterator<Type> list;

	DerivedIterator() {
	    list = prototype.listExport.iterator();
	}

	@Override
	public boolean hasNext() {
	    return list.hasNext();
	}

	@Override
	public SubRecord next() {
	    return get(list.next());
	}

	@Override
	public void remove() {
	}
    }
}
