package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import lev.Ln;
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
    byte[] version = {0x28, 0, 0, 0};
    Mod srcMod;

    MajorRecord() {
    }

    void originateFrom(Mod modToOriginateFrom, String edid) {
	srcMod = modToOriginateFrom;
	subRecords.setMajor(this);
	setEdidNoConsistency(edid);
	ID = modToOriginateFrom.getNextID(getEDID());
	Consistency.addEntry(getEDID(), ID);
	Consistency.newIDs.add(ID);
	modToOriginateFrom.addRecord(this);
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
	if (!Objects.equals(this.ID, other.ID)) {
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
	out.ID = new FormID(ID);
	out.majorFlags = new LFlags(majorFlags);
	System.arraycopy(revision, 0, out.revision, 0, revision.length);
	System.arraycopy(version, 0, out.version, 0, version.length);
	out.subRecords = new SubRecordsCopied(subRecords);
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
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);

	majorFlags = new LFlags(in.extract(4));
	setForm(in.extract(4));
	ID.standardize(srcMod);
	revision = in.extract(4);
	version = in.extract(4);

	if (get(MajorFlags.Compressed)) {
	    set(MajorFlags.Compressed, false);
	    in = in.correctForCompression();
	    logSync(getTypes().toString(), "Decompressed");
	}

	if ("EDID".equals(getNextType(in))) {
	    SubString EDID = subRecords.getSubString("EDID");
	    EDID.parseData(EDID.extractRecordData(in));
	    Consistency.addEntry(EDID.print(), ID);
	}

	if (getEDID().equals("BardAudienceQuest")) {
	    int wer = 23;
	}

	importSubRecords(in);
    }

    void importSubRecords(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	subRecords.importSubRecords(in);
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
	logSync(getTypes().toString(), "Form ID: " + getFormStr() + ", EDID: " + getEDID());
	return "";
    }

    @Override
    int getFluffLength() {
	return 16;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return subRecords.length(srcMod);
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

	    if (getEDID().equals("AV_n80326256_BellyachesAnimals_Deer_Skin_Normal_armo")) {
		int wer = 23;
	    }

	    subRecords.export(out, srcMod);
	}
    }

    void fetchStringPointers() throws IOException {
	subRecords.fetchStringPointers(srcMod);
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

    final void setEdidNoConsistency(String edid) {
	edid = Consistency.getAvailableEDID(edidFilter(edid));
	FormID oldFormID = Consistency.getOldForm(edid);
	if (oldFormID != null) {
	    setForm(oldFormID);
	}
	subRecords.getSubString("EDID").setString(edid);
    }

    static String edidFilter(String edid) {
	return edid.replaceAll(" ", "");
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

    void setForm(byte[] in) throws BadParameter {
	ID.setInternal(in);
	if (logging()) {
	    logSync(toString(), "Setting FormID: " + ID.getArrayStr(true));
	}
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
}
