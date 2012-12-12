/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Actor value records and perk trees.
 *
 * @author Justin Swanson
 */
public class AVIF extends MajorRecordDescription {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.AVIF}));
    static final SubPrototype perkRefProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubForm(Type.PNAM));
	    forceExport(Type.PNAM);
	    add(new SubInt(Type.FNAM));
	    add(new SubInt(Type.XNAM));
	    add(new SubInt(Type.YNAM));
	    add(new SubFloat(Type.HNAM));
	    add(new SubFloat(Type.VNAM));
	    add(new SubForm(Type.SNAM));
	    add(new SubList<>(new SubInt(Type.CNAM)));
	    add(new SubInt(Type.INAM));
	}
    };
    static final SubPrototype AVIFproto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new SubString(Type.ANAM, true));
	    add(new SubData(Type.CNAM));
	    add(new SubData(Type.AVSK));
	    add(new SubList<>(new PerkReference()));
	}
    };
    /**
     * A structure that represents a perk in a perktree
     */
    public static final class PerkReference extends SubShellBulkType {

	PerkReference() {
	    super(perkRefProto, false);
	}

	@Override
	Boolean isValid() {
	    return true;
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

	/**
	 *
	 * @param id
	 */
	public void setPerk(FormID id) {
	    subRecords.setSubForm(Type.PNAM, id);
	}

	/**
	 *
	 * @return
	 */
	public FormID getPerk() {
	    return subRecords.getSubForm(Type.PNAM).getForm();
	}

	/**
	 *
	 * @param x
	 */
	public void setX(int x) {
	    subRecords.setSubInt(Type.XNAM, x);
	}

	/**
	 *
	 * @return
	 */
	public int getX() {
	    return subRecords.getSubInt(Type.XNAM).get();
	}

	/**
	 *
	 * @param y
	 */
	public void setY(int y) {
	    subRecords.setSubInt(Type.YNAM, y);
	}

	/**
	 *
	 * @return
	 */
	public int getY() {
	    return subRecords.getSubInt(Type.YNAM).get();
	}

	/**
	 *
	 * @param horiz
	 */
	public void setHorizontalPos(float horiz) {
	    subRecords.setSubFloat(Type.HNAM, horiz);
	}

	/**
	 *
	 * @return
	 */
	public float getHorizontalPos() {
	    return subRecords.getSubFloat(Type.HNAM).get();
	}

	/**
	 *
	 * @param vert
	 */
	public void setVerticalPos(float vert) {
	    subRecords.setSubFloat(Type.VNAM, vert);
	}

	/**
	 *
	 * @return
	 */
	public float getVerticalPos() {
	    return subRecords.getSubFloat(Type.VNAM).get();
	}

	/**
	 *
	 * @param skill
	 */
	public void setSkill(FormID skill) {
	    subRecords.setSubForm(Type.SNAM, skill);
	}

	/**
	 *
	 * @return
	 */
	public FormID getSkill() {
	    return subRecords.getSubForm(Type.SNAM).getForm();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Integer> getPointers() {
	    return SubList.subIntToPublic(subRecords.getSubList(Type.CNAM));
	}

	/**
	 *
	 */
	public void clearPointers() {
	    subRecords.getSubList(Type.CNAM).clear();
	}

	/**
	 *
	 * @param index
	 */
	public void addPointer(int index) {
	    SubInt cnam = new SubInt(Type.CNAM);
	    cnam.set(index);
	    subRecords.getSubList(Type.CNAM).add(cnam);
	}

	/**
	 *
	 * @param ref
	 */
	public void addPointer(PerkReference ref) {
	    addPointer(ref.getIndex());
	}

	/**
	 *
	 * @param index
	 */
	public void setIndex(int index) {
	    subRecords.setSubInt(Type.INAM, index);
	}

	/**
	 *
	 * @return
	 */
	public int getIndex() {
	    return subRecords.getSubInt(Type.INAM).get();
	}
    }

    // Common Functions
    AVIF() {
	super();
	subRecords.setPrototype(AVIFproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new AVIF();
    }

    // Get/Set
    /**
     *
     * @param abbr
     */
    public void setAbbreviation(String abbr) {
	subRecords.setSubString(Type.ANAM, abbr);
    }

    /**
     *
     * @return
     */
    public String getAbbreviation() {
	return subRecords.getSubString(Type.ANAM).print();
    }

    /**
     *
     * @return
     */
    public ArrayList<PerkReference> getPerkReferences() {
	SubList out = subRecords.getSubList(Type.PNAM);
	return out.collection;
    }
}
