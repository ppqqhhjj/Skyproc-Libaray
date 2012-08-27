package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import lev.*;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Abstract class outlining functions for a generic record.
 *
 * @author Justin Swanson
 */
public abstract class Record extends ExportRecord implements Serializable {

    Record() {
    }

    void parseData(LStream in) throws BadRecord, BadParameter, DataFormatException, IOException {
	in.skip(getIdentifierLength() + getSizeLength());
    }

    final void parseData(ByteBuffer in) throws BadRecord, BadParameter, DataFormatException, IOException {
	parseData(new LShrinkArray(in));
    }

    Boolean isValid() {
	return true;
    }

    /**
     * Returns a short summary/title of the record.
     *
     * @return A short summary/title of the record.
     */
    @Override
    public abstract String toString();

    /**
     * Returns the contents of the record, or exports them to a log, depending
     * on the record type.
     *
     * @return The contents of the record, OR the empty string, depending on the
     * record type.
     */
    public abstract String print();

    abstract Type[] getTypes();

    Record getNew() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

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

    static Type getNextType(LStream in) throws BadRecord, IOException {
	return (matchType(Ln.arrayToString(in.getInts(0, 4))));
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    out.write(getTypes()[0].toString());
	    out.write(getContentLength(srcMod));
	}
    }

    private int extractRecordLength(LStream in) throws IOException {
	return Ln.arrayToInt(in.getInts(getIdentifierLength(), getSizeLength()))
		+ getSizeLength() + getIdentifierLength() + getFluffLength();
    }

    private int extractRecordLength(LFileChannel in) throws IOException {
	return Ln.arrayToInt(in.getInts(getIdentifierLength(), getSizeLength()))
		+ getSizeLength() + getIdentifierLength() + getFluffLength();
    }

    LShrinkArray extractRecordData(LStream in) throws IOException {
	int recordLength = extractRecordLength(in);
	LShrinkArray extracted = new LShrinkArray(in, recordLength);
	in.skip(recordLength);
	return extracted;
    }

    LFileChannel extractRecordFile(LFileChannel in) throws IOException {
	int recordLength = extractRecordLength(in);
	LFileChannel extracted = new LFileChannel(in, recordLength);
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
	return getContentLength(srcMod) + getHeaderLength();
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
