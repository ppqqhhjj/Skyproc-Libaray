package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import lev.*;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Abstract class outlining functions for a generic record.
 *
 * @author Justin Swanson
 */
public abstract class Record implements Serializable {

    final static HashMap<String, ArrayList<String>> typeLists = new HashMap<>();

    Record() {
    }

    void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	in.skip(getIdentifierLength() + getSizeLength());
    }

    final void parseData(ByteBuffer in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	parseData(new LShrinkArray(in), srcMod);
    }

    boolean isValid() {
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

    abstract ArrayList<String> getTypes();

    static ArrayList<String> getTypeList(String t) {
	ArrayList<String> out = typeLists.get(t);
	if (out == null) {
	    out = new ArrayList<>(Arrays.asList(new String[]{t}));
	    typeLists.put(t, out);
	}
	return out;
    }

    /**
     * 
     * @return The type string associated with record.
     */
    public String getType() {
	return getTypes().get(0);
    }

    Record getNew() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    static String getNextType(LImport in) throws BadRecord {
	return (Ln.arrayToString(in.getInts(0, 4)));
    }

    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    out.write(getType().toString());
	    out.write(getContentLength(out));
	}
    }

    /**
     *
     * @param in
     * @return
     */
    public int getRecordLength(LImport in) {
	return Ln.arrayToInt(in.getInts(getIdentifierLength(), getSizeLength()))
		+ getSizeLength() + getIdentifierLength() + getFluffLength();
    }

    LImport extractRecordData(LImport in) {
	return extractData(in, getRecordLength(in));
    }

    LImport extractData(LImport in, int size) {
	LImport extracted;
	if (SPGlobal.streamMode && (in instanceof RecordShrinkArray || in instanceof LInChannel)) {
	    extracted = new RecordShrinkArray(in, size);
	} else {
	    extracted = new LShrinkArray(in, size);
	}
	in.skip(size);
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

    int getTotalLength(ModExporter out) {
	return getContentLength(out) + getHeaderLength();
    }

    abstract int getContentLength(ModExporter out);

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
	SPGlobal.logSync(getType().toString(), log);
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

    static void logMod(Mod srcMod, String header, String ... data) {
	SPGlobal.logMod(srcMod, header, data);
    }

    void newLog(String fileName) {
	SPGlobal.newLog(fileName);
    }

    void flush() {
	SPGlobal.flush();
    }
}
