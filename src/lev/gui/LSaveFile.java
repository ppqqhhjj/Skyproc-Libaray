/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import skyproc.SPGlobal;

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
    boolean initialized = false;

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
	if (!initialized) {
	    initSettings();
	    initHelp();
	    readInSettings();
	    initialized = true;
	}
    }

    protected abstract void initSettings();

    protected abstract void initHelp();

    public void readInSettings() {
	File f = new File(SPGlobal.pathToInternalFiles + "Savefile");
	SPGlobal.log("SaveFile Import", "Starting import");
	if (f.exists()) {
	    try {
		BufferedReader input = new BufferedReader(new FileReader(f));
		input.readLine();  //title
		String inStr;
		String settingTitle;
		while (input.ready()) {
		    inStr = input.readLine();
		    settingTitle = inStr.substring(4, inStr.indexOf(" to "));
		    for (Enum s : saveSettings.keySet()) {
			if (saveSettings.containsKey(s)) {
			    if (saveSettings.get(s).getTitle().equals(settingTitle)) {
				saveSettings.get(s).readSetting(inStr);
				curSettings.get(s).readSetting(inStr);
			    }
			}
		    }
		}

	    } catch (Exception e) {
		JOptionPane.showMessageDialog(null, "Error in reading in save file. Reverting to default settings.");
		init();
	    }
	}
    }

    public void saveToFile() {

	File f = new File(SPGlobal.pathToInternalFiles);
	if (!f.isDirectory()) {
	    f.mkdirs();
	}
	f = new File(SPGlobal.pathToInternalFiles + "Savefile");
	if (f.isFile()) {
	    f.delete();
	}

	try {
	    BufferedWriter output = new BufferedWriter(new FileWriter(f));
	    output.write("Savefile used for the application.\n");
	    for (Enum s : curSettings.keySet()) {
		if (!curSettings.get(s).get().equals("")) {
		    SPGlobal.log("SaveFile Export", "Exporting to savefile: " + curSettings.get(s).getTitle() + " = " + curSettings.get(s));
		    curSettings.get(s).write(output);
		} else {
		    defaultSettings.get(s).write(output);
		}
	    }
	    output.close();
	} catch (java.io.IOException e) {
	    JOptionPane.showMessageDialog(null, "The application couldn't open the save file output stream.  Your settings were not saved.");
	}
    }

    void Add(Enum type, Setting s) {
	for (Map<Enum, Setting> m : sets) {
	    m.put(type, s.copyOf());
	}
    }

    protected void Add(Enum type, Boolean inGame, Boolean b) {
	Add(type, new SaveBool(type.toString(), b, inGame));
    }

    protected void Add(Enum type, Boolean inGame, String s) {
	Add(type, new SaveString(type.toString(), s, inGame));
    }

    protected void Add(Enum type, Boolean inGame, Integer i) {
	Add(type, new SaveInt(type.toString(), i, inGame));
    }

    protected void Add(Enum type, Boolean inGame, Float f) {
	Add(type, new SaveFloat(type.toString(), f, inGame));
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
