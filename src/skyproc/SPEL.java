/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.LFlags;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.exceptions.NotFound;

/**
 *
 * @author Plutoman101
 */
public class SPEL extends MajorRecordDescription {

    private static final Type[] type = {Type.SPEL};
    private SubData OBND = new SubData(Type.OBND);
    private SubForm MDOB = new SubForm(Type.MDOB);
    private SubForm ETYP = new SubForm(Type.ETYP);
    private SPIT SPIT = new SPIT();
    private SubList<EFIDPackage> spellSections = new SubList<EFIDPackage>(new EFIDPackage());

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new SPEL();
    }

    SPEL() {
	super();
	subRecords.remove(Type.FULL);
        subRecords.remove(Type.DESC);

	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(MDOB);
	subRecords.add(ETYP);
	subRecords.add(DESC);
	subRecords.add(SPIT);
	subRecords.add(spellSections);
    }

    @Override
    void standardizeMasters(Mod srcMod) {
	super.standardizeMasters(srcMod);
	spellSections.standardizeMasters(srcMod);
    }

    static class EFIDPackage extends SubRecord {

	private static Type[] types = {Type.EFID, Type.EFIT, Type.CTDA, Type.CIS1, Type.CIS2};
	SubForm EFID = new SubForm(Type.EFID);
	SubData EFIT = new SubData(Type.EFIT);
	SubList<SPEL.CTDApackage> CTDAs = new SubList<SPEL.CTDApackage>(new SPEL.CTDApackage());

	EFIDPackage() {
	    super(types);
	}

	EFIDPackage(LShrinkArray in) throws DataFormatException, BadParameter, BadRecord {
	    this();
	    parseData(in);
	}

	@Override
	void export(LExportParser out, Mod srcMod) throws IOException {
	    EFID.export(out, srcMod);
	    EFIT.export(out, srcMod);
	    CTDAs.export(out, srcMod);
	}

	@Override
	void parseData(LShrinkArray in) throws DataFormatException, BadParameter, BadRecord {
	    switch (getNextType(in)) {
		case EFID:
		    EFID.parseData(in);
		    break;
		case EFIT:
		    EFIT.parseData(in);
		    break;
		default:
		    CTDAs.parseData(in);
		    break;
	    }
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new SPEL.EFIDPackage();
	}

	@Override
	public void clear() {
	    EFID.clear();
	    EFIT.clear();
	    CTDAs.clear();
	}

	@Override
	Boolean isValid() {
	    return EFID.isValid() && EFIT.isValid();
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return EFID.getTotalLength(srcMod) + EFIT.getTotalLength(srcMod)
		    + CTDAs.getTotalLength(srcMod);
	}
    }

    static class CTDApackage extends SubRecord {

	private static Type[] types = {Type.CTDA, Type.CIS1, Type.CIS2};
	SubData CTDA = new SubData(Type.CTDA);
	SubString CIS1 = new SubString(Type.CIS1, true);
	SubString CIS2 = new SubString(Type.CIS2, true);

	CTDApackage() {
	    super(types);
	}

	@Override
	void parseData(LShrinkArray in) throws DataFormatException, BadParameter, BadRecord {
	    switch (getNextType(in)) {
		case CTDA:
		    CTDA.parseData(in);
		    break;
		case CIS1:
		    CIS1.parseData(in);
		    break;
		case CIS2:
		    CIS2.parseData(in);
		    break;
	    }
	}

	@Override
	void export(LExportParser out, Mod srcMod) throws IOException {
	    CTDA.export(out, srcMod);
	    CIS1.export(out, srcMod);
	    CIS2.export(out, srcMod);
	}

	@Override
	int getSizeLength() {
	    return 0;
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new SPEL.CTDApackage();
	}

	@Override
	public void clear() {
	    CTDA.clear();
	    CIS1.clear();
	    CIS2.clear();
	}

	@Override
	Boolean isValid() {
	    return CTDA.isValid();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return CTDA.getTotalLength(srcMod)
		    + CIS1.getTotalLength(srcMod)
		    + CIS2.getTotalLength(srcMod);
	}
    }

    static class SPIT extends SubRecord {

	private float baseCost = 0;
	private LFlags flags;
	private int baseType = 0;
	private float chargeTime = 0;
	private int castType = 0;
	private int targetType = 0;
	private byte[] fluff1;
	private float range = 0;
	private boolean valid = true;
	private SubForm perkType = new SubForm(Type.PERK);

	SPIT() {
	    super(Type.SPIT);
	    valid = false;
	}

	SPIT(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new SPIT();
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);

	    baseCost = in.extractFloat();
	    flags = new LFlags(in.extract(4));
	    baseType = in.extractInt(4);
	    chargeTime = in.extractFloat();
	    castType = in.extractInt(4);
	    targetType = in.extractInt(4);
	    fluff1 = in.extract(4);
	    range = in.extractFloat();
	    perkType.setForm(in.extract(4));

	    if (logging()) {
		logSync("", "SPIT record: ");
		logSync("", "  " + "Base Spell Cost: " + baseCost + ", flags: " + flags
			+", Base Type: " + baseType + ", Spell Charge Time: " + chargeTime);
		logSync("", "  " + "cast type: " + castType + ", targetType: " + targetType
			+ ", fluff: " + Ln.printHex(fluff1, true, false)
			+ ", Spell Range: " + range + ", Perk for Spell: " + perkType.print());
	    }

	    valid = true;
	}

	@Override
	void export(LExportParser out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    if (isValid()) {
		out.write(baseCost);
		out.write(flags.export(),4);
		out.write(baseType, 4);
		out.write(chargeTime);
		out.write(castType, 4);
		out.write(targetType, 4);
		out.write(fluff1, 4);
		out.write(range);
		out.write(perkType.getFormArray(true), 4);
	    }
	}

	@Override
	public void clear() {
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

        @Override
        void standardizeMasters(Mod srcMod) {
            super.standardizeMasters(srcMod);
            perkType.standardizeMasters(srcMod);
        }

	@Override
	int getContentLength(Mod srcMod) {
	    if (isValid()) {
		return 36;
	    } else {
		return 0;
	    }
	}
    }

    // Get Set functions
    /**
     *
     * @return The PERK ref associated with the SPEL.
     */
    public SubForm getPerkRef() {
        return SPIT.perkType;
    }

    /**
     *
     * @param perkRef FormID to set the SPELs PERK ref to.
     * @todo Add FormID confirmation
     * @throws NotFound This functionality to come.  Skyproc does NOT confirm
     * that the FormID associated truly points to a correct record.  You will have to
     * confirm the accuracy yourself for now.
     */
    public void setPerkRef (FormID perkRef) throws NotFound {
        SPIT.perkType.setForm(perkRef);
    }
}