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

    SubStringPointer description = new SubStringPointer(Type.DESC, Files.DLSTRINGS);

    MajorRecordDescription() {
        super();
        description.forceExport = true;
        subRecords.add(description);
    }

    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if empty.
     */
    public String getDescription () {
        return description.print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    public void setDescription (String description) {
        this.description.setText(description);
    }

}
