/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.SubStringPointer.Files;

/**
 *
 * @author Justin Swanson
 */
public abstract class MajorRecordDescription extends MajorRecordNamed {

    static final SubRecordsPrototype descProto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    SubStringPointer description = new SubStringPointer(Type.DESC, Files.DLSTRINGS);
	    description.forceExport = true;
	    forceExport(Type.DESC);
	    add(description);
	}
    };

    MajorRecordDescription() {
	super();
    }

    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if
     * empty.
     */
    public String getDescription() {
	return subRecords.getSubString(Type.DESC).print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    public void setDescription(String description) {
	subRecords.setSubString(Type.DESC, description);
    }
}
