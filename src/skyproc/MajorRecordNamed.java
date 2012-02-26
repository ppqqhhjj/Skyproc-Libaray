package skyproc;

import java.io.Serializable;
import skyproc.SubStringPointer.Files;

/**
 * An extended major record that has a name. (FULL record)
 * @author Justin Swanson
 */
public abstract class MajorRecordNamed extends MajorRecord implements Serializable {

    SubStringPointer FULL = new SubStringPointer(Type.FULL, Files.STRINGS);

    MajorRecordNamed() {
        super();
        subRecords.add(FULL);
    }

    MajorRecordNamed (Mod mod) {
        super(mod);
        subRecords.add(FULL);
    }

    /**
     * Returns the in-game name of the Major Record.
     * @return
     */
    public String getName() {
        return FULL.print();
    }

    /**
     * Sets the in-game name of the Major Record.
     * @param in The string to set the in-game name to.
     */
    public void setName(String in) {
        FULL.text.setString(in);
    }

}
