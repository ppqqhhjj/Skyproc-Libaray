package skyproc;

import java.io.Serializable;

/**
 * The actor class is an abstract class representing objects that can be put into
 * a LVLO entry.
 * @author Justin Swanson
 */
public abstract class Actor extends MajorRecordNamed implements Serializable {

    Actor() {
        super();
    }

    Actor (Mod mod, String edid) {
        super(mod, edid);
    }

    int getTrueLevel(int depth) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Boolean isOffset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
