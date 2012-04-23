/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class AVIF extends MajorRecordDescription {

    SubString ANAM = new SubString(Type.ANAM, true);
    SubData CNAM = new SubData(Type.CNAM);
    SubData AVSK = new SubData(Type.AVSK);
    SubList<PerkReference> perks = new SubList<PerkReference>(new PerkReference());

    static Type[] types = {Type.AVIF};

    AVIF() {
	super();

	subRecords.add(ANAM);
	subRecords.add(CNAM);
	subRecords.add(AVSK);
	subRecords.add(perks);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new AVIF();
    }

    @Override
    void importSubRecords(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
	Type nextType;
	Boolean pastHeader = false;
	while (!in.isEmpty()) {
	    nextType = getNextType(in);
	    if (nextType == Type.PNAM) {
		pastHeader = true;
	    }
	    if (subRecords.contains(nextType)) {
		switch (getNextType(in)) {
		    case CNAM:
			if (pastHeader) {
			    perks.parseData(perks.extractRecordData(in));
			    break;
			}
		    default:
			subRecords.importSubRecord(in, mask);
		}
	    } else {
		throw new BadRecord(getTypes()[0].toString() + " doesn't know what to do with a " + nextType.toString() + " record.");
	    }
	}
    }

    public static class PerkReference extends SubShell {

	SubForm PNAM = new SubForm(Type.PNAM);
	SubInt FNAM = new SubInt(Type.FNAM);
	SubInt XNAM = new SubInt(Type.XNAM);
	SubInt YNAM = new SubInt(Type.YNAM);
	SubFloat HNAM = new SubFloat(Type.HNAM);
	SubFloat VNAM = new SubFloat(Type.VNAM);
	SubForm SNAM = new SubForm(Type.SNAM);
	SubList<SubInt> CNAMs = new SubList<SubInt>(new SubInt(Type.CNAM));
	SubInt INAM = new SubInt(Type.INAM);
	static Type[] types = {Type.PNAM, Type.FNAM, Type.XNAM, Type.YNAM,
	    Type.HNAM, Type.VNAM, Type.SNAM, Type.INAM};

	PerkReference() {
	    super(types);
	    init();
	}

	final void init () {
	    subRecords.add(PNAM);
	    PNAM.ID.valid = true;
	    subRecords.add(FNAM);
	    subRecords.add(XNAM);
	    subRecords.add(YNAM);
	    subRecords.add(HNAM);
	    subRecords.add(VNAM);
	    subRecords.add(SNAM);
	    subRecords.add(CNAMs);
	    subRecords.add(INAM);
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    PNAM.ID.valid = true;
	}

	@Override
	SubRecord getNew(Type type) {
	    if (SPGlobal.logging()) {
		log("AVIF", "--- New Perk Reference Package: ---");
	    }
	    return new PerkReference();
	}

	@Override
	Record getNew() {
	    return new PerkReference();
	}

	public void setPerk(FormID id) {
	    PNAM.setForm(id);
	}

	public FormID getPerk() {
	    return PNAM.getForm();
	}

	public void setX(int x) {
	    XNAM.data = x;
	}

	public int getX() {
	    return XNAM.data;
	}

	public void setY(int y) {
	    YNAM.data = y;
	}

	public int getY() {
	    return YNAM.data;
	}

	public void setHorizontalPos(float horiz) {
	    HNAM.data = horiz;
	}

	public float getHorizontalPos () {
	    return HNAM.data;
	}

	public void setVerticalPos (float vert) {
	    VNAM.data = vert;
	}

	public float getVerticalPos () {
	    return VNAM.data;
	}

	public void setSkill (FormID skill) {
	    SNAM.setForm(skill);
	}

	public FormID getSkill () {
	    return SNAM.getForm();
	}

	public ArrayList<Integer> getPointers () {
	    return SubList.subIntToPublic(CNAMs);
	}

	public void clearPointers() {
	    CNAMs.clear();
	}

	public void addPointer (int index) {
	    SubInt cnam = new SubInt(Type.CNAM);
	    cnam.data = index;
	    CNAMs.add(cnam);
	}

	public void addPointer (PerkReference ref) {
	    addPointer(ref.INAM.data);
	}

	public void setIndex (int index) {
	    INAM.data = index;
	}

	public int getIndex () {
	    return INAM.data;
	}
    }

    public void setAbbreviation(String abbr) {
	ANAM.setString(abbr);
    }

    public String getAbbreviation () {
	return ANAM.print();
    }

    public ArrayList<PerkReference> getPerkReferences () {
	return perks.collection;
    }
}
