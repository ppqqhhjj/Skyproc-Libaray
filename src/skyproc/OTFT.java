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

    static Type[] types = { Type.OTFT };

    SubFormArray INAM = new SubFormArray(Type.INAM, 0);

    OTFT() {
	super();
	subRecords.add(INAM);
    }

    public OTFT(Mod modToOriginateFrom, String uniqueEDID) {
	super(modToOriginateFrom, uniqueEDID);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new OTFT();
    }

    public ArrayList<FormID> getInventoryList() {
	return new ArrayList<>(INAM.IDs);
    }

    public void addInventoryItem(FormID item) {
	INAM.add(item);
    }

    public void removeInventoryItem (FormID item) {
	INAM.remove(item);
    }

    public void clearInventoryItems() {
	INAM.clear();
    }
}
