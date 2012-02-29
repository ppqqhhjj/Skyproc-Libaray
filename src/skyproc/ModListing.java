package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A more accurate representation of a modname:
 * A combination of mod name without the suffix and its master flag.
 * Skyrim.esm, for example, would be "Skyrim" with a true master flag.
 * @author Justin Swanson
 */
public class ModListing extends SubRecord implements Comparable {

    static private Type[] types = {Type.MAST, Type.DATA};
    SubString mast = new SubString(Type.MAST, true);
    SubData data = new SubData(Type.DATA);
    long date = -1;
    boolean master = false;

    ModListing(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        this();
        parseData(in);
    }

    /**
     * ModListing objects are used to uniquely identify mods via name and master tag.
     * @param name The name to give to a mod.  Eg. "Skyrim"   (with no suffix)
     * @param master The master tag.  (.esp or .esm)
     */
    public ModListing(String name, Boolean master) {
        this(name);
        this.master = master;
    }

    /**
     *
     * @param nameWithSuffix String containing the modname AND suffix.  Eg "Skyrim.esm"
     */
    public ModListing(String nameWithSuffix) {
        this();
        setString(nameWithSuffix);
    }

    ModListing() {
        super(types);
        data.initialize(8);
        data.forceExport(true);
    }

    final void setString(String in) {
        String upper = in.toUpperCase();
        if (upper.contains(".ESM")) {
            setMasterTag(true);
            in = in.substring(0, upper.indexOf(".ES"));
        } else if (upper.contains(".ESP")) {
            setMasterTag(false);
            in = in.substring(0, upper.indexOf(".ES"));
        }
        mast.setString(in);
    }

    /**
     *
     * @param date set the date of a mod (to affect its load order)
     */
    public void setDate(Long date) {
        this.date = date;
    }

    /**
     * Prints the mod name and appropriate suffix (.esp or .esm)
     * @return
     */
    @Override
    public String print() {
        if (master) {
            return mast.print() + ".esm";
        } else {
            return mast.print() + ".esp";
        }
    }

    @Override
    void export(LExportParser out, Mod srcMod) throws IOException {
        String tmp = mast.string;
        mast.string = print();
        mast.export(out, srcMod);
        mast.string = tmp;
        data.export(out, srcMod);
    }

    @Override
    final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        switch (getNextType(in)) {
            case MAST:
                mast.parseData(in);
                setString(mast.string);
        }
    }

    @Override
    SubRecord getNew(Type type_) {
        return new ModListing();
    }

    @Override
    int getContentLength(Mod srcMod) {
        return mast.getContentLength(srcMod) + data.getTotalLength(srcMod) + 4;  // For .esp
    }

    /**
     * Sets to the empty string and false master tag.
     */
    @Override
    public void clear() {
        mast.clear();
        data.clear();
    }

    @Override
    Boolean isValid() {
        return mast.isValid();
    }

    void setMasterTag(Boolean in) {
        master = in;
    }

    boolean getMasterTag() {
        return master;
    }

    /**
     * Checks if the modname's are equal (case ignored), and the master tags are the same.
     * @param obj Another ModListing
     * @return True if modname's are equal (case ignored), and the master tags are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModListing other = (ModListing) obj;
        if (this.mast != other.mast && (this.mast == null || !this.mast.equalsIgnoreCase(other.mast))) {
            return false;
        }
        if (this.master != other.master) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return A hashcode with modname and master flag incorperated.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.mast != null ? this.mast.hashUpperCaseCode() : 0);
        hash = 37 * hash + (this.master ? 1 : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        ModListing rhs = (ModListing) o;
        if (equals(rhs)) {
            return 0;
        }
        if (master == true && !rhs.master) {
            return -1;
        }
        if (!master && rhs.master) {
            return 1;
        }
        if (date < rhs.date) {
            return -1;
        } else if (date > rhs.date) {
            return 1;
        } else {
            return 0;
        }
    }
}
