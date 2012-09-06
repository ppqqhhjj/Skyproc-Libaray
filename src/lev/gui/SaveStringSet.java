/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Justin Swanson
 */
public class SaveStringSet extends Setting<Set<String>> {

    static String delimiter = "<#>";

    /**
     *
     * @param title_
     * @param data_
     * @param patchChanging
     */
    public SaveStringSet(String title_, Set<String> data_, Boolean patchChanging) {
	super(title_, data_, patchChanging);
    }

    @Override
    public String toString() {
	String out = "";
	int num = 0;
	for (String f : data) {
	    if (!f.equals("")) {
		out += f + delimiter + "\n" ;
		num++;
	    }
	}
	return num + "\n" + out;
    }

    @Override
    public void parse(String in) {
	String[] split = in.split(delimiter);
	data = new HashSet<>(split.length);
	for (int i = 0; i < split.length; i++) {
	    data.add(split[i]);
	}
    }

    @Override
    public Setting<Set<String>> copyOf() {
	SaveStringSet out = new SaveStringSet(title, data, patchChanging);
	out.data = new HashSet<>(data);
	out.tie = tie;
	return out;
    }
}