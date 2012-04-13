package skyproc;

import skyproc.exceptions.BadParameter;

/**
 * Leveled List.  A list of entries used for spawnpoints when choosing which actor to
 * spawn.  Each entry contains a FormID of an actor, a level, and a count to spawn.
 * @author Justin Swanson
 */
public class LVLN extends LeveledRecord {

    private static final Type[] type = {Type.LVLN};
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    boolean circular = false;

    /**
     * Creates a Leveled List with no entries and default settings.
     * LVLN_Flags set to 0x01=All levels
     * Chance none set to 0.
     * Empty MODL, MODT, and COED.
     */
    LVLN() {
        super();
        init();
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod parameter.
     * @param modToOriginateFrom Mod to mark the LVLN as originating from.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLN(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
        init();
    }

    final void init() {
	subRecords.remove(Type.FULL);

        subRecords.add(OBND);
        subRecords.add(LVLD);
        subRecords.add(LVLF);
        subRecords.add(entries);
        subRecords.add(MODL);
        subRecords.add(MODT);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    /**
     * Prints out the contents of the LVLN to the asynchronous log.
     * @return The empty string.
     */
    @Override
    public String print() {
        super.print();
        logSync(getTypes().toString(), "Chance none: " + getChanceNone() + ", Flags: " + LVLF.print());
        for (LVLO entry : entries) {
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
        return MODL.print();
    }

    /**
     *
     * @param in String to set the LVLN model path to.
     */
    public void setModelPath(String in) {
        MODL.setString(in);
    }
}
