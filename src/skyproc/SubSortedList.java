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
class SubSortedList<T extends SubRecord> extends SubList<T> {

    TreeSet<T> sorter = new TreeSet<T>();

    SubSortedList(T prototype_) {
        super(prototype_);
        this.allowDups = false;
    }

    SubSortedList(Type counter, int counterLength, T prototype_) {
        super(counter, counterLength, prototype_);
        this.allowDups = false;
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
    void allowDuplicates(boolean on) {
        throw new java.lang.UnsupportedOperationException("Sorted List cannot contain dups.");
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


}
