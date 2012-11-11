/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class OTFT extends MajorRecord {

    static final SubRecordsPrototype OTFTproto = new SubRecordsPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubFormArray(Type.INAM, 0));
	}
    };
    static Type[] types = { Type.OTFT };

    OTFT() {
	super();
	subRecords.prototype = OTFTproto;
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
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new OTFT();
    }

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
