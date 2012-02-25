/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Justin Swanson
 */
public class LMergeList<T extends LMergable> implements Iterable<T> {

    ArrayList<T> list = new ArrayList<T>();

    public void add(T in) {
        int index;
        if ((index = list.indexOf(in)) != -1) {
            list.get(index).mergeIn(in);
        } else {
            list.add(in);
        }
    }

    public boolean contains(T in) {
        return list.contains(in);
    }

    public void remove(T in) {
        list.remove(in);
    }

    public int size () {
        return list.size();
    }

    public T get (int i) {
        return list.get(i);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

}
