/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
class SubListSortedUnique<T extends SubRecord> extends SubListSorted<T> {

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
    SubRecord getNew(String type) {
	return new SubListSortedUnique(this);
    }
}
