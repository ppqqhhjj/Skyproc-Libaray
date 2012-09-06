/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @param <T> 
 * @author Justin Swanson
 */
public class LList<T extends Object> extends LHelpComponent implements Iterable<T> {

    DefaultListModel<T> model;
    JList<T> list;
    JScrollPane scroll;
    LLabel title;
    boolean unique;
    Comparator compare;
    LButton remove;
    LButton accept;
    static int spacing = 15;

    /**
     *
     * @param title
     * @param font
     * @param color
     */
    public LList(String title, Font font, Color color) {
	super(title);
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

    /**
     *
     * @param o
     */
    public void addElement(T o) {
	if (!unique || !model.contains(o)) {
	    model.addElement(o);
	}
    }

    /**
     *
     * @param in
     */
    public void addElements(Collection<T> in) {
	for (T t : in) {
	    addElement(t);
	}
    }

    /**
     *
     * @return
     */
    public List<T> getSelectedElements() {
	return list.getSelectedValuesList();
    }

    /**
     *
     * @return
     */
    public T getSelectedElement() {
	return list.getSelectedValue();
    }

    /**
     *
     */
    public void removeSelected() {
	for (Object o : list.getSelectedValuesList()) {
	    model.removeElement(o);
	}
    }

    /**
     *
     * @return
     */
    public int numItems() {
	return model.size();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
	return model.isEmpty();
    }

    /**
     *
     */
    public void clear() {
	model.clear();
    }

    /**
     *
     * @param title
     * @param a
     */
    public void addEnterButton(String title, ActionListener a) {
	accept = new LButton(title);
	accept.addActionListener(a);
	Add(accept);
	remove.setLocation(0, remove.getY());
	setSize(getSize().width, getSize().height);
    }

    /**
     *
     * @param title
     * @param a
     */
    public void setRemoveButton(String title, ActionListener a) {
	remove.setText(title);
	remove.clearActionHandlers();
	remove.addActionListener(a);
    }

    /**
     *
     * @param on
     */
    public void setUnique(boolean on) {
	unique = on;
    }

    /**
     *
     * @param l
     */
    public void addListSelectionListener(ListSelectionListener l) {
	list.addListSelectionListener(l);
    }

    @Override
    public void addMouseListener(MouseListener m) {
	super.addMouseListener(m);
	remove.addMouseListener(m);
	if (accept != null) {
	    accept.addMouseListener(m);
	}
	scroll.addMouseListener(m);
	list.addMouseListener(m);
    }

    @Override
    public Iterator<T> iterator() {
	return getAll().iterator();
    }

    /**
     *
     */
    public void highlightChanged() {
	list.setBackground(new Color(224, 121, 147));
    }

    /**
     *
     */
    public void clearHighlight() {
	list.setBackground(Color.white);
    }

    /**
     *
     * @return
     */
    public ArrayList<T> getAll() {
	ArrayList<T> out = new ArrayList<T>(model.size());
	for (Object o : model.toArray()) {
	    out.add((T) o);
	}
	return out;
    }

    @Override
    protected void addHelpHandler(boolean hoverListener) {
	addMouseListener(new HelpMouseHandler());
    }
}
