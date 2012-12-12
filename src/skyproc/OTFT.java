/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Justin Swanson
 */
public class OTFT extends MajorRecord {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.OTFT}));
    static final SubPrototype OTFTproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubFormArray(Type.INAM, 0));
	}
    };

    // Common Functions
    OTFT() {
	super();
	subRecords.setPrototype(OTFTproto);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param uniqueEDID
     */
    public OTFT(Mod modToOriginateFrom, String uniqueEDID) {
	this();
	originateFrom(modToOriginateFrom, uniqueEDID);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new OTFT();
    }

    // Get/Set
    /**
     *
     * @return
     */
    public ArrayList<FormID> getInventoryList() {
	return subRecords.get(Type.INAM).allFormIDs();
    }

    /**
     *
     * @param item
     */
    public void addInventoryItem(FormID item) {
	subRecords.getSubFormArray(Type.INAM).add(item);
    }

    /**
     *
     * @param item
     */
    public void removeInventoryItem (FormID item) {
	subRecords.getSubFormArray(Type.INAM).remove(item);
    }

    /**
     *
     */
    public void clearInventoryItems() {
	subRecords.getSubFormArray(Type.INAM).clear();
    }
}
