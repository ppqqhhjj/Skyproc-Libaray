/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import skyproc.SPGlobal;

/**
 * A class that manages importing/exporting of save files, as well as providing
 * methods for LUserSetting GUI components to automatically update tied
 * settings.<br><br>
 *
 * NOTE: <br> The in-game parameters currently have no effect at the moment.
 * <br><br>
 *
 * To use it:<br> 1) Create an enum class defining a name for each setting you
 * want. 2) Extend LSaveFile with your own class that defines its own init
 * functions using the setting enum you created.
 *
 * @author Justin Swanson
 */
public abstract class LSaveFile {

    /**
     * List containing default, save, temp, and current setting maps.
     */
    ArrayList<Map<Enum, Setting>> maps = new ArrayList<Map<Enum, Setting>>();
    private static String header = "SaveFile";
    /**
     * Stores the default values for each setting.
     */
    public Map<Enum, Setting> defaultSettings = new TreeMap<Enum, Setting>();
    /**
     * Stores the previously saved settings of the current end user.
     */
    public Map<Enum, Setting> saveSettings = new TreeMap<Enum, Setting>();
    /**
     * Stores the current settings displayed on the GUI.
     */
    public Map<Enum, Setting> curSettings = new TreeMap<Enum, Setting>();
    Map<Enum, Setting> tempCurSettings = new TreeMap<Enum, Setting>();
    /**
     * Map containing the help text associated with settings in the saveFile.
     */
    public Map<Enum, String> helpInfo = new TreeMap<Enum, String>();
    boolean initialized = false;

    /**
     * Ties the LUserSetting to the Enum key
     *
     * @param s Enum key to tie to
     * @param c Setting to tie with
     */
    public void tie(Enum s, LUserSetting c) {
	for (Map<Enum, Setting> e : maps) {
	    if (e.containsKey(s)) {
		e.get(s).tie(c);
	    }
	}
    }

    /**
     *
     */
    protected LSaveFile() {
	maps.add(defaultSettings);
	maps.add(saveSettings);
	maps.add(curSettings);
	maps.add(tempCurSettings);
    }

    /**
     * Call this function at the start of your program to signal the savefile to
     * load its settings and prep for use.
     */
    public void init() {
	if (!initialized) {
	    initSettings();
	    initHelp();
	    readInSettings();
	    initialized = true;
	}
    }

    /**
     * An abstract function that should contain Add() calls that define each
     * setting in the saveFile and their default values.
     */
    protected abstract void initSettings();

    /**
     * A function that loads the help map with help text for any settings that
     * you desire.
     */
    protected abstract void initHelp();

    void readInSettings() {
	File f = new File(SPGlobal.pathToInternalFiles + "Savefile");
	SPGlobal.log("SaveFile Import", "Starting import");
	if (f.exists()) {
	    try {
		BufferedReader input = new BufferedReader(new FileReader(f));
		input.readLine();  //title
		String inStr;
		String settingTitle;
		while (input.ready()) {
		    inStr = input.readLine().trim();
		    if (inStr.equals("")) {
			continue;
		    }
		    settingTitle = inStr.substring(4, inStr.indexOf(" to "));
		    inStr = inStr.substring(inStr.indexOf(" to ") + 4);
		    for (Enum s : saveSettings.keySet()) {
			if (saveSettings.containsKey(s)) {
			    if (saveSettings.get(s).getTitle().equals(settingTitle)) {
				// Multiline setting
				if (saveSettings.get(s).getClass() == SaveStringSet.class) {
				    int num = Integer.valueOf(inStr.trim());
				    inStr = "";
				    for (int i = 0 ; i < num ; i++) {
					inStr += input.readLine();
				    }
				}
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

    /**
     * Tells the savefile to write its values to the disk. Should be called as
     * the program is ending.
     */
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
	for (Map<Enum, Setting> m : maps) {
	    m.put(type, s.copyOf());
	}
    }

    /**
     * Adds a setting of type boolean.
     *
     * @param type Enum to be associated with.
     * @param inGame Defines this setting to be exported to an INI file to be
     * read in by Skyrim.
     * @param b Default value to assign the setting.
     */
    protected void Add(Enum type, Boolean b, boolean patchChanging) {
	Add(type, new SaveBool(type.toString(), b, patchChanging));
    }

    /**
     * Adds a setting of type string.
     *
     * @param type Enum to be associated with.
     * @param inGame Defines this setting to be exported to an INI file to be
     * read in by Skyrim.
     * @param s Default value to assign the setting.
     */
    protected void Add(Enum type, String s, boolean patchChanging) {
	Add(type, new SaveString(type.toString(), s, patchChanging));
    }

    /**
     * Adds a setting of type integer.
     *
     * @param type Enum to be associated with.
     * @param inGame Defines this setting to be exported to an INI file to be
     * read in by Skyrim.
     * @param i Default value to assign the setting.
     */
    protected void Add(Enum type, Integer i, boolean patchChanging) {
	Add(type, new SaveInt(type.toString(), i, patchChanging));
    }

    /**
     * Adds a setting of type integer.
     *
     * @param type Enum to be associated with.
     * @param inGame Defines this setting to be exported to an INI file to be
     * read in by Skyrim.
     * @param i Default value to assign the setting.
     */
    protected void Add(Enum type, Set<String> strs, boolean patchChanging) {
	Add(type, new SaveStringSet(type.toString(), strs, patchChanging));
    }

    /**
     * Adds a setting of type float.
     *
     * @param type Enum to be associated with.
     * @param inGame Defines this setting to be exported to an INI file to be
     * read in by Skyrim.
     * @param f Default value to assign the setting.
     */
    protected void Add(Enum type, Float f, boolean patchChanging) {
	Add(type, new SaveFloat(type.toString(), f, patchChanging));
    }

    /**
     * Copies one map of settings to another. For reverting current settings to
     * default, for example.
     *
     * @param from
     * @param to
     */
    public static void copyTo(Map<Enum, Setting> from, Map<Enum, Setting> to) {
	to.clear();
	for (Enum s : from.keySet()) {
	    to.put(s, from.get(s).copyOf());
	}
    }

    /**
     * Makes the savefile reacquire the settings from any tied GUI components.
     */
    public void update() {
	for (Enum s : curSettings.keySet()) {
	    curSettings.get(s).set();
	}
    }

    void set(Enum setting, Object in) {
	curSettings.get(setting).setTo(in);
    }

    /**
     * Makes the savefile's GUI ties display saved settings, and highlights ones
     * that have changed.
     */
    public void peekSaved() {
	peek(saveSettings);
    }

    /**
     * Makes the savefile's GUI ties display default settings, and highlights
     * ones that have changed.
     */
    public void peekDefaults() {
	peek(defaultSettings);
    }

    /**
     * Clears any "peeked" states, reverts all GUI components to the "current"
     * settings, and clears any highlighting.
     */
    public void clearPeek() {
	for (Setting s : curSettings.values()) {
	    if (s.tie != null) {
		s.tie.revertTo(tempCurSettings);
		s.tie.clearHighlight();
	    }
	}
	update();
    }

    void peek(Map<Enum, Setting> in) {
	copyTo(curSettings, tempCurSettings);
	for (Setting s : curSettings.values()) {
	    if (s.tie != null && !s.tie.revertTo(in)) {
		s.tie.highlightChanged();
	    }
	}
	update();
    }

    /**
     * Reverts a GUI component to the saved setting
     *
     * @param s
     */
    public void revertToSaved(LUserSetting s) {
	revertTo(saveSettings, s);
    }

    /**
     * Reverts a GUI component to the default setting
     *
     * @param s
     */
    public void revertToDefault(LUserSetting s) {
	revertTo(defaultSettings, s);
    }

    /**
     * Reverts the setting to its saved state
     *
     * @param setting
     */
    public void revertToSaved(Enum setting) {
	revertTo(saveSettings, curSettings.get(setting).tie);
    }

    /**
     * Reverts the setting to its default state
     *
     * @param setting
     */
    public void revertToDefault(Enum setting) {
	revertTo(defaultSettings, curSettings.get(setting).tie);
    }

    void revertTo(Map<Enum, Setting> in, LUserSetting s) {
	s.revertTo(in);
	copyTo(in, tempCurSettings);
    }

    public boolean needsPatch() {
	ArrayList<Setting> modified = getModifiedSettings();
	for (Setting s : modified) {
	    if (s.patchChanging) {
		return true;
	    }
	}
	return false;
    }

    public ArrayList<Setting> getModifiedSettings () {
	return getDiff(saveSettings, curSettings);
    }

    public ArrayList<Setting> getDiff (Map<Enum, Setting> lhs, Map<Enum, Setting> rhs) {
	ArrayList<Setting> out = new ArrayList<>();
	for (Enum e : lhs.keySet()) {
	    if (!lhs.get(e).equals(rhs.get(e))) {
		out.add(rhs.get(e));
	    }
	}
	return out;
    }

    /**
     * Returns the value of the setting, and assumes it's a string value.
     *
     * @param s
     * @return
     */
    public String getStr(Enum s) {
	return curSettings.get(s).getStr();
    }

    /**
     * Returns the value of the setting, and assumes it's an int value.
     *
     * @param s
     * @return
     */
    public Integer getInt(Enum s) {
	return curSettings.get(s).getInt();
    }

    /**
     * Returns the value of the setting, and assumes it's a boolean value.
     *
     * @param s
     * @return
     */
    public Boolean getBool(Enum s) {
	return curSettings.get(s).getBool();
    }

    /**
     * Returns the value of the setting, and assumes it's a boolean value.
     *
     * @param s
     * @return
     */
    public Set<String> getStrings(Enum s) {
	return curSettings.get(s).getStrings();
    }
}
