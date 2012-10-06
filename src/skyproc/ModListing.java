package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A more accurate representation of a modname: A combination of mod name
 * without the suffix and its master flag. Skyrim.esm, for example, would be
 * "Skyrim" with a true master flag.
 *
 * @author Justin Swanson
 */
public class ModListing extends SubRecord implements Comparable {

    static private Type[] types = {Type.MAST, Type.DATA};
    SubString mast = new SubString(Type.MAST, true);
    SubData data = new SubData(Type.DATA);
    boolean master = false;
    static ModListing skyrim = new ModListing("Skyrim.esm");
    static ModListing update = new ModListing("Update.esm");

    ModListing(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	this();
	parseData(in);
    }

    /**
     * ModListing objects are used to uniquely identify mods via name and master
     * tag.
     *
     * @param name The name to give to a mod. Eg. "Skyrim" (with no suffix)
     * @param master The master tag. (.esp or .esm)
     */
    public ModListing(String name, Boolean master) {
	this(name);
	this.master = master;
    }

    /**
     *
     * @param nameWithSuffix String containing the modname AND suffix. Eg
     * "Skyrim.esm"
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
     * Prints the mod name and appropriate suffix (.esp or .esm)
     *
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
    public String toString() {
	return print();
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	String tmp = mast.string;
	mast.string = print();
	mast.export(out, srcMod);
	mast.string = tmp;
	data.export(out, srcMod);
    }

    @Override
    final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
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
     * Checks if the modname's are equal (case ignored), and the master tags are
     * the same.
     *
     * @param obj Another ModListing
     * @return True if modname's are equal (case ignored), and the master tags
     * are the same.
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

    /**
     * Compare funtion is as follows:<br> 1) A master always is less than a
     * non-master plugin<br> 2) The global patch always is greater<br> 3) A
     * plugin that is on the active plugins list via importActivePlugins() comes
     * before a plugin that was created manually. <br> 4) Remaining plugins are
     * ordered in the same order they were created in the code.
     *
     * @param o Another ModListing.
     * @return Whether this modlisting is >/==/< the parameter.
     */
    @Override
    public int compareTo(Object o) {
	ModListing rhs = (ModListing) o;
	if (equals(rhs)) {
	    return 0;
	}
	if (master && !rhs.master) {
	    return -1;
	}
	if (!master && rhs.master) {
	    return 1;
	}
	if (SPGlobal.getGlobalPatch() != null) {
	    if (equals(SPGlobal.getGlobalPatch().getInfo())) {
		return 1;
	    }
	    if (rhs.equals(SPGlobal.getGlobalPatch().getInfo())) {
		return -1;
	    }
	}
	if (equals(skyrim)) {
	    return -1;
	}
	if (rhs.equals(skyrim)) {
	    return 1;
	}
	if (equals(update)) {
	    return -1;
	}
	if (rhs.equals(update)) {
	    return 1;
	}
	boolean thisActive = SPDatabase.activePlugins.contains(this);
	boolean rhsActive = SPDatabase.activePlugins.contains(rhs);
	if (thisActive) {
	    if (!rhsActive) {
		return -1;
	    } else {
		int comp = SPDatabase.activePlugins.indexOf(this) - SPDatabase.activePlugins.indexOf(rhs);
		if (comp != 0) {
		    return comp;
		} else {
		    return 1;
		}
	    }
	} else {
	    if (rhsActive) {
		return 1;
	    } else {
		int comp = SPGlobal.getDB().addedPlugins.indexOf(this) - SPGlobal.getDB().addedPlugins.indexOf(rhs);
		if (comp != 0) {
		    return comp;
		} else {
		    return 1;
		}
	    }
	}
    }
}