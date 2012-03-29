/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Justin Swanson
 */
public abstract class LSaveFile <E extends Enum> {

    protected ArrayList<Map<E, Setting>> sets = new ArrayList<Map<E, Setting>>();
    private static String header = "SaveFile";
    public Map<E, Setting> defaultSettings = new TreeMap<E, Setting>();
    public Map<E, Setting> saveSettings = new TreeMap<E, Setting>();
    public Map<E, Setting> curSettings = new TreeMap<E, Setting>();
    public Map<E, Setting> tempCurSettings = new TreeMap<E, Setting>();
    public Map<E, String> helpInfo = new TreeMap<E, String>();

    public void tie(E s, LUserSetting c) {
	for (Map<E, Setting> e : sets) {
	    if (e.containsKey(s)) {
		e.get(s).tie(c);
	    }
	}
    }

    protected LSaveFile() {
	sets.add(defaultSettings);
	sets.add(saveSettings);
	sets.add(curSettings);
	sets.add(tempCurSettings);
    }

    public void init() {
	for (Map<E, Setting> e : sets) {
	    init(e);
	}
	initHelp();
    }

    protected abstract void init(Map<E, Setting> m);
    
    protected void initHelp () {
	
    }

    protected abstract void readInSettings();

    protected abstract void saveToFile();

    protected void Add(Map<E, Setting> m, E type, Setting s) {
	m.put(type, s);
    }

    protected void Add(Map<E, Setting> m, E type, String title, Boolean inGame, Boolean b) {
	Add(m, type, new SaveBool(title, b, inGame));
    }

    protected void Add(Map<E, Setting> m, E type, String title, Boolean inGame, String s) {
	Add(m, type, new SaveString(title, s, inGame));
    }

    protected void Add(Map<E, Setting> m, E type, String title, Boolean inGame, Integer i) {
	Add(m, type, new SaveInt(title, i, inGame));
    }

    protected void Add(Map<E, Setting> m, E type, String title, Boolean inGame, Float f) {
	Add(m, type, new SaveFloat(title, f, inGame));
    }

    public static void copyTo(Map<Enum, Setting> from, Map<Enum, Setting> to) {
	to.clear();
	for (Enum s : from.keySet()) {
	    to.put(s, from.get(s).copyOf());
	}
    }

    public void update() {
	for (E s : curSettings.keySet()) {
	    curSettings.get(s).set();
	}
    }

    public void set(E setting, Object in) {
	curSettings.get(setting).setTo(in);
    }

    public String getStr(E s) {
        return curSettings.get(s).getStr();
    }

    public Integer getInt(E s) {
        return curSettings.get(s).getInt();
    }

    public Boolean getBool(E s) {
        return curSettings.get(s).getBool();
    }
}
