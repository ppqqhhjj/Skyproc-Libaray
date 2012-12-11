/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Justin Swanson
 */
class SubListSorted<T extends SubRecord> extends SubList<T> {

    TreeSet<T> sorter = new TreeSet<>();

    SubListSorted(T prototype_) {
        super(prototype_);
    }

    SubListSorted(SubListSorted rhs) {
	super(rhs);
	sorter.addAll(rhs.sorter);
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubListSorted(this);
    }

    @Override
    public T get(int i) {
        return (T)sorter.toArray()[i];
    }

    @Override
    public boolean add(T item) {
        sorter.add(item);
        return super.add(item);
    }

    @Override
    public boolean remove(T item) {
        sorter.remove(item);
        return super.remove(item);
    }

    @Override
    public void remove(int i) {
        sorter.remove(collection.get(i));
        super.remove(i);
    }

    @Override
    public void clear() {
        super.clear();
        sorter.clear();
    }

    @Override
    public void addRecordsTo(ArrayList<T> in) {
        super.addRecordsTo(in);
        sorter.addAll(in);
    }

    @Override
    public void setRecordsTo(ArrayList<T> in) {
        super.setRecordsTo(in);
        sorter.clear();
        sorter.addAll(in);
    }

    @Override
    public Iterator<T> iterator() {
        return sorter.iterator();
    }

    public Iterator<T> unsortedIterator() {
	return super.iterator();
    }
}
