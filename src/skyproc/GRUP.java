package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.gui.SPProgressBarPlug;

/**
 * A GRUP is a collection of Major Records.
 *
 * @param <T> The type of Major Record a GRUP contains.
 * @author Justin Swanson
 */
public class GRUP<T extends MajorRecord> extends Record implements Iterable<T> {

    byte[] grupType = new byte[4];
    byte[] dateStamp = {0x13, (byte) 0x6F, 0, 0};
    byte[] version = new byte[4];
    ArrayList<T> listRecords = new ArrayList<>();
    Map<FormID, T> mapRecords = new HashMap<>();
    Mod srcMod;
    T prototype;
    private final static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.GRUP}));

    GRUP(Mod srcMod_, T prototype) {
	srcMod = srcMod_;
	this.prototype = prototype;
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    /**
     *
     * @return An enum constant representing the type of record the GRUP
     * contains.
     */
    public GRUP_TYPE getContainedType() {
	return GRUP_TYPE.toRecord(prototype.getType());
    }

    /**
     *
     * @return A generic title of the GRUP. (eg. "Skyrim.esm - LVLN GRUP")
     */
    @Override
    public String toString() {
	return srcMod.getName() + " - " + getContainedType().toString() + " GRUP";
    }

    /**
     *
     * @return True if GRUP has records. (size > 0)
     */
    @Override
    Boolean isValid() {
	return !isEmpty();
    }

    /**
     *
     * @return Returns true if GRUP contains no records.
     */
    public Boolean isEmpty() {
	return mapRecords.isEmpty();
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	in.skip(4); // GRUP type
	grupType = in.extract(4); // What kind of GRUP data it has.
	dateStamp = in.extract(4);
	version = in.extract(4);
	while (!in.isDone()) {
	    if (logging()) {
		logSync(toString(), "============== Extracting Next " + getContainedType() + " =============");
	    }
	    T item = (T) prototype.getNew();
	    item.srcMod = srcMod;
	    item.subRecords.setMajor(item);
	    try {

		item.parseData(item.extractRecordData(in));

		// Customizable middle stage for specialized GRUPs
		parseDataHelper(item);

		// Add to GRUP
		if (item.isValid()) {
		    addRecord(item);
		} else if (logging()) {
		    logSync(toString(), "Did not add " + getContainedType().toString() + " " + item.toString() + " because it was not valid.");
		}

		if (logging()) {
		    logSync(toString(), "=============== DONE ==============");
		}
	    } catch (java.nio.BufferUnderflowException e) {
		handleBadRecord(item, e.toString());
	    }
	}
	if (logging()) {
	    logSync(toString(), "Data exhausted");
	}
    }

    void parseDataHelper(T item) {
    }

    /**
     *
     * @return Number of records the GRUP contains
     */
    public int size() {
	return listRecords.size();
    }

    /**
     * Prints contents of GRUP to the asynchronous log.
     *
     * @return Returns the empty string.
     */
    @Override
    public String print() {
	if (!isEmpty()) {
	    logSync(toString(), "=======================================================================");
	    logSync(toString(), "========================= Printing " + getContainedType().toString() + "s =============================");
	    logSync(toString(), "=======================================================================");
	    for (T t : mapRecords.values()) {
		t.toString();
	    }
	}
	return "";
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    super.export(out, srcMod);
	    out.write(this.getContainedType().toString());
	    out.write(grupType, 4);
	    out.write(dateStamp, 4);
	    out.write(version, 4);
	    if (logging()) {
		logSync(this.toString(), "Exporting " + this.numRecords() + " " + getContainedType() + " records.");
	    }
	    for (MajorRecord t : this) {
		if (logging()) {
		    logSync(this.toString(), t.toString());
		}
		t.export(out, srcMod);
		SPProgressBarPlug.incrementBar();
	    }
	}
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (T item : listRecords) {
	    out.addAll(item.allFormIDs());
	}
	return out;
    }

    void fetchStringPointers() throws IOException {
	for (MajorRecord r : listRecords) {
	    r.fetchStringPointers();
	}
    }

    /**
     *
     * @return The number of contained records.
     */
    public int numRecords() {
	return mapRecords.size();
    }

    /**
     * Removes a record from the GRUP.
     *
     * @param id The FormID of the record to remove.
     * @return Returns true if a record was removed; False if no record with id
     * was contained.
     */
    public boolean removeRecord(FormID id) {
	if (mapRecords.containsKey(id)) {
	    listRecords.remove(mapRecords.get(id));
	    mapRecords.remove(id);
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Removes a record from the GRUP.
     *
     * @param item Major Record to remove. Removal is based on FormID match.
     * @return Returns true if a record was removed; False if no record with id
     * was contained.
     */
    public boolean removeRecord(T item) {
	return removeRecord(item.getForm());
    }

    void handleBadRecord(MajorRecord r, String reason) {
	if (logging()) {
	    if (r.isValid()) {
		logSync(toString(), "Caught a bad record: " + r + ", reason: " + reason);
		logSpecial(SPLogger.SpecialTypes.BLOCKED, toString(), "Caught a bad record: " + r + " from " + srcMod + ", reason: " + reason);
	    } else {
		logSync(toString(), "Caught a bad record, reason:" + reason);
		logSpecial(SPLogger.SpecialTypes.BLOCKED, toString(), "Caught a bad record, reason:" + reason);
	    }
	}
    }

    /**
     * Adds a record to the group, and does the following: 1) Standardizes the
     * record's FormIDs to the database the GRUP is contained in.
     *
     * @param item Record to add to the GRUP.
     */
    public void addRecord(T item) {
	removeRecord(item);
	mapRecords.put(item.getForm(), item);
	listRecords.add(item);
    }

    /**
     *
     * @return A list of all records in the GRUP
     */
    public ArrayList<T> getRecords() {
	return listRecords;
    }

    void standardizeMasters() {
	for (T item : listRecords) {
	    standardizeMaster(item);
	}
    }

    void standardizeMaster(T item) {
	ArrayList<FormID> set = item.allFormIDs();
	for (FormID id : set) {
	    id.standardize(srcMod);
	}
    }

    void addRecord(Object item) {
	addRecord((T) item);
    }

    /**
     *
     * @param id FormID to check check for containment.
     * @return Returns true if GRUP contains a record with id.
     */
    public Boolean contains(FormID id) {
	return mapRecords.containsKey(id);
    }

    /**
     *
     * @param item Record to check check for containment. (based on its FormID)
     * @return Returns true if GRUP contains a record with FormID == id.
     */
    public Boolean contains(T item) {
	return contains(item.getForm());
    }

    /**
     *
     * @param id FormID to query the GRUP for.
     * @return Major Record with FormID == id.
     */
    public MajorRecord get(FormID id) {
	return mapRecords.get(id);
    }

    /**
     * Deletes all records from the GRUP.
     */
    public void clear() {
	listRecords.clear();
	mapRecords.clear();
    }

    @Override
    Record getNew() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Takes all records from rhs and adds them to the GRUP. Any conflicting
     * records (those whose FormIDs match) will be replaced with rhs's version.
     *
     * @param rhs GRUP to copy records from.
     */
    public void merge(GRUP<T> rhs) {
	if (logging() && SPGlobal.debugModMerge) {
	    log(toString(), "Size before: " + numRecords());
	}
	for (MajorRecord item : rhs) {
	    if (logging() && SPGlobal.debugModMerge) {
		if (contains(item.getForm())) {
		    log(toString(), "Replacing record " + item.toString() + " with one from " + rhs.toString());
		} else {
		    log(toString(), "Adding record " + item.toString());
		}
	    }
	    addRecord(item);
	}
	if (logging() && SPGlobal.debugModMerge) {
	    log(toString(), "Size after: " + numRecords());
	}
    }

    @Override
    int getFluffLength() {
	return 16;
    }

    @Override
    int getSizeLength() {
	return 4;
    }

    @Override
    int getContentLength(Mod srcMod) {
	int length = getHeaderLength();
	for (T t : listRecords) {
	    if (t.isValid()) {
		length += t.getTotalLength(srcMod);
	    }
	}
	return length;
    }

    /**
     *
     * @return An iterator that steps through each record in the GRUP, in the
     * order they were added.
     */
    @Override
    public Iterator<T> iterator() {
	ArrayList<T> temp = new ArrayList<>();
	temp.addAll(listRecords);
	return temp.iterator();
    }
}
