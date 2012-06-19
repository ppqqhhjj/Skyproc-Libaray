/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.NotFound;

/**
 *
 * @author Justin Swanson
 */
abstract public class LeveledRecord extends MajorRecord implements Iterable<LVLO> {

    SubList<LVLO> entries = new SubList<LVLO>(Type.LLCT, 1, new LVLO());
    SubData OBND = new SubData(Type.OBND, 12);
    SubData LVLD = new SubData(Type.LVLD);
    SubFlag LVLF = new SubFlag(Type.LVLF, 1);

    /**
     * Creates a Leveled List with no entries and default settings. LVLN_Flags
     * set to 0x01=All levels Chance none set to 0. Empty MODL, MODT, and COED.
     */
    LeveledRecord() {
	super();
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod
     * parameter.
     *
     * @param modToOriginateFrom Mod to mark the LVLN as originating from.
     * @param edid EDID to assign the record. Make sure it's unique.
     */
    public LeveledRecord(Mod modToOriginateFrom, String edid) {
	super(modToOriginateFrom, edid);
	LVLD.initialize(1);
	OBND.initialize(12);
	set(LVLFlag.CalcAllLevelsEqualOrBelowPC, true);
    }

    /**
     *
     * @return An iterator that steps through each entry in the LVLN.
     */
    @Override
    public Iterator<LVLO> iterator() {
	return entries.iterator();
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
	CalcForEachItemInCount;
    }

    /**
     *
     */
    public void clearEntries() {
	entries.clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<LVLO> getEntries() {
	return entries.toPublic();
    }

    /**
     * Returns all non-leveled list entries, with leveled list entries recursively
     *  replaced with their contents.
     * @return
     */
    public ArrayList<LVLO> getFlattenedEntries() {
	ArrayList<LVLO> out = new ArrayList<LVLO>();
	for (LVLO entry : getEntries()) {
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
    public void addEntry(LVLO entry) {
	entries.add(entry);
    }

    /**
     * Adds an entry with the actor, level, and count provided.
     *
     * @param id The formID of the actor to put on the entry.
     * @param level Level to mark the entry at.
     * @param count Number of actors to spawn.
     * @throws NotFound This functionality to come. Skyproc does NOT confirm
     * that the FormID associated truly points to a correct record. You will
     * have to confirm the accuracy yourself for now.
     */
    public void addEntry(FormID id, int level, int count) throws NotFound {
	addEntry(new LVLO(id, level, count));
    }

    /**
     *
     * @return The number of entries contained in the LVLN.
     */
    public int numEntries() {
	return entries.size();
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
    public LVLO getEntry(int i) {
	return entries.get(i);
    }

    /**
     * Removes the ith entry from the LVLN.
     *
     * @param i The zero based index to remove.
     */
    public void removeEntry(int i) {
	entries.remove(i);
    }

    /**
     *
     * @return The percent chance nothing will spawn from this LVLN. (0.0-1.0)
     */
    public Double getChanceNonePct() {
	if (LVLD.isValid()) {
	    return LVLD.toInt() / 100.0;
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
	    LVLD.setData(in);
	} else {
	    throw new BadParameter("Chance none set outside range 0-100: " + in);
	}
    }

    /**
     *
     * @return The chance that no actor will spawn from this LVLN.
     */
    public int getChanceNone() {
	return LVLD.toInt();
    }

    /**
     * Checks a flag of the LVLN given by flag parameter.
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to check.
     * @return True if given flag is true.
     */
    public boolean get(LVLFlag flag) {
	return LVLF.is(flag.ordinal());
    }

    /**
     * Sets a flag of the LVLN given by flag parameter
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to set.
     * @param on Boolean to set flag to.
     */
    final public void set(LVLFlag flag, boolean on) {
	LVLF.set(flag.ordinal(), on);
    }
}
