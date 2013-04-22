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
public class DLVW extends MajorRecord {

    static final SubPrototype DLVWprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubForm("QNAM"));
	    add(new SubList<>(new SubForm("BNAM")));
	    add(new SubList<>(new SubForm("TNAM")));
	    add(new SubData("ENAM"));
	    add(new SubData("DNAM"));
	}
    };

    DLVW() {
	super();
	subRecords.setPrototype(DLVWprototype);
    }

    @Override
    Record getNew() {
	return new DLVW();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("DLVW");
    }

    /**
     *
     * @param quest
     */
    public void setQuest(FormID quest) {
	subRecords.setSubForm("QNAM", quest);
    }

    /**
     *
     * @return
     */
    public FormID getQuest() {
	return subRecords.getSubForm("QNAM").getForm();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getBranches () {
	return SubList.subFormToPublic(subRecords.getSubList("BNAM"));
    }

    /**
     *
     * @param dialogBranch
     */
    public void addBranch(FormID dialogBranch) {
	subRecords.getSubList("BNAM").add(new SubForm("BNAM", dialogBranch));
    }

    /**
     *
     * @param dialogBranch
     */
    public void removeBranch(FormID dialogBranch) {
	subRecords.getSubList("BNAM").remove(new SubForm("BNAM", dialogBranch));
    }

    /**
     *
     */
    public void clearBranches() {
	subRecords.getSubList("BNAM").clear();
    }
}
