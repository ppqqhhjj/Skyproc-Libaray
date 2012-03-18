package skyproc;

import lev.LShrinkArray;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import skyproc.exceptions.BadRecord;
import skyproc.exceptions.BadParameter;

/**
 * Abstract class outlining functions for a generic record.
 * @author Justin Swanson
 */
public abstract class Record extends ExportRecord implements Serializable {

    Record() {
    }

    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter  {
        in.skip(getIdentifierLength() + getSizeLength());
    }

    final void parseData(ByteBuffer in) throws BadRecord, DataFormatException, BadParameter  {
        parseData(new LShrinkArray(in));
    }

    abstract Boolean isValid();

    /**
     * Returns a short summary/title of the record.
     * @return A short summary/title of the record.
     */
    @Override
    public abstract String toString();

    /**
     * Returns the contents of the record, or exports them to a log, depending on the record type.
     * @return The contents of the record, OR the empty string, depending on the record type.
     */
    public abstract String print();

    abstract Type[] getTypes();

    abstract Record getNew();

    Record copyOf(Mod modToOriginateFrom) {
        return (Record) Ln.deepCopy(this);
    }

    static Type matchType(String str) throws BadRecord {
        for (Type t : Type.values()) {
            if (t.toString().equals(str)) {
                return t;
            }
        }
        throw new BadRecord("Record " + str + " ("
                + Ln.printHex(Ln.toIntArray(str), true, false));
    }

    static Type getNextType(LShrinkArray in) throws BadRecord {
        return (matchType(Ln.arrayToString(in.getInts(0, 4))));
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        if (isValid()) {
            out.write(getTypes()[0].toString());
            out.write(getContentLength(srcMod));
        }
    }

    private int extractRecordLength(LShrinkArray in) {
        return Ln.arrayToInt(in.getInts(getIdentifierLength(), getSizeLength()))
                + getSizeLength() + getIdentifierLength() + getFluffLength();
    }

    LShrinkArray extractRecordData(LShrinkArray in) {
        int recordLength = extractRecordLength(in);
        LShrinkArray extracted = new LShrinkArray(in, recordLength);
        in.skip(recordLength);
        return extracted;
    }

    int getHeaderLength() {
        return getIdentifierLength() + getSizeLength() + getFluffLength();
    }

    final int getIdentifierLength() {
        return 4;
    }

    abstract int getSizeLength();

    abstract int getFluffLength();

    int getTotalLength(Mod srcMod) {
        if (isValid()) {
            return getContentLength(srcMod) + getHeaderLength();
        } else {
            return 0;
        }
    }

    abstract int getContentLength(Mod srcMod);

    void newSyncLog(String fileName) {
        SPGlobal.newSyncLog(fileName);
    }

    boolean logging() {
        return SPGlobal.logging();
    }

    void logMain(String header, String... log) {
        SPGlobal.logMain(header, log);
    }

    void logSync(String header, String... log) {
        SPGlobal.logSync(getTypes()[0].toString(), log);
    }

    void logError(String header, String... log) {
        SPGlobal.logError(header, log);
    }

    void logSpecial(Enum e, String header, String... log) {
        SPGlobal.logSpecial(e, header, log);
    }

    void log(String header, String... log) {
        SPGlobal.log(header, log);
    }

    void newLog(String fileName) {
        SPGlobal.newLog(fileName);
    }

    void flush() {
        SPGlobal.flush();
    }
}
