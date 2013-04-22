/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
abstract public class LeveledRecord extends MajorRecord implements Iterable<LeveledEntry> {

    static final SubPrototype LeveledProto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    add(new SubData("LVLD", new byte[1]));
	    add(new SubFlag("LVLF", 1));
	    add(new SubListCounted<>("LLCT", 1, new LeveledEntry()));
	}
    };

    /**
     * Creates a Leveled List with no entries and default settings. LVLN_Flags
     * set to 0x01=All levels Chance none set to 0. Empty MODL, MODT, and COED.
     */
    LeveledRecord() {
	super();
	subRecords.setPrototype(LeveledProto);
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod
     * parameter.
     *
     * @param edid EDID to assign the record. Make sure it's unique.
     */
    public LeveledRecord(String edid) {
	this();
	originateFromPatch(edid);
	set(LVLFlag.CalcAllLevelsEqualOrBelowPC, true);
    }

    /**
     *
     * @return An iterator that steps through each entry in the LVLN.
     */
    @Override
    public Iterator<LeveledEntry> iterator() {
	return subRecords.getSubList("LVLO").iterator();
    }

    /**
     * This enum represents the different flags offered by LVLN.
     */
    public enum LVLFlag {

	/**
	 *
	 */
	CalcAllLevelsEqualOrBelowPC,
	/**
	 *
	 */
	CalcForEachItemInCount,
        /**
	 *
	 */
        UseAll;
    }

    /**
     *
     */
    public void clearEntries() {
	subRecords.getSubList("LVLO").clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<LeveledEntry> getEntries() {
	return subRecords.getSubList("LVLO").toPublic();
    }

    /**
     * Returns all non-leveled list entries, with leveled list entries recursively
     *  replaced with their contents.
     * @return
     */
    public ArrayList<LeveledEntry> getFlattenedEntries() {
	ArrayList<LeveledEntry> out = new ArrayList<>();
	for (LeveledEntry entry : getEntries()) {
	    MajorRecord o = SPDatabase.getMajor(entry.getForm());
	    if (o instanceof LeveledRecord) {
		out.addAll(((LeveledRecord)o).getFlattenedEntries());
	    } else {
		out.add(entry);
	    }
	}
	return out;
    }

    /**
     * Adds the desired entry to the LVLN. Duplicates are accepted.
     *
     * @param entry LVLO to add to this LVLN
     */
    public void addEntry(LeveledEntry entry) {
	subRecords.getSubList("LVLO").add(entry);
    }

    /**
     * Adds an entry with the actor, level, and count provided.
     *
     * @param id The formID of the actor to put on the entry.
     * @param level Level to mark the entry at.
     * @param count Number of actors to spawn.
     */
    public void addEntry(FormID id, int level, int count) {
	addEntry(new LeveledEntry(id, level, count));
    }

    /**
     *
     * @return The number of entries contained in the LVLN.
     */
    public int numEntries() {
	return subRecords.getSubList("LVLO").size();
    }

    /**
     *
     * @return True if LVLN has no entries.
     */
    public Boolean isEmpty() {
	return numEntries() == 0;
    }

    /**
     *
     * @param i The zero based index to query.
     * @return The ith entry in the LVLN.
     */
    public LeveledEntry getEntry(int i) {
	return (LeveledEntry) subRecords.getSubList("LVLO").get(i);
    }

    /**
     * Removes the ith entry from the LVLN.
     *
     * @param i The zero based index to remove.
     */
    public void removeEntry(int i) {
	subRecords.getSubList("LVLO").remove(i);
    }

    /**
     *
     * @param id
     */
    public void removeFirstEntry(FormID id) {
	ArrayList<LeveledEntry> list = getEntries();
	for (int i = 0 ; i < list.size() ; i++) {
	    if (list.get(i).getForm().equals(id)) {
		list.remove(i);
		break;
	    }
	}
    }

    /**
     *
     * @param id
     */
    public void removeAllEntries(FormID id) {
	ArrayList<LeveledEntry> list = getEntries();
	for (int i = 0 ; i < list.size() ; ) {
	    if (list.get(i).getForm().equals(id)) {
		list.remove(i);
	    } else {
		i++;
	    }
	}
    }

    /**
     *
     * @return The percent chance nothing will spawn from this LVLN. (0.0-1.0)
     */
    public Double getChanceNonePct() {
	SubData lvld = subRecords.getSubData("LVLD");
	if (lvld.isValid()) {
	    return lvld.toInt() / 100.0;
	} else {
	    return 0.0;
	}
    }

    /**
     * Sets the chance none for this LVLN.
     *
     * @param in The chance that no creature will spawn from this LVLN.
     * @throws BadParameter If in is outside the range: (0-100)
     */
    public void setChanceNone(final int in) throws BadParameter {
	if (in >= 0 && in <= 100) {
	    subRecords.setSubData("LVLD", in, 1);
	} else {
	    throw new BadParameter("Chance none set outside range 0-100: " + in);
	}
    }

    /**
     *
     * @return The chance that no actor will spawn from this LVLN.
     */
    public int getChanceNone() {
	return subRecords.getSubData("LVLD").toInt();
    }

    /**
     * Checks a flag of the LVLN given by flag parameter.
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to check.
     * @return True if given flag is true.
     */
    public boolean get(LVLFlag flag) {
	return subRecords.getSubFlag("LVLF").is(flag.ordinal());
    }

    /**
     * Sets a flag of the LVLN given by flag parameter
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to set.
     * @param on Boolean to set flag to.
     */
    final public void set(LVLFlag flag, boolean on) {
	subRecords.setSubFlag("LVLF", flag.ordinal(), on);
    }

    /**
     *
     * @param target
     * @return
     */
    final public boolean contains(MajorRecord target) {
	for (LeveledEntry entry : this) {
	    if (entry.getForm().equals(target.getForm())) {
		return true;
	    }
	}
	return false;
    }

    /**
     *
     * @param target Record to look for.
     * @param replacement Record to replace with.
     * @return Number of replacements executed.
     */
    final public int replace(MajorRecord target, MajorRecord replacement) {
	int out = 0;
	FormID targetF = target.getForm();
	FormID replaceF = replacement.getForm();
	for (LeveledEntry entry : this) {
	    if (entry.getForm().equals(targetF)) {
		out++;
		entry.setForm(replaceF);
	    }
	}
	return out;
    }
}
