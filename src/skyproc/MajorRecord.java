package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LFlags;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record contained in a GRUP. These are top level records that all have
 * FormIDs that uniquely identify them.
 *
 * @author Justin Swanson
 */
public abstract class MajorRecord extends Record implements Serializable {

    static final SubPrototype majorProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(SubString.getNew("EDID", true));
	}
    };
    SubRecords subRecords = new SubRecordsStream(majorProto);
    private FormID ID = new FormID();
    LFlags majorFlags = new LFlags(4);
    byte[] revision = new byte[4];
    int formVersion = 0x28;
    byte[] version = new byte[2];
    Mod srcMod;

    MajorRecord() {
    }

    void originateFromPatch(String edid) {
	srcMod = SPGlobal.getGlobalPatch();
	subRecords.setMajor(this);
	setEDID(edid);
	srcMod.addRecord(this);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final MajorRecord other = (MajorRecord) obj;
	if (!other.getType().equals(getType())) {
	    return false;
	}
	if (!ID.equals(other.ID)) {
	    return false;
	}
	return true;
    }

    /**
     * NOTE:  Not tested thoroughly.
     * Generic function that will return true if two major records have the same
     * subrecord content.
     * @param other
     * @return
     */
    public boolean deepEquals(MajorRecord other) {
	if (!other.getType().equals(getType())) {
	    return false;
	}
	if (!subRecords.equals(other.subRecords)) {
	    return false;
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 3;
	hash = 37 * hash + Objects.hashCode(this.ID);
	return hash;
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    @Override
    public String toString() {
	String out = "[" + getType().toString() + " | ";
	if (ID.isStandardized()) {
	    out += getFormStr();
	} else if (isValid()) {
	    out += getFormArrayStr(true);
	}
	SubString EDID = subRecords.getSubString("EDID");
	if (EDID.isValid()) {
	    out += " | " + EDID.print();
	}
	return out + "]";
    }

    MajorRecord copyOf(Mod modToOriginateFrom) {
	return copyOf(modToOriginateFrom, this.getEDID() + "_DUP");
    }

    MajorRecord copyOf(Mod modToOriginateFrom, String edid) {
	MajorRecord out = (MajorRecord) this.getNew();
	out.formVersion = this.formVersion;
	out.version = Arrays.copyOf(this.version, this.version.length);
	out.srcMod = modToOriginateFrom;
	out.ID = new FormID();
	out.majorFlags = new LFlags(majorFlags);
	System.arraycopy(revision, 0, out.revision, 0, revision.length);
	System.arraycopy(version, 0, out.version, 0, version.length);
	out.subRecords = new SubRecordsCopied(subRecords);
	out.setEDID(edid);
	return out;
    }

    public MajorRecord copy(String edid) {
	return SPGlobal.getGlobalPatch().makeCopy(this, edid);
    }

    @Override
    boolean isValid() {
	if (ID == null) {
	    return false;
	} else {
	    return ID.isValid();
	}
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);

	majorFlags = new LFlags(in.extract(4));
	ID.parseData(in, srcMod);
	revision = in.extract(4);
	formVersion = in.extractInt(2);
	version = in.extract(2);

	if (get(MajorFlags.Compressed)) {
	    set(MajorFlags.Compressed, false);
	    in = in.correctForCompression();
	    logMod(srcMod, getTypes().toString(), "Decompressed");
	}

	if (!in.isDone() && "EDID".equals(getNextType(in))) {
	    SubString EDID = subRecords.getSubString("EDID");
	    EDID.parseData(EDID.extractRecordData(in), srcMod);
	}

	if (getEDID().equals("SprigganMatronRace")) {
	    int wer = 23;
	}
	
	importSubRecords(in);
    }

    void importSubRecords(LImport in) throws BadRecord, DataFormatException, BadParameter {
	subRecords.importSubRecords(in, srcMod);
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
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
	logMod(srcMod, getTypes().toString(), "Form ID: " + getFormStr() + ", EDID: " + getEDID());
	return "";
    }

    @Override
    int getFluffLength() {
	return 16;
    }

    @Override
    int getContentLength(ModExporter out) {
	return subRecords.length(out);
    }

    @Override
    int getTotalLength(ModExporter out) {
	int len = super.getTotalLength(out);
	if (shouldExportGRUP()) {
	    len += getGRUPAppend().getTotalLength(out);
	}
	return len;
    }

    @Override
    int getSizeLength() {
	return 4;
    }

    @Override
    void export(ModExporter out) throws IOException {
	out.setSourceMod(srcMod);
	out.setSourceMajor(this);
	super.export(out);
	if (isValid()) {
	    out.write(majorFlags.export(), 4);
	    ID.export(out);
	    out.write(revision, 4);
	    out.write(formVersion, 2);
	    out.write(version, 2);

	    subRecords.export(out);
	    if (SPGlobal.deleteAfterExport) {
		// Save EDID for record validation tests
		SubRecord edid = subRecords.get("EDID");
		subRecords = new SubRecordsDerived(subRecords.getPrototype());
		subRecords.add(edid);
	    }

	    if (shouldExportGRUP()) {
		getGRUPAppend().export(out);
	    }
	}
    }

    // Get/set methods
    /**
     * Sets the EDID of the Major Record<br><br> ONLY works on new records
     * you've created originating from the global patch. <br>
     *
     * NOTE: This will reassign the records formID if the new EDID matches an
     * EDID from the previous patch.
     *
     * @param edid The string to have the EDID set to.
     */
    final public void setEDID(String edid) {
	if (SPGlobal.getGlobalPatch().equals(srcMod)) {
	    edid = Consistency.edidFilter(edid);
	    Consistency.syncIDwithEDID(edid, this);
	    subRecords.getSubString("EDID").setString(edid);
	}
    }

    /**
     *
     * @return The current EDID string.
     */
    final public String getEDID() {
	return subRecords.getSubString("EDID").print();
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

    /**
     *
     */
    public enum MajorFlags {

	/**
	 *
	 */
	Deleted(5),
	/**
	 *
	 */
	RelatedToShields(6),
	/**
	 *
	 */
	HiddenFromLocalMap(9),
	/**
	 *
	 */
	QuestItemPersistentRef(10),
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

    GRUP getGRUPAppend() {
	return null;
    }

    boolean shouldExportGRUP() {
	return false;
    }
}
