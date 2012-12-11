/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Form List Record
 * @author AliTheLord
 */
public class FLST extends MajorRecord {

    static final SubPrototype FLSTproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubList<>(new SubForm(Type.LNAM)));
	}
    };
    private final static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.FLST}));

    FLST() {
	super();
	subRecords.setPrototype(FLSTproto);
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
    ArrayList<Type> getTypes() {
	return type;
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
	return SubList.subFormToPublic(subRecords.getSubList(Type.LNAM));
    }

    /**
     *
     * @param entry FormID to add to the list.
     */
    public void addFormEntry(FormID entry) {
	subRecords.getSubList(Type.LNAM).add(new SubForm(Type.LNAM, entry));
    }

    /**
     *
     * @param entry FormID to remove (if it exists).
     */
    public void removeFormEntry(FormID entry) {
	subRecords.getSubList(Type.LNAM).remove(new SubForm(Type.LNAM, entry));
    }

    /**
     *
     * @return
     */
    public int getSize() {
	return subRecords.getSubList(Type.LNAM).size();
    }

    /**
     *
     * @param entry
     * @param i
     */
    public void addFormEntryAtIndex(FormID entry, int i) {
	subRecords.getSubList(Type.LNAM).addAtIndex(new SubForm(Type.LNAM, entry), i);
    }
}
