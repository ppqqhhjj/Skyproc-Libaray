/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 * Form List Record
 * @author AliTheLord
 */
public class FLST extends MajorRecord {

    private static Type[] types = { Type.FLST };

    SubList<SubForm> LNAMs = new SubList<>(new SubForm(Type.LNAM));

    FLST() {
	super();
	subRecords.add(LNAMs);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param edid EDID to give the new record.  Make sure it is unique.
     */
    public FLST (Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
    }



    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new FLST();
    }

    /**
     *
     * @return List of all the FormIDs in the Form list.
     */
    public ArrayList<FormID> getFormIDEntries() {
	return SubList.subFormToPublic(LNAMs);
    }

    /**
     *
     * @param entry FormID to add to the list.
     */
    public void addFormEntry(FormID entry) {
	LNAMs.add(new SubForm(Type.LNAM, entry));
    }

    /**
     *
     * @param entry FormID to remove (if it exists).
     */
    public void removeFormEntry(FormID entry) {
	LNAMs.remove(new SubForm(Type.LNAM, entry));
    }

    /**
     *
     * @return
     */
    public int getSize() {
	return LNAMs.size();
    }

    /**
     *
     * @param entry
     * @param i
     */
    public void addFormEntryAtIndex(FormID entry, int i) {
	LNAMs.addAtIndex(new SubForm(Type.LNAM, entry), i);
    }
}
