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
public abstract class LSaveFile {

    protected ArrayList<Map<Enum, Setting>> sets = new ArrayList<Map<Enum, Setting>>();
    private static String header = "SaveFile";
    public Map<Enum, Setting> defaultSettings = new TreeMap<Enum, Setting>();
    public Map<Enum, Setting> saveSettings = new TreeMap<Enum, Setting>();
    public Map<Enum, Setting> curSettings = new TreeMap<Enum, Setting>();
    public Map<Enum, Setting> tempCurSettings = new TreeMap<Enum, Setting>();
    public Map<Enum, String> helpInfo = new TreeMap<Enum, String>();

    public void tie(Enum s, LUserSetting c) {
	for (Map<Enum, Setting> e : sets) {
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
	for (Map<Enum, Setting> e : sets) {
	    init(e);
	}
	initHelp();
	readInSettings();
    }

    protected abstract void init(Map<Enum, Setting> m);

    protected void initHelp () {

    }

    public abstract void readInSettings();

    public abstract void saveToFile();

    protected void Add(Map<Enum, Setting> m, Enum type, Setting s) {
	m.put(type, s);
    }

    protected void Add(Map<Enum, Setting> m, Enum type, String title, Boolean inGame, Boolean b) {
	Add(m, type, new SaveBool(title, b, inGame));
    }

    protected void Add(Map<Enum, Setting> m, Enum type, String title, Boolean inGame, String s) {
	Add(m, type, new SaveString(title, s, inGame));
    }

    protected void Add(Map<Enum, Setting> m, Enum type, String title, Boolean inGame, Integer i) {
	Add(m, type, new SaveInt(title, i, inGame));
    }

    protected void Add(Map<Enum, Setting> m, Enum type, String title, Boolean inGame, Float f) {
	Add(m, type, new SaveFloat(title, f, inGame));
    }

    public static void copyTo(Map<Enum, Setting> from, Map<Enum, Setting> to) {
	to.clear();
	for (Enum s : from.keySet()) {
	    to.put(s, from.get(s).copyOf());
	}
    }

    public void update() {
	for (Enum s : curSettings.keySet()) {
	    curSettings.get(s).set();
	}
    }

    public void set(Enum setting, Object in) {
	curSettings.get(setting).setTo(in);
    }

    public String getStr(Enum s) {
        return curSettings.get(s).getStr();
    }

    public Integer getInt(Enum s) {
        return curSettings.get(s).getInt();
    }

    public Boolean getBool(Enum s) {
        return curSettings.get(s).getBool();
    }
}
