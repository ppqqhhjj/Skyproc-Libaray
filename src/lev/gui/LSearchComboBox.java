/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Justin Swanson
 */
public class LSearchComboBox<T extends Object> extends LComboBox<T> {

    ArrayList<T> backup;
    JTextField search;
    static String searchText = "Search...";
    LButton enterButton;
    FilterWorker worker = new FilterWorker("");

    /**
     *
     * @param title_
     */
    public LSearchComboBox(String title_, Font font, Color shade) {
	super(title_, font, shade);
	titleLabel.addShadow();
	backup = new ArrayList<>();
	box.setLocation(box.getX(), titleLabel.getY() + titleLabel.getHeight() + 10);
	search = new JTextField();
	search.setLocation(titleLabel.getX() + titleLabel.getWidth() + 10, 0);
	search.setText(searchText);
	search.setVisible(true);
	search.addFocusListener(new FocusListener() {

	    @Override
	    public void focusGained(FocusEvent e) {
		if (search.getText().equals(searchText)) {
		    search.setText("");
		}
	    }

	    @Override
	    public void focusLost(FocusEvent e) {
		if (search.getText().trim().equals("")) {
		    search.setText(searchText);
		}
	    }
	});
	addDocumentListener(new DocumentListener() {

	    @Override
	    public void insertUpdate(DocumentEvent e) {
		filterItems();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent e) {
		filterItems();
	    }

	    @Override
	    public void changedUpdate(DocumentEvent e) {
	    }
	});
	add(search);
    }

    void filterItems() {
	String trimmed = search.getText().trim();
	if (trimmed.equals(searchText)) {
	    trimmed = "";
	}
	trimmed = trimmed.toUpperCase();
	worker.cancel(true);
	box.removeAllItems();
	worker = new FilterWorker(trimmed);
	worker.execute();
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	search.setSize(x - titleLabel.getWidth() - titleLabel.getX() - 10, titleLabel.getHeight());
	box.setSize(x, y - titleLabel.getY() - titleLabel.getHeight() - 10);
	if (enterButton != null) {
	    enterButton.setLocation(x - enterButton.getWidth(), search.getY() + search.getHeight() + 10);
	    box.setSize(x - enterButton.getWidth() - 10, box.getHeight());
	    enterButton.setSize(enterButton.getWidth(), box.getHeight());
	}
    }

    /**
     *
     */
    @Override
    public void removeAllItems() {
	super.removeAllItems();
	backup.clear();
    }

    public int getBackupListSize () {
	return backup.size();
    }

    @Override
    public void addItem(T o) {
	super.addItem(o);
	backup.add(o);
    }

    public void reset() {
	setText(searchText);
    }

    public void setText(String s) {
	search.setText(s);
    }

    public String getText() {
	return search.getText();
    }

    public void addEnterButton(String label, ActionListener done) {
	enterButton = new LButton(label);
	enterButton.addActionListener(done);
	add(enterButton);
	setSize(getSize().width, getSize().height);
    }

    class FilterWorker extends SwingWorker<Integer, Integer> {

	String filter;

	FilterWorker(String s) {
	    filter = s;
	}

	@Override
	protected Integer doInBackground() throws Exception {
	    for (T item : backup) {
		if (item.toString().toUpperCase().contains(filter)) {
		    box.addItem(item);
		}
	    }
	    return 1;
	}

	@Override
	protected void done() {
	}
    }

    public final void addDocumentListener(DocumentListener d) {
	search.getDocument().addDocumentListener(d);
    }
}
