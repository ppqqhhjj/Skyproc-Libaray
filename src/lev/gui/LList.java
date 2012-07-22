/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Justin Swanson
 */
public class LList<T extends Object> extends LComponent implements Iterable<T> {

    DefaultListModel<T> model;
    JList list;
    JScrollPane scroll;
    boolean unique;
    Comparator compare;

    public LList(boolean unique) {
	model = new DefaultListModel();
	list = new JList(model);
	scroll = new JScrollPane(list);
	scroll.setVisible(true);
	add(scroll);
	this.unique = unique;
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	scroll.setSize(width, height);
    }

    public void addElement(T o) {
	if (!unique || !model.contains(o)) {
	    model.addElement(o);
	}
    }

    public void removeSelected() {
	for (Object o : list.getSelectedValuesList()) {
	    model.removeElement(o);
	}
    }

    public int numItems() {
	return model.size();
    }

    public boolean isEmpty() {
	return model.isEmpty();
    }

    public void clear () {
	model.clear();
    }

    @Override
    public Iterator<T> iterator() {
	return toArrayList().iterator();
    }

    public ArrayList<T> toArrayList() {
	ArrayList<T> out = new ArrayList<T>(model.size());
	for (Object o : model.toArray()) {
	    out.add((T)o);
	}
	return out;
    }

}
