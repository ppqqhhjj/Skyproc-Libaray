/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    LLabel title;
    boolean unique;
    Comparator compare;
    LButton remove;
    LButton accept;
    static int spacing = 15;

    public LList(String title, Font font, Color color) {
	this.title = new LLabel(title, font, color);
	this.title.addShadow();
	add(this.title);

	model = new DefaultListModel();
	list = new JList(model);
	scroll = new JScrollPane(list);
	scroll.setVisible(true);
	scroll.setLocation(0, this.title.getY() + this.title.getHeight() + 10);
	add(scroll);

	remove = new LButton("Remove Selected");
	remove.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		removeSelected();
	    }
	});
	Add(remove);

	setSize(200, 150);
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	scroll.setSize(width, height - title.getHeight() - remove.getHeight() - 20);
	remove.setSize((scroll.getWidth() - spacing) / 2, remove.getHeight());
	if (accept != null) {
	    remove.putUnder(scroll, 0, 10);
	    accept.setSize(remove.getSize());
	    accept.putUnder(scroll, remove.getRight() + spacing, 10);
	} else {
	    remove.centerOn(scroll, scroll.getY() + scroll.getHeight() + 10);
	}
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

    public void clear() {
	model.clear();
    }

    public void addEnterButton(String title, ActionListener a) {
	accept = new LButton(title);
	accept.addActionListener(a);
	Add(accept);
	remove.setLocation(0, remove.getY());
	setSize(getSize().width, getSize().height);
    }

    public void setUnique(boolean on) {
	unique = on;
    }

    @Override
    public Iterator<T> iterator() {
	return getAll().iterator();
    }

    public void highlightChanged() {
	list.setBackground(new Color(224, 121, 147));
    }

    public void clearHighlight() {
	list.setBackground(Color.white);
    }

    public ArrayList<T> getAll() {
	ArrayList<T> out = new ArrayList<T>(model.size());
	for (Object o : model.toArray()) {
	    out.add((T) o);
	}
	return out;
    }
}
