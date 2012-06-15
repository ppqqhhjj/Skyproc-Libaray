/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Plutoman101
 */
public class ECZN extends MajorRecord {

    private static final Type[] type = {Type.ECZN};
    DATA DATA = new DATA();

    /**
     * Creates a new ECZN record.
     */
    ECZN() {
        super();
        subRecords.add(DATA);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new ECZN();
    }

    static class DATA extends SubRecord implements Serializable {

        private FormID owner = new FormID();
        private FormID location = new FormID();
        private int rank = 0;
        private int minLevel = 0;
        LFlags flags = new LFlags(1);
        private int maxLevel = 0;
        private boolean valid = true;

        DATA() {
            super(Type.DATA);
            valid = false;
        }

        DATA(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new DATA();
        }

        @Override
        final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);

            owner.setInternal(in.extract(4));
            location.setInternal(in.extract(4));
            rank = in.extractInt(1);
            minLevel = in.extractInt(1);
            flags.set(in.extract(1));
            maxLevel = in.extractInt(1);

            if (logging()) {
                logSync("", "DATA record: ");
                logSync("", "  " + "Owner: " + owner.getFormStr() + ", Location: " + location.getFormStr());
                logSync("", "  " + "Required Rank: " + rank + ", Minimum Level: " + minLevel);
                logSync("", "  " + "Max Level: " + maxLevel + ", Flags: " + flags);
            }

            valid = true;
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                owner.export(out);
                location.export(out);
                out.write(rank, 1);
                out.write(minLevel, 1);
		flags.export();
                out.write(maxLevel, 1);
            }
        }

        @Override
        public void clear() {
        }

        @Override
        Boolean isValid() {
            return valid;
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
        ArrayList<FormID> allFormIDs(boolean deep) {
            if (deep) {
                ArrayList<FormID> out = new ArrayList<FormID>(2);
                out.add(owner);
                out.add(location);
                return out;
            } else {
                return new ArrayList<FormID>(0);
            }
        }
    }

    public enum ECZNFlags {

        NeverResets(1),
        MatchPCBelowMin(2),
        DisableCombatBoundary(4),;
        int value;

        ECZNFlags(int value) {
            this.value = value;
        }
    }

    public boolean get(ECZNFlags flag) {
        return DATA.flags.get(flag.value);
    }

    public void set(ECZNFlags flag, boolean on) {
        this.DATA.flags.set(flag.value, on);
    }

    public FormID getLocation() {
        return DATA.location;
    }

    public void setLocation(FormID location) {
        this.DATA.location = location;
    }

    public int getMaxLevel() {
        return DATA.maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.DATA.maxLevel = maxLevel;
    }

    public int getMinLevel() {
        return DATA.minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.DATA.minLevel = minLevel;
    }

    public FormID getOwner() {
        return DATA.owner;
    }

    public void setOwner(FormID owner) {
        this.DATA.owner = owner;
    }

    public int getRank() {
        return DATA.rank;
    }

    public void setRank(int rank) {
        this.DATA.rank = rank;
    }
}
