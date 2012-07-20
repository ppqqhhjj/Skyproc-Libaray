/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Justin Swanson
 */
public class LList extends LComponent {

    DefaultListModel model;
    JList list;
    JScrollPane scroll;

    public LList () {
	model = new DefaultListModel();
	list = new JList(model);
	scroll = new JScrollPane(list);
	scroll.setVisible(true);
	add(scroll);
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	scroll.setSize(width, height);
    }

}
