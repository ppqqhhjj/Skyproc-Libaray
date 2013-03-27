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
public class DIAL extends MajorRecord {

    static final SubPrototype DIALprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(SubString.getNew("FULL", true));
	    add(new SubData("PNAM"));
	    add(new SubForm("BNAM"));
	    add(new SubForm("QNAM"));
	    add(SubString.getNew("SNAM", false));
	    add(new SubData("DATA"));
	    add(new SubInt("TIFC"));
	}
    };

    DIAL() {
	super();
	subRecords.setPrototype(DIALprototype);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("DIAL");
    }

    @Override
    Record getNew() {
	return new DIAL();
    }
}
