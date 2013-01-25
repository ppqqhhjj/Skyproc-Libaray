/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Form List Record
 * @author AliTheLord
 */
public class FLST extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype FLSTproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubList<>(new SubForm("LNAM")));
	}
    };

    // Common Functions
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
    ArrayList<String> getTypes() {
	return Record.getTypeList("FLST");
    }

    @Override
    Record getNew() {
	return new FLST();
    }

    // Get/Set
    /**
     *
     * @return List of all the FormIDs in the Form list.
     */
    public ArrayList<FormID> getFormIDEntries() {
	return SubList.subFormToPublic(subRecords.getSubList("LNAM"));
    }

    /**
     *
     * @param entry FormID to add to the list.
     */
    public void addFormEntry(FormID entry) {
	subRecords.getSubList("LNAM").add(new SubForm("LNAM", entry));
    }

    public void addAll(Collection<FormID> entries) {
	subRecords.getSubList("LNAM").collection.addAll(entries);
    }

    /**
     *
     * @param entry FormID to remove (if it exists).
     */
    public void removeFormEntry(FormID entry) {
	subRecords.getSubList("LNAM").remove(new SubForm("LNAM", entry));
    }

    /**
     *
     * @return
     */
    public int getSize() {
	return subRecords.getSubList("LNAM").size();
    }

    /**
     *
     * @param entry
     * @param i
     */
    public void addFormEntryAtIndex(FormID entry, int i) {
	subRecords.getSubList("LNAM").addAtIndex(new SubForm("LNAM", entry), i);
    }
}
