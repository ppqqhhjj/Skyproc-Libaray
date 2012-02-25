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

    SubStringPointer DESC = new SubStringPointer(Type.DESC, Files.DLSTRINGS);

    MajorRecordDescription() {
        super();
        DESC.forceExport = true;
        subRecords.add(DESC);
    }

    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if empty.
     */
    public String getDescription () {
        return DESC.print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    public void setDescription (String description) {
        DESC.setText(description);
    }

}
