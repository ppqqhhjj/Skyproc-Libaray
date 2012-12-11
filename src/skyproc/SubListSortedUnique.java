/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class SubListSortedUnique<T extends SubRecord> extends SubListSorted<T> {

    SubListSortedUnique(T prototype_) {
        super(prototype_);
    }

    SubListSortedUnique(SubListSorted rhs) {
	super(rhs);
	sorter.addAll(rhs.sorter);
    }

    @Override
    public boolean add(T item) {
        if (allow(item)) {
            super.add(item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    boolean allow(T item) {
	return !collection.contains(item);
    }

    @Override
    public boolean addAtIndex(T item, int i) {
	if (allow(item)) {
            super.addAtIndex(item, i);
            return true;
        } else {
            return false;
        }
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubListSortedUnique(this);
    }
}
