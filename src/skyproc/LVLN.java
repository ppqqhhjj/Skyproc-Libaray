package skyproc;

import java.io.IOException;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.LVLN.LVLO;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.exceptions.NotFound;

/**
 * Leveled List.  A list of entries used for spawnpoints when choosing which actor to
 * spawn.  Each entry contains a FormID of an actor, a level, and a count to spawn.
 * @author Justin Swanson
 */
public class LVLN extends Actor implements Iterable<LVLO> {

    private static final Type[] type = {Type.LVLN};
    SubList<LVLO> entries = new SubList<LVLO>(Type.LLCT, 1, new LVLO());
    SubData chanceNone = new SubData(Type.LVLD);
    SubFlag LVLNflags = new SubFlag(Type.LVLF, 1);
    SubData objectBounds = new SubData(Type.OBND);
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
     */
    public LVLN(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
        init();
    }

    final void init() {
        subRecords.add(objectBounds);
        subRecords.add(chanceNone);
        subRecords.add(LVLNflags);
        subRecords.add(entries);
        subRecords.add(MODL);
        subRecords.add(MODT);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    int getTrueLevel(int depth) {
        return -1;
    }

    /**
     * Prints out the contents of the LVLN to the asynchronous log.
     * @return The empty string.
     */
    @Override
    public String print() {
        super.print();
        logSync(getTypes().toString(), "Chance none: " + getChanceNone() + ", Flags: " + LVLNflags.print());
        for (LVLO entry : entries) {
            entry.toString();
        }
        return "";
    }

    /**
     * Wipes all entries from the LVLN.
     */
    public void clearEntries() {
        entries.clear();
    }

    void markCircular() {
        circular = true;
    }

    /**
     *
     * @return The EDID associated with the LVLN.
     */
    @Override
    public String getName() {
        return EDID.toString();
    }

    @Override
    Record getNew() {
        return new LVLN();
    }

    /**
     *
     * @return An iterator that steps through each entry in the LVLN.
     */
    @Override
    public Iterator<LVLO> iterator() {
        return entries.iterator();
    }

    // Internal Classes
    /**
     * An entry in a LVLN.
     */
    public static class LVLO extends SubForm {

        int level = 0;
        int count = 0;
        byte[] fluff1 = new byte[2];
        byte[] fluff2 = new byte[2];
        SubData COED = new SubData(Type.COED);
        private static final Type[] types = {Type.LVLO, Type.COED};

        LVLO(LShrinkArray in) throws BadRecord, BadParameter, DataFormatException {
            this();
            parseData(in);
        }

        LVLO() {
            super(types);
        }

        /**
         *
         * @param id  The formID of the actor to put on the entry.
         * @param level The level to mark the entry on the LVLN.
         * @param count The number of actors to spawn when this entry is picked.
         * @throws NotFound This functionality to come.  Skyproc does NOT confirm
         * that the FormID associated truly points to a correct record.  You will have to
         * confirm the accuracy yourself for now.
         */
        public LVLO(FormID id, int level, int count) {
            this();
            this.ID = id;
            this.count = count;
            this.level = level;
        }

        @Override
        final void parseData(LShrinkArray in) throws BadRecord, BadParameter, DataFormatException {
            switch (getNextType(in)) {
                case LVLO:
                    in.extractInts(6);
                    level = Ln.arrayToInt(in.extractInts(2));
                    fluff1 = in.extract(2);
                    setForm(in.extract(4));
                    count = Ln.arrayToInt(in.extractInts(2));
                    fluff2 = in.extract(2);
                    if (logging()) {
                        logSync(toString(), "   Entry level: " + level + ", count: " + count);
                    }
                    break;
                case COED:
                    COED.parseData(in);
                    break;
            }
        }

        /**
         *
         * @return The level this entry is marked on the LVLN.
         */
        public int getLevel() {
            return level;
        }

        /**
         *
         * @param in The level to mark the entry as on the LVLN.
         */
        public void setLevel(int in) {
            level = in;
        }

        /**
         *
         * @param in The number to set the spawn count to.
         */
        public void setCount(int in) {
            count = in;
        }

        /**
         *
         * @return The spawn counter.
         */
        public int getCount() {
            return count;
        }

        /**
         * Prints the entry to the asynchronous log.
         * @return The empty string.
         */
        @Override
        public String print() {
            logSync(getTypes().toString(), "   FormID: " + super.toString() + ", Level: " + level + ", Count: " + count);
            logSync(getTypes().toString(), "   -----------");
            return "";
        }

        @Override
        void export(LExportParser out, Mod srcMod) throws IOException {
            if (isValid()) {
                out.write(getTypes()[0].toString());
                out.write(getContentLength(srcMod), 2);
                out.write(getLevel(), 2);
                out.write(fluff1, 2);
                out.write(getFormArray(true), 4);
                out.write(count, 2);
                out.write(fluff2, 2);
                COED.export(out, srcMod);
            }
        }

        @Override
        int getContentLength(Mod srcMod) {
            if (isValid()) {
                return 12;
            } else {
                return 0;
            }
        }

        @Override
        int getTotalLength(Mod srcMod) {
            return super.getTotalLength(srcMod) + COED.getTotalLength(srcMod);
        }

        @Override
        SubRecord getNew(Type type_) {
            return new LVLO();
        }
    }

    /**
     * This enum represents the different flags offered by LVLN.
     */
    public enum LVLN_Flags {

        /**
         * Whether the LVLN should include only entries <= the players level.
         */
        ALL_LEVELS,
        /**
         * Whether to repeat the count calculation each LVLN call.
         */
        EACH,
        /**
         * Use all entries when LVLN is called.
         */
        USE_ALL;
    }

    // Get/set
    /**
     *
     * @return The percent chance nothing will spawn from this LVLN. (0.0-1.0)
     */
    public Double getChanceNonePct() {
        if (chanceNone.isValid()) {
            return chanceNone.toInt() / 100.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Sets the chance none for this LVLN.
     * @param in The chance that no creature will spawn from this LVLN.
     * @throws BadParameter If in is outside the range: (0-100)
     */
    public void setChanceNone(final int in) throws BadParameter {
        if (in >= 0 && in <= 100) {
            chanceNone.setData(in);
        } else {
            throw new BadParameter("Chance none set outside range 0-100: " + in);
        }
    }

    /**
     *
     * @return The chance that no actor will spawn from this LVLN.
     */
    public int getChanceNone() {
        return chanceNone.toInt();
    }

    /**
     * Adds the desired entry to the LVLN. Duplicates are accepted.
     * @param entry LVLO to add to this LVLN
     */
    public void addEntry(LVLO entry) {
        entries.add(entry);
    }

    /**
     * Adds an entry with the actor, level, and count provided.
     * @param id  The formID of the actor to put on the entry.
     * @param level Level to mark the entry at.
     * @param count Number of actors to spawn.
     * @throws NotFound This functionality to come.  Skyproc does NOT confirm
     * that the FormID associated truly points to a correct record.  You will have to
     * confirm the accuracy yourself for now.
     */
    public void addEntry(FormID id, int level, int count) throws NotFound {
        addEntry(new LVLO(id, level, count));
    }

    /**
     *
     * @return The number of entries contained in the LVLN.
     */
    public int numEntries() {
        return entries.size();
    }

    /**
     *
     * @return True if LVLN has no entries.
     */
    public Boolean isEmpty() {
        return numEntries() == 0;
    }

    /**
     *
     * @param i The zero based index to query.
     * @return The ith entry in the LVLN.
     */
    public LVLO getEntry(int i) {
        return entries.get(i);
    }

    /**
     * Removes the ith entry from the LVLN.
     * @param i The zero based index to remove.
     */
    public void removeEntry(int i) {
        entries.remove(i);
    }

    /**
     * Checks a flag of the LVLN given by flag parameter.
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to check.
     * @return True if given flag is true.
     */
    public boolean isFlag(LVLN_Flags flag) {
        return LVLNflags.is(flag.ordinal());
    }

    /**
     * Sets a flag of the LVLN given by flag parameter
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to set.
     * @param on Boolean to set flag to.
     */
    public void setFlag(LVLN_Flags flag, boolean on) {
        LVLNflags.set(flag.ordinal(), on);
    }

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
