package skyproc;

import lev.LShrinkArray;
import java.util.Iterator;
import skyproc.exceptions.BadParameter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.LFileChannel;
import skyproc.MajorRecord.Mask;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.NotFound;
import skyproc.exceptions.BadRecord;

/**
 * A GRUP is a collection of Major Records.
 *
 * @param <T> The type of Major Record a GRUP contains.
 * @author Justin Swanson
 */
public class GRUP<T extends MajorRecord> extends Record implements Iterable<T> {

    private byte[] grupType = new byte[4];
    private byte[] dateStamp = new byte[4];
    private byte[] version = new byte[4];
    ArrayList<T> listRecords = new ArrayList<T>();
    Map<FormID, T> mapRecords = new HashMap<FormID, T>();
    Mod srcMod;
    T prototype;
    private static final Type[] type = {Type.GRUP};

    GRUP(Mod srcMod_, T prototype) {
        srcMod = srcMod_;
        this.prototype = prototype;
    }

    void parseData(ByteBuffer in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
        parseData(new LShrinkArray(in), mask);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    /**
     *
     * @return An enum constant representing the type of record the GRUP
     * contains.
     */
    public Type getContainedType() {
        return prototype.getTypes()[0];
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
    final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        parseData(in, null);
    }

    void parseData(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        in.skip(4); // GRUP type
        grupType = in.extract(4);
        dateStamp = in.extract(4);
        version = in.extract(4);
        while (!in.isEmpty()) {
            if (logging()) {
                logSync(toString(), "============== Extracting Next " + getContainedType() + " =============");
            }
            T item = (T) prototype.getNew();
            try {

                item.parseData(item.extractRecordData(in), mask);

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
            } catch (BadRecord e) {
                handleBadRecord(item, e.toString());
            }
            flush();
        }
        if (logging()) {
            logSync(toString(), "Data exhausted");
        }
    }

    void parseDataHelper(T item) {
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
                flush();
            }
        }
        return "";
    }

    @Override
    void export(LExportParser out, Mod srcMod) throws IOException {
        if (isValid()) {
            super.export(out, srcMod);
            out.write(this.getContainedType().toString());
            out.write(grupType, 4);
            out.write(dateStamp, 4);
            out.write(version, 4);
            if (logging()) {
                logSync(this.toString(), "Exporting " + this.numRecords() + " " + getContainedType() + " records.");
            }
            for (T t : this) {
                t.export(out, srcMod);
                SPGuiPortal.progress.incrementBar();
            }
        }
    }

    void standardizeMasters() {
        for (T item : listRecords) {
            item.standardizeMasters(srcMod);
        }
    }

    void fetchExceptions(SPDatabase database) {
        for (T item : mapRecords.values()) {
            item.fetchException(database);
            SPGuiPortal.progress.incrementBar();
        }
    }

    void fetchStringPointers(Mod srcMod, Map<Files, LFileChannel> streams) throws IOException {
        for (MajorRecord r : listRecords) {
            r.fetchStringPointers(srcMod, streams);
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
                logSync(toString(), "Caught a bad record: " + r.getFormStr() + ", reason: " + reason);
                logSpecial(SPLogger.SpecialTypes.BLOCKED, toString(), "Caught a bad record: " + r.getFormStr() + ", reason: " + reason);
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
        // Get Masters
        item.standardizeMasters(srcMod);

        mapRecords.put(item.getForm(), item);
        listRecords.add(item);
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
     * @throws NotFound
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
        for (T item : rhs) {
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
            length += t.getTotalLength(srcMod);
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
        ArrayList<T> temp = new ArrayList<T>();
        temp.addAll(listRecords);
        return temp.iterator();
    }
}
