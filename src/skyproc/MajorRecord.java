package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.*;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.exceptions.Uninitialized;

/**
 * A record contained in a GRUP. These are top level records that all have
 * FormIDs that uniquely identify them.
 *
 * @author Justin Swanson
 */
public abstract class MajorRecord extends Record implements Serializable {

    SubRecords subRecords = new SubRecords();
    private FormID ID = new FormID();
    LFlags majorFlags = new LFlags(4);
    byte[] revision = new byte[4];
    byte[] version = {0x28, 0, 0, 0};
    SubString EDID = new SubString(Type.EDID, true);
    Enum exception;

    MajorRecord() {
	subRecords.add(EDID);
    }

    MajorRecord(Mod modToOriginateFrom, String edid) {
	this();
	setEdidNoConsistency(edid);
	ID = modToOriginateFrom.getNextID(getEDID());
	Consistency.addEntry(getEDID(), ID);
	Consistency.newIDs.add(ID);
	modToOriginateFrom.addRecordSilent(this);
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    @Override
    public String toString() {
	String out = "[" + getTypes()[0].toString() + " | ";
	if (ID.isStandardized()) {
	    out += getFormStr();
	} else if (isValid()) {
	    out += getFormArrayStr(true);
	}
	if (EDID.isValid()) {
	    out += " | " + EDID.print();
	}
	return out + "]";
    }

    @Override
    MajorRecord copyOf(Mod modToOriginateFrom) {
	return copyOf(modToOriginateFrom, this.getEDID() + "_DUP");
    }

    MajorRecord copyOf(Mod modToOriginateFrom, String edid) {
	MajorRecord out = (MajorRecord) super.copyOf(modToOriginateFrom);
	out.setEdidNoConsistency(edid);
	out.setForm(modToOriginateFrom.getNextID(out.getEDID()));
	Consistency.addEntry(out.getEDID(), out.getForm());
	Consistency.newIDs.add(out.getForm());
	return out;
    }

    @Override
    Boolean isValid() {
	if (ID == null) {
	    return false;
	} else {
	    return ID.isValid();
	}
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, null);
    }

    void parseData(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	majorFlags = new LFlags(in.extract(4));
	setForm(in.extract(4));
	revision = in.extract(4);
	version = in.extract(4);

	if (get(MajorFlags.Compressed)) {
	    set(MajorFlags.Compressed, false);
	    in.correctForCompression();
	    logSync(getTypes().toString(), "Decompressed");
	}

	if (getNextType(in) == Type.EDID) {
	    EDID.parseData(EDID.extractRecordData(in));
	    Consistency.addEntry(EDID.print(), ID);
	}

//	    if (getEDID().equals("DA11Cannibalism")) {
//		int wer = 23;
//	    }


	importSubRecords(in, mask);
	subRecords.printSummary();
    }

    void importSubRecords(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
 	subRecords.importSubRecords(in, mask);
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<FormID>();
	out.add(ID);
	out.addAll(subRecords.allFormIDs());
	return out;
    }

    /**
     * Prints the contents of the Major Record to the asynchronous logs.
     *
     * @return The empty string.
     */
    @Override
    public String print() {
	logSync(getTypes().toString(), "Form ID: " + getFormStr() + ", EDID: " + EDID.print());
	if (hasException()) {
	    logSync(getTypes().toString(), "Exception: " + exception.toString());
	}
	return "";
    }

    @Override
    int getFluffLength() {
	return 16;
    }

    @Override
    int getContentLength(Mod srcMod) {
	if (isValid()) {
	    return subRecords.length(srcMod);
	} else {
	    return 0;
	}
    }

    @Override
    int getSizeLength() {
	return 4;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	if (isValid()) {
	    if (logging() && SPGlobal.debugExportSummary) {
		logSync(toString(), "Exporting: " + ID.getArrayStr(true) + ID.getMaster().print() + ", with total length: " + Ln.prettyPrintHex(getTotalLength(srcMod)));
	    }

	    out.write(majorFlags.export(), 4);
	    out.write(ID.getInternal(true), 4);
	    out.write(revision, 4);
	    out.write(version, 4);
	    subRecords.export(out, srcMod);
	}
    }

    void setException(Enum input) {
	exception = input;
    }

    Enum getException() {
	if (hasException()) {
	    return exception;
	} else {
	    return SPExceptionDbInterface.NullException.NULL;
	}
    }

    void fetchException(SPDatabase database) {
	try {
	    setException(database.getException(this));
	} catch (Uninitialized ex) {
	    exception = SPExceptionDbInterface.NullException.NULL;
	}
	if (logging() && !exception.equals(SPExceptionDbInterface.NullException.NULL)) {
	    logSync(toString(), "Exception fetched (", getException().toString() + ") for " + toString());
	}
    }

    Boolean hasException() {
	return exception != null;
    }

    void fetchStringPointers(Mod srcMod, Map<Files, LFileChannel> streams) throws IOException {
	subRecords.fetchStringPointers(srcMod, this, streams);
    }

    // Get/set methods
    /**
     * Sets the EDID of the Major Record<br><br>
     *
     * NOTE: This will reassign the records formID if the new EDID matches an
     * EDID from the previous patch.
     *
     * @param edid The string to have the EDID set to.
     */
    final public void setEDID(String edid) {
	setEdidNoConsistency(edid);
	Consistency.addEntry(edid, this.getForm());
    }

    final void setEdidNoConsistency (String edid) {
	edid = Consistency.getAvailableEDID(edidFilter(edid));
	FormID oldFormID = Consistency.getOldForm(edid);
	if (oldFormID != null) {
	    setForm(oldFormID);
	}
	EDID.setString(edid);
    }

    static String edidFilter (String edid) {
	return edid.replaceAll(" ", "");
    }

    /**
     *
     * @return The current EDID string.
     */
    final public String getEDID() {
	return EDID.print();
    }

    /**
     * Sets the FormID of the Major Record. Any changes to that FormID will be
     * reflected in the Major Record as well, it shares the same object.
     *
     * @param in The FormID to assign to this Major Record.
     */
    public void setForm(FormID in) {
	ID = in;
    }

    /**
     * Returns the FormID object of the Major Record. Note that any changes made
     * to this FormID will be reflected in the Major Record also.
     *
     * @return The FormID object of the Major Record.
     */
    public FormID getForm() {
	return ID;
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    public String getFormStr() {
	return ID.getFormStr();
    }

    String getFormArrayStr(Boolean master) {
	return ID.getArrayStr(master);
    }

    /**
     *
     * @return The name of the mod from which this Major Record originates.
     */
    public ModListing getFormMaster() {
	return ID.getMaster();
    }

    void setForm(byte[] in) throws BadParameter {
	ID.setInternal(in);
	if (logging()) {
	    logSync(toString(), "Setting FormID: " + ID.getArrayStr(true));
	}
    }

    /**
     * Returns a customized Mask object with the correct Types preloaded with
     * the flags set to false. This means it will initially import nothing. You
     * must tell the mask which Types to allow.
     *
     * @see Type
     * @return Major Record specific customized mask with flags set to false.
     */
    Mask getMask() {
	return new Mask();
    }

    /**
     * This function creates an mask for the major record given, or null if a
     * mask type is given that isn't a major record.<br><br>
     *
     * To use a mask:<br> 1) create one with this function<br> 2) Set the flags
     * for the desired subrecords to true.<br> 3) Add it to the SPImporter
     * object<br> 4) Import as usual
     *
     * @param maskType Type to create a mask for. (major record)
     * @return A mask reflecting the major record's type, with all subrecord
     * flags set to block.
     */
    public static Mask getMask(Type maskType) {
	Mod tempMod = new Mod(new ModListing("temp", false), true);
	GRUP g = tempMod.GRUPs.get(GRUP_TYPE.toRecord(maskType));
	if (g == null) {
	    return null;
	}
	MajorRecord m = (MajorRecord) g.prototype.getNew();
	return m.getMask();
    }

    /**
     * A mask that can be added to an SPImporter object to inform it of special
     * importing rules for specific GRUPs. The SPImporter stops importing a
     * Major Record when all of the subrecords allowed by a mask have been
     * imported. This saves time when only a few subrecords need to be imported
     * for use in your program.
     *
     * NOTE: If a mask is used for a specific GRUP, that GRUP should never be
     * exported with the patch, or else the records will be incomplete (missing
     * information). It should only be used for in-program purposes.
     */
    public class Mask {

	Map<Type, Boolean> allowed = new EnumMap<Type, Boolean>(Type.class);
	Type type;
	int numImport = 0;
	Map<Type, Boolean> imported = new EnumMap<Type, Boolean>(Type.class);
	int numImported = 0;

	/**
	 * Creates a mask with all related Type flags set to false.
	 */
	public Mask() {
	    this(false);
	}

	Mask(boolean b) {
	    type = getTypes()[0];
	    for (Type t : subRecords.getTypes()) {
		allowed.put(t, b);
		imported.put(t, false);
	    }
	}

	/**
	 *
	 * @see Type
	 * @param t Type to allow import.
	 */
	public void allow(Type t) {
	    if (allowed.get(t) == false) {
		allowed.put(t, true);
		numImport++;
	    }
	}

	void clear() {
	    for (Type t : imported.keySet()) {
		imported.put(t, false);
	    }
	    numImported = 0;
	}

	void imported(Type t) {
	    if (imported.get(t) == false) {
		imported.put(t, true);
		numImported++;
	    }
	}

	boolean done() {
	    if (numImported == numImport) {
		clear();
		SPGlobal.logSync("MASK", "Done importing record.");
		return true;
	    }
	    return false;
	}
    }

    static class Null_Major extends MajorRecord {

	private static final Type[] type = {Type.NULL};

	@Override
	public void fetchException(SPDatabase database) {
	}

	@Override
	Type[] getTypes() {
	    return type;
	}

	public static Null_Major getNull() {
	    return new Null_Major();
	}

	@Override
	Record getNew() {
	    return getNull();
	}
    }

    /**
     *
     */
    public enum MajorFlags {
	/**
	 *
	 */
	Deleted (5),
	/**
	 *
	 */
	RelatedToShields (6),
	/**
	 *
	 */
	HiddenFromLocalMap (9),
	/**
	 *
	 */
	QuestItemPersistentRef (10),
	/**
	 *
	 */
	InitiallyDisabled(11),
	/**
	 *
	 */
	Ignored(12),
	/**
	 *
	 */
	VisibleWhenDistant(15),
	/**
	 *
	 */
	DangerousOffLimits(17),
	/**
	 *
	 */
	Compressed(18),
	/**
	 *
	 */
	CantWait(19);

	int value;
	MajorFlags(int value) {
	    this.value = value;
	}
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(MajorFlags flag, Boolean on) {
	majorFlags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(MajorFlags flag) {
	return majorFlags.get(flag.value);
    }
}
