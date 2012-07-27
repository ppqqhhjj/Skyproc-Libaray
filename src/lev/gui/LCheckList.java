/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Justin Swanson
 */
public class LCheckList<T extends CheckableItem<T>> extends LComponent {

    DefaultListModel model;
    JList<T> list;
    JScrollPane scroll;

    public LCheckList() {
	model = new DefaultListModel();
	list = new JList<>(model);
	list.setCellRenderer(new CheckListRenderer());
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.setBorder(new EmptyBorder(0, 4, 0, 0));
	list.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		int index = list.locationToIndex(e.getPoint());
		CheckableItem item = (CheckableItem) list.getModel().getElementAt(index);
		item.setSelected(!item.isSelected());
		Rectangle rect = list.getCellBounds(index, index);
		list.repaint(rect);
	    }
	});
	scroll = new JScrollPane(list);
	add(scroll);
    }

    public void setSize(int x, int y) {
	super.setSize(x, y);
	scroll.setSize(x, y);
    }

    public void addItem(T item) {
	model.addElement(item);
    }

    public ArrayList<CheckableItem<T>> createData(T[] objs) {
	int n = objs.length;
	ArrayList<CheckableItem<T>> items = new ArrayList<>();
	for (int i = 0; i < n; i++) {
	    items.add(new CheckableItem<>(objs[i]));
	}
	return items;
    }

    class CheckListRenderer extends JCheckBox implements ListCellRenderer {

	public CheckListRenderer() {
	    setBackground(UIManager.getColor("List.textBackground"));
	    setForeground(UIManager.getColor("List.textForeground"));
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean hasFocus) {
	    setEnabled(list.isEnabled());
	    setSelected(((CheckableItem) value).isSelected());
	    setFont(list.getFont());
	    setText(value.toString());
	    return this;
	}
    }
}
