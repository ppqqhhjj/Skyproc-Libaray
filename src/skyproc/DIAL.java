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

    static final INFO info = new INFO();
    static final SubPrototype DIALprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubStringPointer("FULL", SubStringPointer.Files.STRINGS));
	    add(new SubData("PNAM"));
	    add(new SubForm("BNAM"));
	    add(new SubForm("QNAM"));
	    add(new SubData("DATA"));
	    add(SubString.getNew("SNAM", false));
	    add(new SubInt("TIFC"));
	}
    };
    GRUP<INFO> grup = new GRUP<>(info);

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

    @Override
    public GRUP getGRUPAppend() {
	return grup;
    }
    
    @Override
    public boolean shouldExportGRUP() {
	return true;//!grup.isEmpty();// || version[2] != 1;
    }
}
