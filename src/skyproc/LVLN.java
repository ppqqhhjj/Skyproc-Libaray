package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Leveled List.  A list of entries used for spawnpoints when choosing which actor to
 * spawn.  Each entry contains a FormID of an actor, a level, and a count to spawn.
 * @author Justin Swanson
 */
public class LVLN extends LeveledRecord {

    static final SubPrototype LVLNproto = new SubPrototype(LeveledRecord.LeveledProto){

	@Override
	protected void addRecords() {
	    remove(Type.FULL);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	}
    };
    private final static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.LVLN}));
    boolean circular = false;

    /**
     * Creates a Leveled List with no entries and default settings.
     * LVLN_Flags set to 0x01=All levels
     * Chance none set to 0.
     * Empty MODL, MODT, and COED.
     */
    LVLN() {
        super();
	subRecords.setPrototype(LVLNproto);
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod parameter.
     * @param modToOriginateFrom Mod to mark the LVLN as originating from.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLN(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
	subRecords.setPrototype(LVLNproto);
    }

    @Override
    ArrayList<Type> getTypes() {
        return type;
    }

    /**
     * Prints out the contents of the LVLN to the asynchronous log.
     * @return The empty string.
     */
    @Override
    public String print() {
        super.print();
        logSync(getTypes().toString(), "Chance none: " + getChanceNone() + ", Flags: " + subRecords.getSubFlag(Type.LVLF).print());
        for (LeveledEntry entry : getEntries()) {
            entry.toString();
        }
        return "";
    }

    @Override
    Record getNew() {
        return new LVLN();
    }

    // Get/set

    /**
     *
     * @return Model path associated with the LVLN.
     */
    public String getModelPath() {
        return subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @param in String to set the LVLN model path to.
     */
    public void setModelPath(String in) {
        subRecords.setSubString(Type.MODL, in);
    }
}
